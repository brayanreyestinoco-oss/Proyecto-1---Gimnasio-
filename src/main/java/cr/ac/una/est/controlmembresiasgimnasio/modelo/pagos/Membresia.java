package cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;

import java.time.LocalDate;

/**
 * Representa la membresía de un socio del gimnasio.
 * Relaciona un socio con un plan y mantiene
 * las fechas de inicio y vencimiento.
 */
public class Membresia {

    private int idMembresia;

    private Socio socio;

    private Plan plan;

    private LocalDate fechaInicio;

    private LocalDate fechaVencimiento;

    private boolean activa;

    /**
     * Crea una nueva membresía.
     *
     * La membresía se crea inicialmente inactiva.
     * Se activa cuando el socio realiza su primer pago.
     *
     * @param idMembresia identificador de la membresía
     * @param socio socio propietario de la membresía
     * @param plan plan contratado
     * @param fechaInicio fecha indicada para iniciar
     */
    public Membresia(
            int idMembresia,
            Socio socio,
            Plan plan,
            LocalDate fechaInicio) {

        this.idMembresia = idMembresia;

        this.socio = socio;

        this.plan = plan;

        this.fechaInicio = fechaInicio;

        /*
         * Todavía no existe vencimiento
         * porque aún no se ha realizado un pago.
         */
        this.fechaVencimiento = null;

        /*
         * La membresía comienza inactiva.
         * El pago será el encargado de activarla.
         */
        this.activa = false;
    }

    /**
     * Obtiene el identificador de la membresía.
     *
     * @return identificador de la membresía
     */
    public int getIdMembresia() {

        return idMembresia;
    }

    /**
     * Cambia el identificador de la membresía.
     *
     * @param idMembresia nuevo identificador
     */
    public void setIdMembresia(
            int idMembresia) {

        this.idMembresia =
                idMembresia;
    }

    /**
     * Obtiene el socio relacionado
     * con la membresía.
     *
     * @return socio de la membresía
     */
    public Socio getSocio() {

        return socio;
    }

    /**
     * Cambia el socio de la membresía.
     *
     * @param socio nuevo socio
     */
    public void setSocio(
            Socio socio) {

        this.socio =
                socio;
    }

    /**
     * Obtiene el plan contratado.
     *
     * @return plan de la membresía
     */
    public Plan getPlan() {

        return plan;
    }

    /**
     * Cambia el plan de la membresía.
     *
     * @param plan nuevo plan
     */
    public void setPlan(
            Plan plan) {

        this.plan =
                plan;
    }

    /**
     * Obtiene la fecha de inicio.
     *
     * @return fecha de inicio
     */
    public LocalDate getFechaInicio() {

        return fechaInicio;
    }

    /**
     * Cambia la fecha de inicio.
     *
     * @param fechaInicio nueva fecha
     */
    public void setFechaInicio(
            LocalDate fechaInicio) {

        this.fechaInicio =
                fechaInicio;
    }

    /**
     * Obtiene la fecha de vencimiento.
     *
     * @return fecha de vencimiento
     */
    public LocalDate getFechaVencimiento() {

        return fechaVencimiento;
    }

    /**
     * Cambia la fecha de vencimiento.
     *
     * @param fechaVencimiento nueva fecha
     */
    public void setFechaVencimiento(
            LocalDate fechaVencimiento) {

        this.fechaVencimiento =
                fechaVencimiento;
    }

    /**
     * Indica si la membresía está activa.
     *
     * @return true si está activa
     */
    public boolean isActiva() {

        return activa;
    }

    /**
     * Cambia el estado de la membresía.
     *
     * @param activa nuevo estado
     */
    public void setActiva(
            boolean activa) {

        this.activa =
                activa;
    }

    /**
     * Verifica si la membresía está vigente
     * en una fecha determinada.
     *
     * Para estar vigente debe:
     *
     * 1. Estar activa.
     * 2. Tener fecha de vencimiento.
     * 3. No haber vencido.
     *
     * @param fecha fecha que se desea verificar
     * @return true si la membresía está vigente
     */
    public boolean estaVigente(
            LocalDate fecha) {

        if (!activa) {

            return false;
        }

        if (fechaVencimiento == null) {

            return false;
        }

        return !fecha.isAfter(
                fechaVencimiento
        );
    }
}