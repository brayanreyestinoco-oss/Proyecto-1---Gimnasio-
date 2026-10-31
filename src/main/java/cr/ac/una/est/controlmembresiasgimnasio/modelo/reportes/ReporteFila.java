package cr.ac.una.est.controlmembresiasgimnasio.modelo.reportes;

public class ReporteFila {

    private final String socio;
    private final String cedula;
    private final String plan;
    private final String fechaInicio;
    private final String fechaVencimiento;
    private final String estado;
    private final String renovacionAutomatica;
    private final String costoRenovacion;
    private final String metodoPago;

    public ReporteFila(String socio, String cedula, String plan, String fechaInicio,
                       String fechaVencimiento, String estado, String renovacionAutomatica,
                       String costoRenovacion, String metodoPago) {
        this.socio = socio;
        this.cedula = cedula;
        this.plan = plan;
        this.fechaInicio = fechaInicio;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
        this.renovacionAutomatica = renovacionAutomatica;
        this.costoRenovacion = costoRenovacion;
        this.metodoPago = metodoPago;
    }

    public String getSocio() {
        return socio;
    }

    public String getCedula() {
        return cedula;
    }

    public String getPlan() {
        return plan;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public String getRenovacionAutomatica() {
        return renovacionAutomatica;
    }

    public String getCostoRenovacion() {
        return costoRenovacion;
    }

    public String getMetodoPago() {
        return metodoPago;
    }
}
