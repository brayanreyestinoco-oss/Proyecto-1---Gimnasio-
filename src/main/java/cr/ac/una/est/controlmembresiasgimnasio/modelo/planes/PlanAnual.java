package cr.ac.una.est.controlmembresiasgimnasio.modelo.planes;

import cr.ac.una.est.controlmembresiasgimnasio.interfaces.IRenovable;

// ============================================================
// PlanAnual.java
// QUÉ HACE:      Plan de 12 meses con un descuento (15%) sobre el precio base.
// CONECTADO A:   Hijo de Plan; usado por PlanService y PlanRepositoryJdbc.
// ============================================================
/**
 * Representa un plan anual con descuento respecto al precio mensual.
 */
public class PlanAnual extends Plan implements IRenovable {

    /** Código con el que se guarda este tipo en MySQL. */
    public static final String TIPO = "ANUAL";

    // Descuento en porcentaje (15 = 15%).
    private final double descuentoPorcentaje;

    /**
     * Crea un plan anual con los valores originales del sistema:
     * 12 mensualidades de 15000 con 15% de descuento.
     */
    public PlanAnual() {
        this("Anual", 15000 * 12, 15,
                "Acceso al gimnasio + 15% de descuento por pago anual");
    }

    /** Crea un plan anual nuevo (sin guardar). */
    public PlanAnual(String nombre, double precioBase,
                     double descuentoPorcentaje, String beneficios) {
        super(nombre, precioBase, beneficios);
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    /** Reconstruye un plan anual desde los datos almacenados. */
    public PlanAnual(int idPlan, String nombre, double precioBase,
                     double descuentoPorcentaje, String beneficios) {
        super(idPlan, nombre, precioBase, beneficios);
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    /** Obtiene el descuento aplicado al plan, en porcentaje. */
    public double getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    /** El ajuste del plan anual es su descuento en porcentaje. */
    @Override
    public double getAjuste() {
        return descuentoPorcentaje;
    }

    /** Calcula el precio final aplicando el descuento anual. */
    @Override
    public double calcularPrecioFinal() {
        return getPrecioBase() * (1 - descuentoPorcentaje / 100.0);
    }

    /** El plan anual dura 12 meses. */
    @Override
    public int getDuracionEnMeses() {
        return 12;
    }

    /** El plan anual se renueva automáticamente al vencer. */
    @Override
    public boolean esRenovableAutomaticamente() {
        return true;
    }

    @Override
    public double costoRenovacion() {
        return calcularPrecioFinal();
    }
}