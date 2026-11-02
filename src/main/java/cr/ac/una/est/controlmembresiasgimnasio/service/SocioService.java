package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.interfaces.Gestionable;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;
import cr.ac.una.est.controlmembresiasgimnasio.repository.SocioRepository;
import cr.ac.una.est.controlmembresiasgimnasio.repository.SocioRepositoryJdbc;

import java.util.ArrayList;
import java.util.List;

public class SocioService implements Gestionable<Socio> {

    private final SocioRepository socioRepository;

    public SocioService() {
        this(new SocioRepositoryJdbc());
    }

    public SocioService(SocioRepository socioRepository) {
        if (socioRepository == null) {
            throw new IllegalArgumentException(
                    "El repositorio de socios no puede ser nulo.");
        }

        this.socioRepository = socioRepository;
    }

    @Override
    public void registrar(Socio socio) {

        validarSocio(socio);

        if (buscarPorCedula(socio.getCedula()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un socio registrado con esa cédula.");
        }

        // MySQL genera el ID y el repositorio lo asigna al objeto.
        socioRepository.guardar(socio);
    }

    @Override
    public Socio buscar(int id) {
        return socioRepository.buscarPorId(id).orElse(null);
    }

    public Socio buscarPorCedula(String cedula) {

        if (cedula == null || cedula.trim().isEmpty()) {
            return null;
        }

        return socioRepository.buscarPorCedula(
                cedula.trim()
        ).orElse(null);
    }

    @Override
    public boolean modificar(Socio socio) {

        validarSocio(socio);

        if (socio.getIdSocio() <= 0) {
            throw new IllegalArgumentException(
                    "El socio no tiene un identificador válido.");
        }

        Socio socioMismaCedula =
                buscarPorCedula(socio.getCedula());

        if (socioMismaCedula != null
                && socioMismaCedula.getIdSocio() != socio.getIdSocio()) {

            throw new IllegalArgumentException(
                    "Ya existe otro socio registrado con esa cédula.");
        }

        return socioRepository.actualizar(socio);
    }

    @Override
    public boolean eliminar(int id) {
        return socioRepository.eliminar(id);
    }

    public ArrayList<Socio> getSocios() {
        List<Socio> socios = socioRepository.listarTodos();
        return new ArrayList<>(socios);
    }

    private void validarSocio(Socio socio) {

        if (socio == null) {
            throw new IllegalArgumentException(
                    "Los datos del socio no pueden estar vacíos.");
        }

        if (vacio(socio.getCedula())
                || vacio(socio.getNombre())
                || vacio(socio.getTelefono())
                || vacio(socio.getCorreo())
                || vacio(socio.getContactoEmergencia())
                || vacio(socio.getTelefonoEmergencia())) {

            throw new IllegalArgumentException(
                    "Debe completar todos los campos obligatorios.");
        }
    }

    private boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}