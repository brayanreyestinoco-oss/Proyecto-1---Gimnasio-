package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.bd.ConexionBD;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.accesos.RegistroAcceso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// ============================================================
// AccesoRepositoryJdbc.java
// QUÉ HACE:      Guarda y lee en MySQL los intentos de entrada (intento_acceso) y las consultas
//                (consulta_membresia).
// CONECTADO A:   ConexionBD (la base de datos), ControlAcceso (guarda) e HistorialAccesoService
//                (lee).
// ============================================================
/**
 * Implementación JDBC del contrato de acceso. El historial nuevo usa intento_acceso.
 * Se conservan las operaciones antiguas para compatibilidad:
 * <ul>
 *   <li>intento_acceso: intentos nuevos, permitidos y denegados.</li>
 *   <li>acceso_historial: entradas antiguas, conservadas sin migrar.</li>
 *   <li>consulta_membresia: consultas del estado de una membresía e
 *       intentos de entrada denegados.</li>
 * </ul>
 */
public class AccesoRepositoryJdbc implements AccesoRepository {

    /**
     * Busca el id del socio con esa cédula.
     *
     * @return el id, o null si la cédula no está registrada
     */
    public Integer buscarIdSocioPorCedula(String cedula)
            throws SQLException {

        String sql = "SELECT id_socio FROM socio WHERE cedula = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cedula);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    /**
     * Registra la entrada de un socio al gimnasio. La fecha y hora las
     * pone MySQL (CURRENT_TIMESTAMP).
     */
    public void registrarEntrada(int idSocio) throws SQLException {

        String sql = "INSERT INTO acceso_historial (id_socio, fecha_hora) VALUES (?, ?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idSocio);
            ps.setTimestamp(2, ahora());
            ps.executeUpdate();
        }
    }

    /**
     * Guarda una consulta de membresía (o un intento de entrada
     * denegado) en consulta_membresia.
     *
     * @param idSocio id del socio, o null si la cédula no existe
     */
    public void registrarConsulta(Integer idSocio, String cedula,
                                  boolean vigente, String estado)
            throws SQLException {

        String sql = "INSERT INTO consulta_membresia "
                + "(id_socio, cedula_consultada, fecha_hora, vigente, estado) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            if (idSocio == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, idSocio);
            }

            ps.setString(2, recortar(cedula, 20));
            ps.setTimestamp(3, ahora());
            ps.setBoolean(4, vigente);
            ps.setString(5, recortar(estado, 100));

            ps.executeUpdate();
        }
    }

    /**
     * Lista intentos permitidos y denegados, del más reciente al más antiguo.
     * El detalle conserva el motivo original.
     *
     * @param desde  solo entradas desde esa fecha y hora (null = todas)
     * @param limite máximo de filas
     */
    public List<RegistroAcceso> listar(LocalDateTime desde, int limite)
            throws SQLException {

        return buscar(desde, null, "", limite);
    }

    /** Consulta intentos filtrando antes de aplicar el límite; todos los valores son parámetros. */
    @Override
    public List<RegistroAcceso> buscar(LocalDateTime desde, LocalDateTime hasta,
                                       String socio, int limite) throws SQLException {
        return buscarTipo(desde, hasta, socio, limite, false);
    }

    /** Construye SQL con identificadores fijos; los filtros siempre se enlazan con parámetros. */
    private List<RegistroAcceso> buscarTipo(LocalDateTime desde, LocalDateTime hasta,
                                           String socio, int limite, boolean consulta) throws SQLException {
        if (limite < 1 || limite > 5000) throw new IllegalArgumentException("Límite inválido");
        String sql = "SELECT a.fecha_hora, s.nombre, a.cedula_consultada AS cedula, "
                + (consulta ? "a.vigente AS resultado, a.estado AS detalle "
                            : "a.permitido AS resultado, a.motivo AS detalle ")
                + (consulta ? "FROM consulta_membresia a " : "FROM intento_acceso a ")
                + "LEFT JOIN socio s ON a.id_socio = s.id_socio "
                + "WHERE (? IS NULL OR a.fecha_hora >= ?) "
                + "AND (? IS NULL OR a.fecha_hora < ?) "
                + "AND (? = '' OR a.cedula_consultada = ? OR LOCATE(?, s.nombre) > 0) "
                + (consulta ? "ORDER BY a.fecha_hora DESC, a.id_consulta DESC LIMIT ?"
                            : "ORDER BY a.fecha_hora DESC, a.id_intento DESC LIMIT ?");
        List<RegistroAcceso> filas = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            Timestamp inicio = desde == null ? null : Timestamp.valueOf(desde);
            Timestamp fin = hasta == null ? null : Timestamp.valueOf(hasta);
            ps.setTimestamp(1, inicio); ps.setTimestamp(2, inicio);
            ps.setTimestamp(3, fin); ps.setTimestamp(4, fin);
            ps.setString(5, socio); ps.setString(6, socio); ps.setString(7, socio);
            ps.setInt(8, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) filas.add(new RegistroAcceso(
                        rs.getTimestamp("fecha_hora").toLocalDateTime(),
                        rs.getString("nombre"), rs.getString("cedula"),
                        rs.getBoolean("resultado"), rs.getString("detalle")));
            }
        }
        return filas;
    }

    /**
     * Lista las consultas de membresía e intentos denegados, de la más
     * reciente a la más antigua. El "detalle" es el estado.
     */
    public List<RegistroAcceso> listarConsultas(
            LocalDateTime desde, int limite) throws SQLException {

        return buscarConsultas(desde, null, "", limite);
    }

    /** Consulta estados de membresía con filtros aplicados en MySQL. */
    @Override
    public List<RegistroAcceso> buscarConsultas(LocalDateTime desde, LocalDateTime hasta,
                                               String socio, int limite) throws SQLException {
        return buscarTipo(desde, hasta, socio, limite, true);
    }

    /** Guarda cada intento con el resultado y motivo decididos en ese momento.
     * @param idSocio socio existente, o null para una cédula desconocida
     * @param cedula cédula digitada
     * @param permitido resultado de la verificación
     * @param motivo explicación del resultado
     * @throws SQLException si no se puede persistir el intento
     */
    @Override
    public void registrarIntento(Integer idSocio, String cedula,
                                  boolean permitido, String motivo) throws SQLException {
        String sql = "INSERT INTO intento_acceso "
                + "(id_socio, cedula_consultada, fecha_hora, permitido, motivo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (idSocio == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, idSocio);
            ps.setString(2, cedula);
            ps.setTimestamp(3, ahora());
            ps.setBoolean(4, permitido);
            ps.setString(5, motivo);
            ps.executeUpdate();
        }
    }

    /** Hora actual de la aplicación; se envía a MySQL para no depender de la hora del servidor. */
    private static Timestamp ahora() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    private static String recortar(String texto, int maximo) {
        return texto.length() <= maximo
                ? texto : texto.substring(0, maximo);
    }
}