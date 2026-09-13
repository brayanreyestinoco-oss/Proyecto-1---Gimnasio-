package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;

/**
 * Servicio encargado de verificar si un socio
 * puede ingresar al gimnasio.
 */
public class ControlAcceso {

    private PagoService pagoService;

    /**
     * Crea el servicio de control de acceso utilizando
     * el servicio de pagos y membresías del sistema.
     *
     * @param pagoService servicio que administra pagos y membresías
     */
    public ControlAcceso(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    /**
     * Busca la membresía asociada a un socio.
     *
     * @param idSocio identificador del socio
     * @return la membresía encontrada o null si no existe
     */
    public Membresia buscarMembresia(int idSocio) {
        return pagoService.buscarMembresiaPorSocio(idSocio);
    }

    /**
     * Verifica si un socio puede ingresar al gimnasio.
     * El acceso se permite cuando el socio posee
     * una membresía vigente.
     *
     * @param idSocio identificador del socio
     * @return true si puede ingresar, false si el acceso es denegado
     */
    public boolean verificarAcceso(int idSocio) {

        Membresia membresia =
                buscarMembresia(idSocio);

        if (membresia == null) {
            return false;
        }

        return pagoService.membresiaEstaVigente(membresia);
    }

    /**
     * Cambia el servicio de pagos utilizado
     * para comprobar el acceso.
     *
     * @param pagoService nuevo servicio de pagos
     */
    public void setPagoService(PagoService pagoService) {
        this.pagoService = pagoService;
    }
}