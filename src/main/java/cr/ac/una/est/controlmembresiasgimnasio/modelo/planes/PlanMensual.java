package cr.ac.una.est.controlmembresiasgimnasio.modelo.planes;

import cr.ac.una.est.controlmembresiasgimnasio.interfaces.IRenovable;

/**
 * Representa un plan mensual básico del gimnasio.
 */
public class PlanMensual extends Plan implements IRenovable {

    /** Código con el que se guarda este tipo en MySQL. */
    public static final String TIPO = "MENSUAL";

    /** Crea un plan mensual con los valores originales del sistema. */
    public PlanMensual() {
        this("Mensual", 15000, "Acceso al gimnasio en horario regular");
    }

    /** Crea un plan mensual nuevo (sin guardar). */
    public PlanMensual(String nombre, double precioBase, String beneficios) {
        super(nombre, precioBase, beneficios);
    }

    /** Reconstruye un plan mensual desde los datos almacenados. */
    public PlanMensual(int idPlan, String nombre, double precioBase, String beneficios) {
        super(idPlan, nombre, precioBase, beneficios);
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    /** El plan mensual no tiene ajustes. */
    @Override
    public double getAjuste() {
        return 0;
    }

    @Override
    public double calcularPrecioFinal() {
        return getPrecioBase();
    }

    /** El plan mensual dura 1 mes. */
    @Override
    public int getDuracionEnMeses() {
        return 1;
    }

    /** El plan mensual no se renueva automáticamente. */
    @Override
    public boolean esRenovableAutomaticamente() {
        return false;
    }

    @Override
    public double costoRenovacion() {
        return calcularPrecioFinal();
    }
}