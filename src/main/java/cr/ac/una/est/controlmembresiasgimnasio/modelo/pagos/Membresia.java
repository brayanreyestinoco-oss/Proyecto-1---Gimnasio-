package cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;

import java.time.LocalDate;

/**
 * Representa la membresía que tiene un socio del gimnasio.s
 * Una membresía relaciona un socio con un plan y mantiene
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
     * @param idMembresia identificador de la membresía
     * @param socio socio al que pertenece la membresía
     * @param plan plan contratado por el socio
     * @param fechaInicio fecha en la que inicia la membresía
     */
    public Membresia(int idMembresia, Socio socio, Plan plan,
                     LocalDate fechaInicio) {

        this.idMembresia = idMembresia;
        this.socio = socio;
        this.plan = plan;
        this.fechaInicio = fechaInicio;

        // Calcula automáticamente el vencimiento según la duración del plan.
        this.fechaVencimiento =
                fechaInicio.plusMonths(plan.getDuracionEnMeses());

        this.activa = true;
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
     * Modifica el identificador de la membresía.
     *
     * @param idMembresia nuevo identificador
     * @return no retorna ningún valor
     */
    public void setIdMembresia(int idMembresia) {
        this.idMembresia = idMembresia;
    }

    /**
     * Obtiene el socio asociado a la membresía.
     *
     * @return socio de la membresía
     */
    public Socio getSocio() {
        return socio;
    }

    /**
     * Modifica el socio asociado a la membresía.
     *
     * @param socio nuevo socio
     * @return no retorna ningún valor
     */
    public void setSocio(Socio socio) {
        this.socio = socio;
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
     * Modifica el plan de la membresía.
     *
     * @param plan nuevo plan
     * @return no retorna ningún valor
     */
    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    /**
     * Obtiene la fecha de inicio de la membresía.
     *
     * @return fecha de inicio
     */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Modifica la fecha de inicio de la membresía.
     *
     * @param fechaInicio nueva fecha de inicio
     * @return no retorna ningún valor
     */
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
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
     * Modifica la fecha de vencimiento.
     *
     * @param fechaVencimiento nueva fecha de vencimiento
     * @return no retorna ningún valor
     */
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * Indica si la membresía está activa.
     *
     * @return true si está activa, false si está inactiva
     */
    public boolean isActiva() {
        return activa;
    }

    /**
     * Cambia el estado de la membresía.
     *
     * @param activa nuevo estado de la membresía
     * @return no retorna ningún valor
     */
    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    /**
     * Verifica si la membresía se encuentra vigente en una fecha.
     *
     * @param fecha fecha que se desea verificar
     * @return true si la membresía está vigente, false si está vencida
     */
    public boolean estaVigente(LocalDate fecha) {
        return activa && !fecha.isAfter(fechaVencimiento);
    }
}