package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.accesos.RegistroAcceso;
import cr.ac.una.est.controlmembresiasgimnasio.repository.AccesoRepository;
import cr.ac.una.est.controlmembresiasgimnasio.repository.AccesoRepositoryJdbc;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Consulta del historial de entradas al gimnasio.
 */
public class HistorialAccesoService {

    private final AccesoRepository accesoRepository =
            new AccesoRepositoryJdbc();

    /** @param desde inicio inclusivo o null @param hasta fin exclusivo o null
     * @param socio filtro por socio @param limite máximo de filas @return intentos filtrados */
    public List<RegistroAcceso> buscar(LocalDateTime desde, LocalDateTime hasta,
                                      String socio, int limite) {
        try {
            return accesoRepository.buscar(desde, hasta, socio, limite);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar MySQL. Revise la conexión y el script de historial.", e);
        }
    }

    /** @param desde inicio inclusivo @param hasta fin exclusivo @param socio filtro
     * @param limite máximo @return consultas filtradas */
    public List<RegistroAcceso> buscarConsultas(LocalDateTime desde, LocalDateTime hasta,
                                               String socio, int limite) {
        try {
            return accesoRepository.buscarConsultas(desde, hasta, socio, limite);
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar el historial de membresías.", e);
        }
    }

    /** Consultas de membresía (tabla consulta_membresia). */
    public List<RegistroAcceso> listarConsultas(
            LocalDateTime desde, int limite) {
        try {
            return accesoRepository.listarConsultas(desde, limite);
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar el historial de consultas.", e);
        }
    }

    /**
     * Entradas al gimnasio (tabla acceso_historial).
     *
     * @param desde  solo entradas desde esa fecha (null = todas)
     * @param limite máximo de filas a traer
     * @return las entradas más recientes primero
     */
    public List<RegistroAcceso> listar(LocalDateTime desde, int limite) {
        try {
            return accesoRepository.listar(desde, limite);
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar el historial de accesos.", e);
        }
    }
}