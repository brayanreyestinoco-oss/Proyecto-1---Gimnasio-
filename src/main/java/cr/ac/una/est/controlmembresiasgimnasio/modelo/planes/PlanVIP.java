package cr.ac.una.est.controlmembresiasgimnasio.modelo.planes;

import cr.ac.una.est.controlmembresiasgimnasio.interfaces.IRenovable;

/**
 * Representa un plan VIP con acceso a clases especiales.
 */
public class PlanVIP extends Plan implements IRenovable {

    private static final double RECARGO_VIP = 10000;

    /**
     * Crea un plan VIP con precio base mensual más el recargo VIP.
     */
    public PlanVIP() {
        super("VIP Mensual", 15000);
    }

    /**
     * Calcula el precio final sumando el recargo por beneficios VIP.
     * @return precio final en colones
     */
    @Override
    public double calcularPrecioFinal() {
        return getPrecioBase() + RECARGO_VIP;
    }

    /**
     * El plan VIP dura 1 mes, igual que el mensual, pero con más beneficios.
     * @return 1
     */
    @Override
    public int getDuracionEnMeses() {
        return 1;
    }

    /**
     * Describe los beneficios del plan VIP.
     * @return texto de beneficios
     */
    @Override
    public String getBeneficios() {
        return "Acceso al gimnasio + clases especiales + casillero personal";
    }

    /**
     * El plan VIP no se renueva automáticamente (requiere confirmación).
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
