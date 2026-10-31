package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;

public class ControlAcceso {

    private PagoService pagoService;

    public ControlAcceso(PagoService pagoService) {

        if (pagoService == null) {
            throw new IllegalArgumentException(
                    "El servicio de pagos no puede ser nulo."
            );
        }

        this.pagoService = pagoService;
    }

    public Membresia buscarMembresia(int idSocio) {

        return pagoService.buscarMembresiaPorSocio(idSocio);
    }

    public boolean verificarAcceso(int idSocio) {

        Membresia membresia = buscarMembresia(idSocio);

        if (membresia == null) {
            return false;
        }

        return pagoService.membresiaEstaVigente(membresia);
    }

    public Membresia buscarMembresiaPorCedula(String cedula) {

        if (cedula == null || cedula.trim().isEmpty()) {
            return null;
        }

        return pagoService.buscarMembresiaPorCedula(
                cedula.trim()
        );
    }

    public boolean verificarAccesoPorCedula(String cedula) {

        Membresia membresia =
                buscarMembresiaPorCedula(cedula);

        if (membresia == null) {
            return false;
        }

        return pagoService.membresiaEstaVigente(membresia);
    }

    public void setPagoService(PagoService pagoService) {

        if (pagoService == null) {
            throw new IllegalArgumentException(
                    "El servicio de pagos no puede ser nulo."
            );
        }

        this.pagoService = pagoService;
    }
}