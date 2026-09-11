package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.interfaces.Gestionable;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;

import java.util.ArrayList;


public class SocioService implements Gestionable<Socio> {

    private ArrayList<Socio> socios;


    public SocioService() {
        socios = new ArrayList<>();
    }


    @Override
    public void registrar(Socio socio) {
        socios.add(socio);
    }


    @Override
    public Socio buscar(int id) {
        for (Socio socio : socios) {
            if (socio.getIdSocio() == id) {
                return socio;
            }
        }

        return null;
    }


    @Override
    public boolean modificar(Socio socio) {

        Socio socioExistente = buscar(socio.getIdSocio());

        if (socioExistente != null) {

            socioExistente.setNombre(socio.getNombre());
            socioExistente.setTelefono(socio.getTelefono());
            socioExistente.setCorreo(socio.getCorreo());
            socioExistente.setContactoEmergencia(socio.getContactoEmergencia());
            socioExistente.setTelefonoEmergencia(socio.getTelefonoEmergencia());
            socioExistente.setCondicionesMedicas(socio.getCondicionesMedicas());

            return true;
        }

        return false;
    }


    @Override
    public boolean eliminar(int id) {

        Socio socio = buscar(id);

        if (socio != null) {
            socios.remove(socio);
            return true;
        }

        return false;
    }
}