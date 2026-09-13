package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.interfaces.Gestionable;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;

import java.util.ArrayList;

/**
 * Servicio encargado de administrar los socios
 * registrados en el gimnasio.
 */
public class SocioService implements Gestionable<Socio> {

    private ArrayList<Socio> socios;

    private int siguienteId;

    /**
     * Crea el servicio de socios.
     */
    public SocioService() {

        socios = new ArrayList<>();

        siguienteId = 1;
    }

    /**
     * Registra un nuevo socio.
     *
     * @param socio socio que se desea registrar
     */
    @Override
    public void registrar(Socio socio) {

        if (socio == null) {
            return;
        }

        socio.setIdSocio(siguienteId);

        siguienteId++;

        socios.add(socio);
    }

    /**
     * Busca un socio utilizando su número de socio.
     *
     * @param id número del socio
     * @return socio encontrado o null si no existe
     */
    @Override
    public Socio buscar(int id) {

        for (Socio socio : socios) {

            if (socio.getIdSocio() == id) {

                return socio;
            }
        }

        return null;
    }

    /**
     * Busca un socio utilizando su cédula.
     *
     * @param cedula cédula del socio
     * @return socio encontrado o null si no existe
     */
    public Socio buscarPorCedula(String cedula) {

        if (cedula == null) {
            return null;
        }

        for (Socio socio : socios) {

            if (socio.getCedula().equalsIgnoreCase(cedula)) {

                return socio;
            }
        }

        return null;
    }

    /**
     * Modifica los datos de un socio existente.
     *
     * @param socio socio con los nuevos datos
     * @return true si se modificó correctamente
     */
    @Override
    public boolean modificar(Socio socio) {

        if (socio == null) {
            return false;
        }

        Socio socioExistente =
                buscar(socio.getIdSocio());

        if (socioExistente != null) {

            socioExistente.setCedula(
                    socio.getCedula()
            );

            socioExistente.setNombre(
                    socio.getNombre()
            );

            socioExistente.setTelefono(
                    socio.getTelefono()
            );

            socioExistente.setCorreo(
                    socio.getCorreo()
            );

            socioExistente.setContactoEmergencia(
                    socio.getContactoEmergencia()
            );

            socioExistente.setTelefonoEmergencia(
                    socio.getTelefonoEmergencia()
            );

            socioExistente.setCondicionesMedicas(
                    socio.getCondicionesMedicas()
            );

            return true;
        }

        return false;
    }

    /**
     * Elimina un socio utilizando su número de socio.
     *
     * @param id número del socio
     * @return true si fue eliminado
     */
    @Override
    public boolean eliminar(int id) {

        Socio socio =
                buscar(id);

        if (socio != null) {

            socios.remove(socio);

            return true;
        }

        return false;
    }

    /**
     * Obtiene todos los socios registrados.
     *
     * @return lista de socios
     */
    public ArrayList<Socio> getSocios() {

        return socios;
    }
}