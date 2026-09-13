package cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;

import java.time.LocalDate;

/**
 * Representa un pago realizado por un socio
 * para una membresía del gimnasio.
 */
public class Pago {

    private int idPago;
    private Membresia membresia;
    private double monto;
    private LocalDate fechaPago;
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

        this.idPago = idPago;
        this.membresia = membresia;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.metodoPago = metodoPago;
    }

    /**
     * Obtiene el identificador del pago.
     *
     * @return identificador del pago
     */
    public int getIdPago() {
        return idPago;
    }

    /**
     * Modifica el identificador del pago.
     *
     * @param idPago nuevo identificador
     * @return no retorna ningún valor
     */
    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    /**
     * Obtiene la membresía relacionada con el pago.
     *
     * @return membresía del pago
     */
    public Membresia getMembresia() {
        return membresia;
    }

    /**
     * Modifica la membresía relacionada con el pago.
     *
     * @param membresia nueva membresía
     * @return no retorna ningún valor
     */
    public void setMembresia(Membresia membresia) {
        this.membresia = membresia;
    }

    /**
     * Obtiene el monto del pago.
     *
     * @return monto pagado
     */
    public double getMonto() {
        return monto;
    }

    /**
     * Modifica el monto del pago.
     *
     * @param monto nuevo monto
     * @return no retorna ningún valor
     */
    public void setMonto(double monto) {
        this.monto = monto;
    }

    /**
     * Obtiene la fecha en la que se realizó el pago.
     *
     * @return fecha del pago
     */
    public LocalDate getFechaPago() {
        return fechaPago;
    }

    /**
     * Modifica la fecha del pago.
     *
     * @param fechaPago nueva fecha del pago
     * @return no retorna ningún valor
     */
    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    /**
     * Obtiene el método utilizado para realizar el pago. s
     *
     * @return método de pago
     */
    public String getMetodoPago() {
        return metodoPago;
    }

    /**
     * Modifica el método utilizado para realizar el pago.
     *
     * @param metodoPago nuevo método de pago
     * @return no retorna ningún valor
     */
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
}