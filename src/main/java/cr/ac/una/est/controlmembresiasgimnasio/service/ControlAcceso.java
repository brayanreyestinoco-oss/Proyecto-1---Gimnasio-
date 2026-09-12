package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.Pago;

import java.time.LocalDate;
import java.util.List;

public class ControlAcceso {

    private List<Membresia> membresias;
    private List<Pago> pagos;

    /**
     * Crea el servicio encargado de verificar el acceso al gimnasio.
     *
     * @param membresias lista de membresías registradas en el sistema
     * @param pagos lista de pagos registrados en el sistema
     */
    public ControlAcceso(List<Membresia> membresias, List<Pago> pagos) {
        this.membresias = membresias;
        this.pagos = pagos;
    }

    /**
     * Busca una membresía asociada al número de un socio.
     *
     * @param idSocio identificador del socio que se desea buscar
     * @return la membresía encontrada o null si no existe
     */
    public Membresia buscarMembresia(int idSocio) {

        for (Membresia membresia : membresias) {

            if (membresia.getSocio() != null
                    && membresia.getSocio().getIdSocio() == idSocio) {

                return membresia;
            }
        }

        return null;
    }

    /**
     * Verifica si existe un pago registrado para una membresía.
     *
     * @param membresia membresía que se desea consultar
     * @return true si existe un pago registrado, false si no existe
     */
    public boolean tienePagoRegistrado(Membresia membresia) {

        for (Pago pago : pagos) {

            if (pago.getMembresia() != null
                    && pago.getMembresia().getIdMembresia()
                    == membresia.getIdMembresia()) {

                return true;
            }
        }

        return false;
    }

    /**
     * Verifica si un socio puede ingresar al gimnasio.
     * Para permitir el acceso debe existir una membresía asociada
     * al socio, la membresía debe estar vigente y debe existir
     * un pago registrado para dicha membresía.
     *
     * @param idSocio identificador del socio que desea ingresar
     * @return true si el acceso está permitido, false si está denegado
     */
    public boolean verificarAcceso(int idSocio) {

        Membresia membresia = buscarMembresia(idSocio);

        if (membresia == null) {
            return false;
        }

        LocalDate fechaActual = LocalDate.now();

        if (!membresia.estaVigente(fechaActual)) {
            return false;
        }

        if (!tienePagoRegistrado(membresia)) {
            return false;
        }

        return true;
    }

    /**
     * Modifica la lista de membresías utilizada por el control de acceso.
     *
     * @param membresias nueva lista de membresías
     */
    public void setMembresias(List<Membresia> membresias) {
        this.membresias = membresias;
    }

    /**
     * Modifica la lista de pagos utilizada por el control de acceso.
     *
     * @param pagos nueva lista de pagos
     */
    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }
}