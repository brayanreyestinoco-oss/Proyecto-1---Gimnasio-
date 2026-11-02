package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.bd.ConexionBD;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.reportes.ReporteFila;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReporteRepositoryJdbc implements ReporteRepository {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String CONSULTA_BASE =
            "SELECT s.nombre AS socio, s.cedula, p.nombre AS plan, "
                    + "m.fecha_inicio, m.fecha_vencimiento, m.activa, "
                    + "p.tipo, p.precio_base, p.descuento_pct, p.recargo, "
                    + "(SELECT pago_ultimo.metodo_pago "
                    + "FROM pago pago_ultimo "
                    + "WHERE pago_ultimo.id_membresia = m.id_membresia "
                    + "ORDER BY pago_ultimo.fecha_pago DESC, pago_ultimo.id_pago DESC "
                    + "LIMIT 1) AS metodo_pago "
                    + "FROM membresia m "
                    + "LEFT JOIN socio s ON s.id_socio = m.id_socio "
                    + "LEFT JOIN plan p ON p.id_plan = m.id_plan ";

    @Override
    public List<ReporteFila> listarTodas() throws SQLException {
        String sql = CONSULTA_BASE + "ORDER BY m.id_membresia";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultados = sentencia.executeQuery()) {
            return leerFilas(resultados);
        }
    }

    @Override
    public List<ReporteFila> consultarProximasVencer(LocalDate desde, LocalDate hasta)
            throws SQLException {
        String sql = CONSULTA_BASE
                + "WHERE m.activa = TRUE AND m.fecha_vencimiento BETWEEN ? AND ? "
                + "ORDER BY m.fecha_vencimiento, m.id_membresia";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setDate(1, Date.valueOf(desde));
            sentencia.setDate(2, Date.valueOf(hasta));
            try (ResultSet resultados = sentencia.executeQuery()) {
                return leerFilas(resultados);
            }
        }
    }

    private List<ReporteFila> leerFilas(ResultSet resultados) throws SQLException {
        List<ReporteFila> filas = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        while (resultados.next()) {
            LocalDate fechaInicio = fecha(resultados, "fecha_inicio");
            LocalDate fechaVencimiento = fecha(resultados, "fecha_vencimiento");
            boolean activa = resultados.getBoolean("activa");
            String tipo = resultados.getString("tipo");
            BigDecimal precioBase = resultados.getBigDecimal("precio_base");
            BigDecimal descuento = resultados.getBigDecimal("descuento_pct");
            BigDecimal recargo = resultados.getBigDecimal("recargo");

            String estado;
            if (!activa) {
                estado = "Inactiva";
            } else if (fechaVencimiento == null) {
                estado = "-";
            } else {
                estado = fechaVencimiento.isBefore(hoy) ? "Vencida" : "Vigente";
            }

            filas.add(new ReporteFila(
                    texto(resultados.getString("socio")),
                    texto(resultados.getString("cedula")),
                    texto(resultados.getString("plan")),
                    formatear(fechaInicio),
                    formatear(fechaVencimiento),
                    estado,
                    renovacionAutomatica(tipo),
                    costoRenovacion(tipo, precioBase, descuento, recargo),
                    texto(resultados.getString("metodo_pago"))
            ));
        }
        return filas;
    }

    private LocalDate fecha(ResultSet resultados, String columna) throws SQLException {
        Date fecha = resultados.getDate(columna);
        return fecha == null ? null : fecha.toLocalDate();
    }

    private String formatear(LocalDate fecha) {
        return fecha == null ? "-" : fecha.format(FORMATO_FECHA);
    }

    private String texto(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    private String renovacionAutomatica(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return "-";
        }
        return "ANUAL".equalsIgnoreCase(tipo) ? "Sí" : "No";
    }

    private String costoRenovacion(String tipo, BigDecimal precioBase,
                                   BigDecimal descuento, BigDecimal recargo) {
        if (tipo == null || precioBase == null) {
            return "-";
        }

        BigDecimal costo;
        if ("ANUAL".equalsIgnoreCase(tipo)) {
            if (descuento == null) {
                return "-";
            }
            costo = precioBase.multiply(
                    BigDecimal.ONE.subtract(descuento.movePointLeft(2)));
        } else if ("VIP".equalsIgnoreCase(tipo)) {
            if (recargo == null) {
                return "-";
            }
            costo = precioBase.add(recargo);
        } else if ("MENSUAL".equalsIgnoreCase(tipo)) {
            costo = precioBase;
        } else {
            return "-";
        }

        return String.format(Locale.ROOT, "₡%,.0f", costo.doubleValue());
    }
}
