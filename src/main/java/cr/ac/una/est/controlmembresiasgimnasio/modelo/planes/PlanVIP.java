package cr.ac.una.est.controlmembresiasgimnasio.modelo.planes;

import cr.ac.una.est.controlmembresiasgimnasio.interfaces.IRenovable;

// ============================================================
// PlanVIP.java
// QUÉ HACE:      Plan mensual con un recargo extra por beneficios VIP (clases, casillero).
// CONECTADO A:   Hijo de Plan; usado por PlanService y PlanRepositoryJdbc.
// ============================================================
/**
 * Representa un plan VIP con acceso a clases especiales.
 */
public class PlanVIP extends Plan implements IRenovable {

    /** Código con el que se guarda este tipo en MySQL. */
    public static final String TIPO = "VIP";

    // Recargo en colones por los beneficios VIP.
    private final double recargo;

    /**
     * Crea un plan VIP con los valores originales del sistema:
     * precio mensual de 15000 más 10000 de recargo.
     */
    public PlanVIP() {
        this("VIP Mensual", 15000, 10000,
                "Acceso al gimnasio + clases especiales + casillero personal");
    }

    /** Crea un plan VIP nuevo (sin guardar). */
    public PlanVIP(String nombre, double precioBase,
                   double recargo, String beneficios) {
        super(nombre, precioBase, beneficios);
        this.recargo = recargo;
    }

    /** Reconstruye un plan VIP desde los datos almacenados. */
    public PlanVIP(int idPlan, String nombre, double precioBase,
                   double recargo, String beneficios) {
        super(idPlan, nombre, precioBase, beneficios);
        this.recargo = recargo;
    }

    /** Obtiene el recargo VIP en colones. */
    public double getRecargo() {
        return recargo;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    /** El ajuste del plan VIP es su recargo en colones. */
    @Override
    public double getAjuste() {
        return recargo;
    }

    /** Calcula el precio final sumando el recargo VIP. */
    @Override
    public double calcularPrecioFinal() {
        return getPrecioBase() + recargo;
    }

    /** El plan VIP dura 1 mes. */
    @Override
    public int getDuracionEnMeses() {
        return 1;
    }

    /** El plan VIP no se renueva automáticamente. */
    @Override
    public boolean esRenovableAutomaticamente() {
        return false;
    }

    @Override
    public double costoRenovacion() {
        return calcularPrecioFinal();
    }
}