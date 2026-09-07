package cr.ac.una.est.controlmembresiasgimnasio.modelo.planes;

/**
 * Clase abstracta que representa un plan de membresía del gimnasio.
 * Sirve como base para PlanMensual, PlanAnual y PlanVIP.
 */
public abstract class Plan {
    private String nombre;
    private double precioBase;

    /**
     * Crea un plan con nombre y precio base.
     * @param nombre nombre del plan (ej. "Mensual")
     * @param precioBase precio base sin ajustes
     */
    public Plan(String nombre, double precioBase) {
        this.nombre = nombre;
        this.precioBase = precioBase;
    }

    /**
     * Obtiene el nombre del plan.
     * @return nombre del plan
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Calcula el precio final del plan; cada subclase decide si aplica
     * algún ajuste (ej. descuento anual).
     * @return precio final en colones
     */
    public abstract double calcularPrecioFinal();

    /**
     * Obtiene la duración del plan en meses, usada para calcular vencimientos.
     * @return cantidad de meses que dura el plan
     */
    public abstract int getDuracionEnMeses();

    /**
     * Describe los beneficios incluidos en el plan.
     * @return texto describiendo los beneficios
     */
    public abstract String getBeneficios();

    /**
     * Obtiene el precio base sin ajustes, para uso interno de las subclases.
     * @return precio base
     */
    protected double getPrecioBase() {
        return precioBase;
    }
}
