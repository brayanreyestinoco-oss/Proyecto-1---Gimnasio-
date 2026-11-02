package cr.ac.una.est.controlmembresiasgimnasio.modelo.planes;

/**
 * Clase abstracta que representa un plan de membresía del gimnasio.
 * Sirve como base para PlanMensual, PlanAnual y PlanVIP.
 */
public abstract class Plan {

    // Identificador generado por MySQL (0 mientras el plan no se ha guardado).
    private int idPlan;
    private String nombre;
    private double precioBase;
    private String beneficios;

    /**
     * Crea un plan nuevo (aún sin guardar en la base de datos).
     */
    public Plan(String nombre, double precioBase, String beneficios) {
        this(0, nombre, precioBase, beneficios);
    }

    /**
     * Crea un plan con identificador, usado al reconstruirlo desde MySQL.
     */
    public Plan(int idPlan, String nombre, double precioBase, String beneficios) {
        this.idPlan = idPlan;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.beneficios = beneficios;
    }

    /** Obtiene el identificador del plan (0 si aún no se ha guardado). */
    public int getIdPlan() {
        return idPlan;
    }

    /** Asigna el identificador generado por MySQL. */
    public void setIdPlan(int idPlan) {
        this.idPlan = idPlan;
    }

    /** Obtiene el nombre del plan. */
    public String getNombre() {
        return nombre;
    }

    /** Obtiene el precio base sin ajustes (descuentos o recargos). */
    public double getPrecioBase() {
        return precioBase;
    }

    /** Describe los beneficios incluidos en el plan. */
    public String getBeneficios() {
        return beneficios;
    }

    /**
     * Código del tipo de plan tal como se guarda en la columna "tipo"
     * de MySQL: MENSUAL, ANUAL o VIP.
     */
    public abstract String getTipo();

    /**
     * Valor del ajuste propio de cada tipo: descuento en porcentaje (anual),
     * recargo en colones (VIP) o 0 (mensual).
     */
    public abstract double getAjuste();

    /** Calcula el precio final del plan en colones. */
    public abstract double calcularPrecioFinal();

    /** Obtiene la duración del plan en meses, usada para calcular vencimientos. */
    public abstract int getDuracionEnMeses();
}