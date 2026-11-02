package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.reportes.ReporteFila;
import cr.ac.una.est.controlmembresiasgimnasio.repository.ReporteRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

// ============================================================
// ReporteService.java
// QUÉ HACE:      Pide al repositorio las membresías del reporte según el filtro (todas, por vencer
//                en 7/15/30 días o ya vencidas).
// CONECTADO A:   ReporteRepositoryJdbc. Lo usa ReportesController.
// ============================================================
public class ReporteService {

    private final ReporteRepository reporteRepository;

    public ReporteService(ReporteRepository reporteRepository) {
        this.reporteRepository = Objects.requireNonNull(reporteRepository);
    }

    // Membresías que ya vencieron (hasta hoy).
    public List<ReporteFila> consultarVencidas() throws SQLException {
        return reporteRepository.consultarVencidas(LocalDate.now());
    }

    // Sin días = todas; con 7, 15 o 30 = las que vencen en ese plazo.
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
