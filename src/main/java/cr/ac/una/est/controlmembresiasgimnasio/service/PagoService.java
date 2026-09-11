package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.Pago;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.Socio;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Servicio encargado de administrar los pagos y las membresías
 * de los socios del gimnasio.
 */
public class PagoService {

    private ArrayList<Pago> pagos;
    private ArrayList<Membresia> membresias;

    /**
     * Crea el servicio de pagos e inicializa las colecciones.
     *
     * @return no retorna ningún valor
     */
    public PagoService() {
        pagos = new ArrayList<>();
        membresias = new ArrayList<>();
    }

    /**
     * Obtiene la lista de pagos registrados.
     *
     * @return lista de pagos
     */
    public ArrayList<Pago> getPagos() {
        return pagos;
    }

    /**
     * Obtiene la lista de membresías registradas.
     *
     * @return lista de membresías
     */
    public ArrayList<Membresia> getMembresias() {
        return membresias;
    }

    /**
     * Registra una nueva membresía para un socio.
     *
     * @param idMembresia identificador de la membresía
     * @param socio socio que adquiere la membresía
     * @param plan plan seleccionado
     * @param fechaInicio fecha de inicio de la membresía
     * @return la nueva membresía creada
     */
    public Membresia registrarMembresia(int idMembresia,
                                        Socio socio,
                                        Plan plan,
                                        LocalDate fechaInicio) {

        if (socio == null) {
            throw new IllegalArgumentException(
                    "El socio no puede ser nulo.");
        }

        if (plan == null) {
            throw new IllegalArgumentException(
                    "El plan no puede ser nulo.");
        }

        if (fechaInicio == null) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser nula.");
        }

        if (buscarMembresiaPorSocio(socio.getIdSocio()) != null) {
            throw new IllegalArgumentException(
                    "El socio ya tiene una membresía registrada.");
        }

        Membresia membresia = new Membresia(
                idMembresia,
                socio,
                plan,
                fechaInicio
        );

        membresias.add(membresia);

        return membresia;
    }

    /**
     * Registra un pago y actualiza la membresía correspondiente.
     *
     * @param idPago identificador del pago
     * @param membresia membresía que recibe el pago
     * @param monto monto pagado
     * @param fechaPago fecha en la que se realiza el pago
     * @param metodoPago método utilizado para pagar
     * @return el pago registrado
     */
    public Pago registrarPago(int idPago,
                              Membresia membresia,
                              double monto,
                              LocalDate fechaPago,
                              String metodoPago) {

        validarDatosPago(membresia, monto, fechaPago, metodoPago);

        actualizarVencimiento(membresia, fechaPago);

        Pago pago = new Pago(
                idPago,
                membresia,
                monto,
                fechaPago,
                metodoPago
        );

        pagos.add(pago);

        return pago;
    }

    /**
     * Busca una membresía utilizando el identificador del socio.
     *
     * @param idSocio identificador del socio que se desea buscar
     * @return membresía encontrada o null si no existe
     */
    public Membresia buscarMembresiaPorSocio(int idSocio) {

        for (Membresia membresia : membresias) {

            if (membresia.getSocio().getIdSocio() == idSocio) {
                return membresia;
            }
        }

        return null;
    }

    /**
     * Busca una membresía utilizando su identificador.
     *
     * @param idMembresia identificador de la membresía
     * @return membresía encontrada o null si no existe
     */
    public Membresia buscarMembresia(int idMembresia) {

        for (Membresia membresia : membresias) {

            if (membresia.getIdMembresia() == idMembresia) {
                return membresia;
            }
        }

        return null;
    }

    /**
     * Actualiza la fecha de vencimiento de una membresía
     * tomando en cuenta la duración del plan.
     *
     * @param membresia membresía que se desea actualizar
     * @param fechaPago fecha en la que se realizó el pago
     * @return no retorna ningún valor
     */
    private void actualizarVencimiento(Membresia membresia,
                                       LocalDate fechaPago) {

        LocalDate fechaBase;

        if (membresia.getFechaVencimiento() != null
                && !membresia.getFechaVencimiento().isBefore(fechaPago)) {

            fechaBase = membresia.getFechaVencimiento();

        } else {

            fechaBase = fechaPago;
            membresia.setFechaInicio(fechaPago);
        }

        LocalDate nuevoVencimiento = fechaBase.plusMonths(
                membresia.getPlan().getDuracionEnMeses()
        );

        membresia.setFechaVencimiento(nuevoVencimiento);
        membresia.setActiva(true);
    }

    /**
     * Valida los datos necesarios para registrar un pago.
     *
     * @param membresia membresía relacionada con el pago
     * @param monto monto que se desea registrar
     * @param fechaPago fecha del pago
     * @param metodoPago método utilizado para pagar
     * @return no retorna ningún valor
     */
    private void validarDatosPago(Membresia membresia,
                                  double monto,
                                  LocalDate fechaPago,
                                  String metodoPago) {

        if (membresia == null) {
            throw new IllegalArgumentException(
                    "La membresía no puede ser nula.");
        }

        if (monto <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero.");
        }

        if (fechaPago == null) {
            throw new IllegalArgumentException(
                    "La fecha del pago no puede ser nula.");
        }

        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe indicar el método de pago.");
        }
    }

    /**
     * Verifica si una membresía está vigente en la fecha actual.
     *
     * @param membresia membresía que se desea verificar
     * @return true si está vigente, false si está vencida
     */
    public boolean membresiaEstaVigente(Membresia membresia) {

        if (membresia == null) {
            return false;
        }

        return membresia.estaVigente(LocalDate.now());
    }

    /**
     * Verifica si el socio tiene una membresía vigente.
     *
     * @param idSocio identificador del socio
     * @return true si tiene acceso vigente, false en caso contrario
     */
    public boolean socioTieneAcceso(int idSocio) {

        Membresia membresia = buscarMembresiaPorSocio(idSocio);

        return membresia != null
                && membresiaEstaVigente(membresia);
    }
}