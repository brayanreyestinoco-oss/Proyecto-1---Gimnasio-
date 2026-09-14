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

    // Identificador único de la membresía.
    private int idMembresia;

    // Socio al que pertenece la membresía.
    private Socio socio;

    // Plan que tiene contratado el socio.
    private Plan plan;

    // Fecha en la que comienza la membresía.
    private LocalDate fechaInicio;

    // Fecha hasta la cual estará vigente la membresía.
    private LocalDate fechaVencimiento;

    // Indica si la membresía se encuentra activa.
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

        // Guarda el identificador recibido para la membresía.
        this.idMembresia = idMembresia;

        // Guarda el socio asociado a la membresía.
        this.socio = socio;

        // Guarda el plan contratado por el socio.
        this.plan = plan;

        // Guarda la fecha establecida para iniciar la membresía.
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

        // Retorna el identificador de la membresía.
        return idMembresia;
    }

    /**
     * Cambia el identificador de la membresía.
     *
     * @param idMembresia nuevo identificador
     */
    public void setIdMembresia(
            int idMembresia) {

        // Actualiza el identificador de la membresía.
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

        // Retorna el socio relacionado con la membresía.
        return socio;
    }

    /**
     * Cambia el socio de la membresía.
     *
     * @param socio nuevo socio
     */
    public void setSocio(
            Socio socio) {

        // Actualiza el socio relacionado con la membresía.
        this.socio =
                socio;
    }

    /**
     * Obtiene el plan contratado.
     *
     * @return plan de la membresía
     */
    public Plan getPlan() {

        // Retorna el plan contratado.
        return plan;
    }

    /**
     * Cambia el plan de la membresía.
     *
     * @param plan nuevo plan
     */
    public void setPlan(
            Plan plan) {

        // Actualiza el plan asociado a la membresía.
        this.plan =
                plan;
    }

    /**
     * Obtiene la fecha de inicio.
     *
     * @return fecha de inicio
     */
    public LocalDate getFechaInicio() {

        // Retorna la fecha de inicio de la membresía.
        return fechaInicio;
    }

    /**
     * Cambia la fecha de inicio.
     *
     * @param fechaInicio nueva fecha
     */
    public void setFechaInicio(
            LocalDate fechaInicio) {

        // Actualiza la fecha de inicio.
        this.fechaInicio =
                fechaInicio;
    }

    /**
     * Obtiene la fecha de vencimiento.
     *
     * @return fecha de vencimiento
     */
    public LocalDate getFechaVencimiento() {

        // Retorna la fecha de vencimiento.
        return fechaVencimiento;
    }

    /**
     * Cambia la fecha de vencimiento.
     *
     * @param fechaVencimiento nueva fecha
     */
    public void setFechaVencimiento(
            LocalDate fechaVencimiento) {

        // Actualiza la fecha de vencimiento de la membresía.
        this.fechaVencimiento =
                fechaVencimiento;
    }

    /**
     * Indica si la membresía está activa.
     *
     * @return true si está activa
     */
    public boolean isActiva() {

        // Retorna el estado actual de la membresía.
        return activa;
    }

    /**
     * Cambia el estado de la membresía.
     *
     * @param activa nuevo estado
     */
    public void setActiva(
            boolean activa) {

        // Actualiza el estado de la membresía.
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

        // Si la membresía no está activa, no puede estar vigente.
        if (!activa) {

            return false;
        }

        // Si no existe una fecha de vencimiento, no se puede considerar vigente.
        if (fechaVencimiento == null) {

            return false;
        }

        // Comprueba que la fecha indicada no sea posterior a la fecha de vencimiento.
        return !fecha.isAfter(
                fechaVencimiento
        );
    }
}