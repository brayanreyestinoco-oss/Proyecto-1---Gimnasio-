package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.reportes.ReporteFila;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

// ============================================================
// ReporteRepository.java
// QUÉ HACE:      Lista de consultas del reporte de membresías (todas, por vencer y ya vencidas).
// CONECTADO A:   La implementa ReporteRepositoryJdbc; la usa ReporteService.
// ============================================================
public interface ReporteRepository {

    List<ReporteFila> listarTodas() throws SQLException;

    List<ReporteFila> consultarProximasVencer(LocalDate desde, LocalDate hasta)
            throws SQLException;

    List<ReporteFila> consultarVencidas(LocalDate hoy) throws SQLException;
}
