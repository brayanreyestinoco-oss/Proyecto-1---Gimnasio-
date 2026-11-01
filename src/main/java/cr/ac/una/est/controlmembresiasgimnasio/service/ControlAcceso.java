package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.repository.AccesoRepository;

import java.sql.SQLException;

public class ControlAcceso {

    private PagoService pagoService;

    private final AccesoRepository accesoRepository =
            new AccesoRepository();

    public ControlAcceso(PagoService pagoService) {

        if (pagoService == null) {
            throw new IllegalArgumentException(
                    "El servicio de pagos no puede ser nulo."
            );
        }

        this.pagoService = pagoService;
    }

    public Membresia buscarMembresia(int idSocio) {

        return pagoService.buscarMembresiaPorSocio(idSocio);
    }

    public boolean verificarAcceso(int idSocio) {

        Membresia membresia = buscarMembresia(idSocio);

        if (membresia == null) {
            return false;
        }

        return pagoService.membresiaEstaVigente(membresia);
    }

    public Membresia buscarMembresiaPorCedula(String cedula) {

        if (cedula == null || cedula.trim().isEmpty()) {
            return null;
        }

        return pagoService.buscarMembresiaPorCedula(
                cedula.trim()
        );
    }

    public boolean verificarAccesoPorCedula(String cedula) {

        Membresia membresia =
                buscarMembresiaPorCedula(cedula);

        if (membresia == null) {
            return false;
        }

        return pagoService.membresiaEstaVigente(membresia);
    }

    /**
     * Registra una ENTRADA al gimnasio: verifica la membresía.
     * Si está vigente, la entrada se guarda en acceso_historial (solo
     * quedan quienes entraron). Si se deniega, el intento se guarda en
     * consulta_membresia para no perderlo.
     *
     * @param cedula cédula digitada
     * @return resultado de la verificación
     */
    public ResultadoAcceso registrarEntrada(String cedula) {

        ResultadoAcceso r = evaluar(cedula);

        boolean registrado = true;

        try {
            if (r.permitido()) {
                accesoRepository.registrarEntrada(r.idSocio());
            } else {
                accesoRepository.registrarConsulta(
                        r.idSocio(), limpiar(cedula), false,
                        "Entrada denegada: " + r.motivo());
            }
        } catch (SQLException e) {
            registrado = false;
        }

        return conRegistro(r, registrado);
    }

    /**
     * Consulta el estado de una membresía SIN registrar entrada. La
     * consulta queda guardada en la tabla consulta_membresia.
     *
     * @param cedula cédula digitada
     * @return estado de la membresía (permitido = vigente)
     */
    public ResultadoAcceso consultarMembresia(String cedula) {

        ResultadoAcceso r = evaluar(cedula);

        boolean registrado = true;

        try {
            accesoRepository.registrarConsulta(
                    r.idSocio(), limpiar(cedula), r.permitido(), r.motivo());
        } catch (SQLException e) {
            registrado = false;
        }

        return conRegistro(r, registrado);
    }

    /** Busca la membresía en MySQL (una consulta) y decide el estado. */
    private ResultadoAcceso evaluar(String cedula) {

        String cedulaLimpia = limpiar(cedula);

        Membresia membresia =
                pagoService.buscarMembresiaPorCedula(cedulaLimpia);

        if (membresia != null) {

            Integer idSocio = membresia.getSocio().getIdSocio();
            boolean vigente = false;
            String motivo;

            if (!membresia.isActiva()) {
                motivo = "Membresía inactiva";
            } else if (pagoService.membresiaEstaVigente(membresia)) {
                vigente = true;
                motivo = "Membresía vigente";
            } else {
                motivo = "Membresía vencida";
            }

            return new ResultadoAcceso(
                    idSocio, membresia, true, vigente, motivo, false);
        }

        Integer idSocio;

        try {
            idSocio = accesoRepository
                    .buscarIdSocioPorCedula(cedulaLimpia);
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar el socio.", e);
        }

        boolean existe = idSocio != null;

        return new ResultadoAcceso(
                idSocio, null, existe, false,
                existe ? "Sin membresía" : "Cédula no registrada",
                false);
    }

    private static ResultadoAcceso conRegistro(
            ResultadoAcceso r, boolean registrado) {

        return new ResultadoAcceso(
                r.idSocio(), r.membresia(), r.socioExiste(),
                r.permitido(), r.motivo(), registrado);
    }

    private static String limpiar(String cedula) {
        return cedula == null ? "" : cedula.trim();
    }

    public void setPagoService(PagoService pagoService) {

        if (pagoService == null) {
            throw new IllegalArgumentException(
                    "El servicio de pagos no puede ser nulo."
            );
        }

        this.pagoService = pagoService;
    }
}