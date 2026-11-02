package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;
import java.util.List;
import java.util.Optional;

// ============================================================
// SocioRepository.java
// QUÉ HACE:      Lista de operaciones para guardar y leer socios.
// CONECTADO A:   La implementa SocioRepositoryJdbc; la usa SocioService.
// ============================================================
public interface SocioRepository {

    Socio guardar(Socio socio);

    List<Socio> listarTodos();

    Optional<Socio> buscarPorId(int id);

    Optional<Socio> buscarPorCedula(String cedula);

    boolean actualizar(Socio socio);

    boolean eliminar(int id);
}
