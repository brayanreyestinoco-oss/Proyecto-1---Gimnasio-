package cr.ac.una.est.controlmembresiasgimnasio;

import cr.ac.una.est.controlmembresiasgimnasio.service.PagoService;
import cr.ac.una.est.controlmembresiasgimnasio.service.PlanService;
import cr.ac.una.est.controlmembresiasgimnasio.service.SocioService;

/**
 * Clase encargada de mantener las instancias compartidas
 * de los servicios utilizados por la aplicación.
 */
public class DatosAplicacion {

    private static final SocioService socioService =
            new SocioService();

    private static final PlanService planService =
            new PlanService();

    private static final PagoService pagoService =
            new PagoService();

    /**
     * Constructor privado para evitar crear objetos
     * adicionales de esta clase.
     */
    private DatosAplicacion() {
    }

    /**
     * Obtiene el servicio compartido de socios.
     *
     * @return servicio de socios
     */
    public static SocioService getSocioService() {
        return socioService;
    }

    /**
     * Obtiene el servicio compartido de planes.
     *
     * @return servicio de planes
     */
    public static PlanService getPlanService() {
        return planService;
    }

    /**
     * Obtiene el servicio compartido de pagos y membresías.
     *
     * @return servicio de pagos
     */
    public static PagoService getPagoService() {
        return pagoService;
    }
}
