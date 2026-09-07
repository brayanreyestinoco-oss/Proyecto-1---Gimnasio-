package cr.ac.una.est.controlmembresiasgimnasio.modelo.planes;

import cr.ac.una.est.controlmembresiasgimnasio.interfaces.IRenovable;

/**
 * Representa un plan anual con descuento respecto al precio mensual.
 */
public class PlanAnual extends Plan implements IRenovable {

    private static final double DESCUENTO = 0.15; // 15% de descuento

    /**
     * Crea un plan anual con un precio base fijo (antes de descuento).
     */
    public PlanAnual() {
        super("Anual", 15000 * 12);
    }

    /**
     * Calcula el precio final aplicando el descuento anual.
     * @return precio final en colones
     */
    @Override
    public double calcularPrecioFinal() {
        return getPrecioBase() * (1 - DESCUENTO);
    }

    /**
     * El plan anual dura 12 meses.
     * @return 12
     */
    @Override
    public int getDuracionEnMeses() {
        return 12;
    }

    /**
     * Describe los beneficios del plan anual.
     * @return texto de beneficios
     */
    @Override
    public String getBeneficios() {
        return "Acceso al gimnasio + 15% de descuento por pago anual";
    }

    /**
     * El plan anual se renueva automáticamente al vencer.
     * @return true
     */
    @Override
    public boolean esRenovableAutomaticamente() {
        return true;
    }

    /**
     * El costo de renovar es igual al precio final con descuento.
     * @return costo de renovación
     */
    @Override
    public double costoRenovacion() {
        return calcularPrecioFinal();
    }
}
