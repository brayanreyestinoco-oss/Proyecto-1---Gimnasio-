package cr.ac.una.est.controlmembresiasgimnasio.modelo.planes;

import cr.ac.una.est.controlmembresiasgimnasio.interfaces.IRenovable;

/**
 * Representa un plan mensual básico del gimnasio.
 */
public class PlanMensual extends Plan implements IRenovable {

    /**
     * Crea un plan mensual con un precio base fijo.
     */
    public PlanMensual() {
        super("Mensual", 15000);
    }

    /**
     * Calcula el precio final del plan mensual (sin ajustes).
     * @return precio final en colones
     */
    @Override
    public double calcularPrecioFinal() {
        return getPrecioBase();
    }

    /**
     * El plan mensual dura 1 mes.
     * @return 1
     */
    @Override
    public int getDuracionEnMeses() {
        return 1;
    }

    /**
     * Describe los beneficios del plan mensual.
     * @return texto de beneficios
     */
    @Override
    public String getBeneficios() {
        return "Acceso al gimnasio en horario regular";
    }

    /**
     * El plan mensual no se renueva automáticamente.
     * @return false
     */
    @Override
    public boolean esRenovableAutomaticamente() {
        return false;
    }

    /**
     * El costo de renovar es igual al precio final.
     * @return costo de renovación
     */
    @Override
    public double costoRenovacion() {
        return calcularPrecioFinal();
    }
}