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

/**
 * Acceso a las tablas de accesos:
 * <ul>
 *   <li>acceso_historial: solo quienes ENTRARON al gimnasio.</li>
 *   <li>consulta_membresia: consultas del estado de una membresía e
 *       intentos de entrada denegados.</li>
 * </ul>
 */
public class AccesoRepository {

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

        String sql = "INSERT INTO acceso_historial (id_socio) VALUES (?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idSocio);
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
                + "(id_socio, cedula_consultada, vigente, estado) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            if (idSocio == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, idSocio);
            }

            ps.setString(2, recortar(cedula, 20));
            ps.setBoolean(3, vigente);
            ps.setString(4, recortar(estado, 100));

            ps.executeUpdate();
        }
    }

    /**
     * Lista quién entró, de la entrada más reciente a la más antigua.
     * El "detalle" es el plan del socio. Usa idx_acceso_fecha.
     *
     * @param desde  solo entradas desde esa fecha y hora (null = todas)
     * @param limite máximo de filas
     */
    public List<RegistroAcceso> listar(LocalDateTime desde, int limite)
            throws SQLException {

        String sql = "SELECT a.fecha_hora, s.nombre, s.cedula, "
                + "TRUE AS resultado, p.nombre AS detalle "
                + "FROM acceso_historial a "
                + "INNER JOIN socio s ON a.id_socio = s.id_socio "
                + "LEFT JOIN membresia m ON m.id_socio = s.id_socio "
                + "LEFT JOIN plan p ON p.id_plan = m.id_plan "
                + (desde != null ? "WHERE a.fecha_hora >= ? " : "")
                + "ORDER BY a.fecha_hora DESC, a.id_acceso DESC "
                + "LIMIT ?";

        return consultar(sql, desde, limite);
    }

    /**
     * Lista las consultas de membresía e intentos denegados, de la más
     * reciente a la más antigua. El "detalle" es el estado.
     */
    public List<RegistroAcceso> listarConsultas(
            LocalDateTime desde, int limite) throws SQLException {

        String sql = "SELECT a.fecha_hora, s.nombre, "
                + "a.cedula_consultada AS cedula, "
                + "a.vigente AS resultado, a.estado AS detalle "
                + "FROM consulta_membresia a "
                + "LEFT JOIN socio s ON a.id_socio = s.id_socio "
                + (desde != null ? "WHERE a.fecha_hora >= ? " : "")
                + "ORDER BY a.fecha_hora DESC, a.id_consulta DESC "
                + "LIMIT ?";

        return consultar(sql, desde, limite);
    }

    private List<RegistroAcceso> consultar(
            String sql, LocalDateTime desde, int limite)
            throws SQLException {

        List<RegistroAcceso> registros = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            int indice = 1;

            if (desde != null) {
                ps.setTimestamp(indice++, Timestamp.valueOf(desde));
            }

            ps.setInt(indice, limite);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String detalle = rs.getString("detalle");

                    registros.add(new RegistroAcceso(
                            rs.getTimestamp("fecha_hora").toLocalDateTime(),
                            rs.getString("nombre"),
                            rs.getString("cedula"),
                            rs.getBoolean("resultado"),
                            detalle == null ? "-" : detalle));
                }
            }
        }

        return registros;
    }

    private static String recortar(String texto, int maximo) {
        return texto.length() <= maximo
                ? texto : texto.substring(0, maximo);
    }
}