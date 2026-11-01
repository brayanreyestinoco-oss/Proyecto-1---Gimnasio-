package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.accesos.RegistroAcceso;
import cr.ac.una.est.controlmembresiasgimnasio.repository.AccesoRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Consulta del historial de entradas al gimnasio.
 */
public class HistorialAccesoService {

    private final AccesoRepository accesoRepository =
            new AccesoRepository();

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