package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;
import java.util.List;
import java.util.Optional;

public interface SocioRepository {

    Socio guardar(Socio socio);

    List<Socio> listarTodos();

    Optional<Socio> buscarPorId(int id);

    Optional<Socio> buscarPorCedula(String cedula);

    boolean actualizar(Socio socio);

    boolean eliminar(int id);
}
