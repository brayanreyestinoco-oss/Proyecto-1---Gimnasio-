package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.reportes.ReporteFila;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface ReporteRepository {

    List<ReporteFila> listarTodas() throws SQLException;

    List<ReporteFila> consultarProximasVencer(LocalDate desde, LocalDate hasta)
            throws SQLException;
}
