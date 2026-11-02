package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;

import java.sql.SQLException;
import java.util.List;

// ============================================================
// PlanRepository.java
// QUÉ HACE:      Lista de operaciones para guardar y leer planes.
// CONECTADO A:   La implementa PlanRepositoryJdbc; la usa PlanService.
// ============================================================
/**
 * Operaciones de persistencia de los planes del gimnasio.
 * La implementación usa JDBC y MySQL.
 */
public interface PlanRepository {

    /** Guarda un plan nuevo y le asigna el id generado por MySQL. */
    void guardar(Plan plan) throws SQLException;

    /** Busca un plan por id; devuelve null si no existe. */
    Plan buscarPorId(int idPlan) throws SQLException;

    /** Busca un plan por nombre (sin distinguir mayúsculas); null si no existe. */
    Plan buscarPorNombre(String nombre) throws SQLException;

    /** Obtiene todos los planes ordenados por id. */
    List<Plan> listar() throws SQLException;

    /** Actualiza nombre, precio, ajuste y beneficios. El tipo no cambia. */
    boolean actualizar(Plan plan) throws SQLException;

    /** Elimina un plan; falla con error 1451 si hay membresías que lo usan. */
    boolean eliminar(int idPlan) throws SQLException;
}