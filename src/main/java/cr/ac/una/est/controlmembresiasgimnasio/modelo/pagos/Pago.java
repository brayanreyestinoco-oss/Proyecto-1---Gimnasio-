package cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;

import java.time.LocalDate;

/**
 * Representa un pago realizado por un socio
 * para una membresía del gimnasio.
 */
public class Pago {

    // Identificador único del pago.
    private int idPago;

    // Membresía a la que está asociado este pago.
    private Membresia membresia;

    // Cantidad de dinero pagada.
    private double monto;

    // Fecha en la que se realizó el pago.
    private LocalDate fechaPago;

    // Método utilizado para realizar el pago.
    private String metodoPago;

    /**
     * Crea un nuevo registro de pago.
     *
     * @param idPago identificador del pago
     * @param membresia membresía relacionada con el pago
     * @param monto monto pagado
     * @param fechaPago fecha en la que se realizó el pago
     * @param metodoPago método utilizado para realizar el pago
     */
    public Pago(int idPago, Membresia membresia, double monto,
                LocalDate fechaPago, String metodoPago) {

        // Guarda el identificador del pago.
        this.idPago = idPago;

        // Guarda la membresía relacionada con el pago.
        this.membresia = membresia;

        // Guarda el monto que fue pagado.
        this.monto = monto;

        // Guarda la fecha en la que se realizó el pago.
        this.fechaPago = fechaPago;

        // Guarda el método utilizado para realizar el pago.
        this.metodoPago = metodoPago;
    }

    /**
     * Obtiene el identificador del pago.
     *
     * @return identificador del pago
     */
    public int getIdPago() {

        // Retorna el identificador del pago.
        return idPago;
    }

    /**
     * Modifica el identificador del pago.
     *
     * @param idPago nuevo identificador
     * @return no retorna ningún valor
     */
    public void setIdPago(int idPago) {

        // Actualiza el identificador del pago.
        this.idPago = idPago;
    }

    /**
     * Obtiene la membresía relacionada con el pago.
     *
     * @return membresía del pago
     */
    public Membresia getMembresia() {

        // Retorna la membresía asociada al pago.
        return membresia;
    }

    /**
     * Modifica la membresía relacionada con el pago.
     *
     * @param membresia nueva membresía
     * @return no retorna ningún valor
     */
    public void setMembresia(Membresia membresia) {

        // Actualiza la membresía asociada al pago.
        this.membresia = membresia;
    }

    /**
     * Obtiene el monto del pago.
     *
     * @return monto pagado
     */
    public double getMonto() {

        // Retorna el monto registrado en el pago.
        return monto;
    }

    /**
     * Modifica el monto del pago.
     *
     * @param monto nuevo monto
     * @return no retorna ningún valor
     */
    public void setMonto(double monto) {

        // Actualiza el monto del pago.
        this.monto = monto;
    }

    /**
     * Obtiene la fecha en la que se realizó el pago.
     *
     * @return fecha del pago
     */
    public LocalDate getFechaPago() {

        // Retorna la fecha en la que se realizó el pago.
        return fechaPago;
    }

    /**
     * Modifica la fecha del pago.
     *
     * @param fechaPago nueva fecha del pago
     * @return no retorna ningún valor
     */
    public void setFechaPago(LocalDate fechaPago) {

        // Actualiza la fecha en la que se realizó el pago.
        this.fechaPago = fechaPago;
    }

    /**
     * Obtiene el método utilizado para realizar el pago. s
     *
     * @return método de pago
     */
    public String getMetodoPago() {

        // Retorna el método utilizado para realizar el pago.
        return metodoPago;
    }

    /**
     * Modifica el método utilizado para realizar el pago.
     *
     * @param metodoPago nuevo método de pago
     * @return no retorna ningún valor
     */
    public void setMetodoPago(String metodoPago) {

        // Actualiza el método utilizado para realizar el pago.
        this.metodoPago = metodoPago;
    }
}