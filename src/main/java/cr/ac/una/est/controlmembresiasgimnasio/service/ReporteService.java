package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.reportes.ReporteFila;
import cr.ac.una.est.controlmembresiasgimnasio.repository.ReporteRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteService(ReporteRepository reporteRepository) {
        this.reporteRepository = Objects.requireNonNull(reporteRepository);
    }

    public List<ReporteFila> consultar(Integer dias) throws SQLException {
        if (dias == null) {
            return reporteRepository.listarTodas();
        }
        if (dias != 7 && dias != 15 && dias != 30) {
            throw new IllegalArgumentException("El plazo debe ser 7, 15 o 30 días.");
        }

        LocalDate hoy = LocalDate.now();
        return reporteRepository.consultarProximasVencer(hoy, hoy.plusDays(dias));
    }
}
