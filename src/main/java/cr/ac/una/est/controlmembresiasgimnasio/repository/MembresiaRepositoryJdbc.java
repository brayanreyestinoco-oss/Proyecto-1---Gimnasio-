package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.bd.ConexionBD;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanAnual;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanMensual;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanVIP;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// ============================================================
// MembresiaRepositoryJdbc.java
// QUÉ HACE:      Lee y escribe la tabla membresia en MySQL (y arma la membresía junto con su socio
//                y su plan).
// CONECTADO A:   ConexionBD y los modelos Membresía, Socio y Plan. Lo usa PagoService.
// ============================================================
/**
 * Implementación JDBC para la tabla membresia.
 *
 * Utiliza la conexión existente de Brayan
 * y respeta el esquema SQL del gimnasio.
 */
public class MembresiaRepositoryJdbc
        implements MembresiaRepository {

    /*
     * Consulta base que reúne los datos de:
     * - membresia
     * - socio
     * - plan
     *
     * Esto permite reconstruir objetos Java completos.
     */
    private static final String CONSULTA_BASE =
            "SELECT m.id_membresia, m.id_socio, m.id_plan, "
                    + "m.fecha_inicio, m.fecha_vencimiento, m.activa, "
                    + "s.cedula, s.nombre AS socio_nombre, "
                    + "s.telefono, s.correo, "
                    + "s.contacto_emergencia, s.telefono_emergencia, "
                    + "s.condiciones_medicas, "
                    + "p.tipo, p.nombre AS plan_nombre, "
                    + "p.precio_base, p.descuento_pct, "
                    + "p.recargo, p.beneficios "
                    + "FROM membresia m "
                    + "INNER JOIN socio s ON m.id_socio = s.id_socio "
                    + "INNER JOIN plan p ON m.id_plan = p.id_plan ";

    /**
     * Guarda una membresía nueva.
     *
     * La conexión se recibe desde fuera para
     * permitir transacciones compartidas.
     */
    @Override
    public void guardar(
            Membresia membresia,
            Connection conexion) throws SQLException {

        validarMembresia(membresia);

        String sql =
                "INSERT INTO membresia "
                        + "(id_socio, id_plan, fecha_inicio, "
                        + "fecha_vencimiento, activa) "
                        + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps =
                     conexion.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            asignarDatos(ps, membresia);

            int filas = ps.executeUpdate();

            if (filas != 1) {
                throw new SQLException(
                        "No se pudo guardar la membresía.");
            }

            // Recuperar el identificador generado por MySQL.
            try (ResultSet claves = ps.getGeneratedKeys()) {

                if (claves.next()) {
                    membresia.setIdMembresia(
                            claves.getInt(1));
                } else {
                    throw new SQLException(
                            "MySQL no devolvió el ID de la membresía.");
                }
            }
        }
    }

    /**
     * Busca una membresía mediante su ID.
     */
    @Override
    public Membresia buscarPorId(int idMembresia)
            throws SQLException {

        String sql = CONSULTA_BASE
                + "WHERE m.id_membresia = ?";

        try (Connection conexion =
                     ConexionBD.obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idMembresia);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return reconstruirMembresia(rs);
                }

                return null;
            }
        }
    }

    /**
     * Busca la membresía de un socio.
     */
    @Override
    public Membresia buscarPorSocio(int idSocio)
            throws SQLException {

        String sql = CONSULTA_BASE
                + "WHERE m.id_socio = ?";

        try (Connection conexion =
                     ConexionBD.obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idSocio);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return reconstruirMembresia(rs);
                }

                return null;
            }
        }
    }

    // Busca la membresía de un socio por su cédula.
    @Override
    public Membresia buscarPorCedula(String cedula)
            throws SQLException {

        String sql = CONSULTA_BASE
                + "WHERE s.cedula = ?";

        try (Connection conexion =
                     ConexionBD.obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, cedula);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next()
                        ? reconstruirMembresia(rs)
                        : null;
            }
        }
    }
    /**
     * Recupera todas las membresías.
     */
    @Override
    public List<Membresia> listar()
            throws SQLException {

        List<Membresia> membresias = new ArrayList<>();

        String sql = CONSULTA_BASE
                + "ORDER BY m.id_membresia";

        try (Connection conexion =
                     ConexionBD.obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                membresias.add(
                        reconstruirMembresia(rs));
            }
        }

        return membresias;
    }

    /**
     * Actualiza una membresía existente.
     *
     * Usa la conexión recibida para poder
     * participar en una transacción.
     */
    @Override
    public boolean actualizar(
            Membresia membresia,
            Connection conexion) throws SQLException {

        validarMembresia(membresia);

        String sql =
                "UPDATE membresia SET "
                        + "id_socio = ?, "
                        + "id_plan = ?, "
                        + "fecha_inicio = ?, "
                        + "fecha_vencimiento = ?, "
                        + "activa = ? "
                        + "WHERE id_membresia = ?";

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            asignarDatos(ps, membresia);

            ps.setInt(6, membresia.getIdMembresia());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina una membresía por ID.
     *
     * MySQL impedirá eliminarla si existen
     * pagos asociados, debido a ON DELETE RESTRICT.
     */
    @Override
    public boolean eliminar(int idMembresia)
            throws SQLException {

        String sql =
                "DELETE FROM membresia "
                        + "WHERE id_membresia = ?";

        try (Connection conexion =
                     ConexionBD.obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idMembresia);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Asigna los cinco valores utilizados
     * por INSERT y UPDATE.
     */
    private void asignarDatos(
            PreparedStatement ps,
            Membresia membresia) throws SQLException {

        ps.setInt(
                1,
                membresia.getSocio().getIdSocio());

        ps.setInt(
                2,
                membresia.getPlan().getIdPlan());

        ps.setDate(
                3,
                Date.valueOf(membresia.getFechaInicio()));

        // El vencimiento puede ser NULL antes del primer pago.
        if (membresia.getFechaVencimiento() == null) {
            ps.setNull(4, java.sql.Types.DATE);
        } else {
            ps.setDate(
                    4,
                    Date.valueOf(
                            membresia.getFechaVencimiento()));
        }

        ps.setBoolean(5, membresia.isActiva());
    }

    /**
     * Comprueba que la membresía tenga
     * los datos mínimos necesarios.
     */
    private void validarMembresia(
            Membresia membresia) throws SQLException {

        if (membresia == null) {
            throw new SQLException(
                    "La membresía no puede ser nula.");
        }

        if (membresia.getSocio() == null
                || membresia.getSocio().getIdSocio() <= 0) {
            throw new SQLException(
                    "La membresía necesita un socio guardado.");
        }

        if (membresia.getPlan() == null
                || membresia.getPlan().getIdPlan() <= 0) {
            throw new SQLException(
                    "La membresía necesita un plan guardado.");
        }

        if (membresia.getFechaInicio() == null) {
            throw new SQLException(
                    "La fecha de inicio es obligatoria.");
        }
    }

    /**
     * Convierte una fila SQL en un objeto Membresia,
     * incluyendo su Socio y su Plan.
     */
    private Membresia reconstruirMembresia(
            ResultSet rs) throws SQLException {

        // 1. Reconstruir el socio.
        Socio socio = new Socio(
                rs.getString("cedula"),
                rs.getString("socio_nombre"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getString("contacto_emergencia"),
                rs.getString("telefono_emergencia"),
                rs.getString("condiciones_medicas")
        );

        socio.setIdSocio(rs.getInt("id_socio"));

        // 2. Reconstruir el plan correspondiente.
        Plan plan = reconstruirPlan(rs);

        // 3. Reconstruir la membresía.
        Membresia membresia = new Membresia(
                rs.getInt("id_membresia"),
                socio,
                plan,
                rs.getDate("fecha_inicio").toLocalDate()
        );

        // 4. Recuperar la fecha de vencimiento.
        Date vencimiento =
                rs.getDate("fecha_vencimiento");

        if (vencimiento != null) {
            membresia.setFechaVencimiento(
                    vencimiento.toLocalDate());

            socio.setExpiraMembrecia(
                    vencimiento.toLocalDate());
        }

        // 5. Recuperar el estado activo.
        membresia.setActiva(rs.getBoolean("activa"));

        return membresia;
    }

    /**
     * Construye el tipo de plan correspondiente
     * a la columna tipo de MySQL.
     */
    private Plan reconstruirPlan(
            ResultSet rs) throws SQLException {

        int idPlan = rs.getInt("id_plan");
        String tipo = rs.getString("tipo");
        String nombre = rs.getString("plan_nombre");
        double precioBase = rs.getDouble("precio_base");
        String beneficios = rs.getString("beneficios");

        if (tipo == null) {
            throw new SQLException(
                    "El plan no tiene un tipo válido.");
        }

        switch (tipo.toUpperCase()) {

            case "MENSUAL":
                return new PlanMensual(
                        idPlan,
                        nombre,
                        precioBase,
                        beneficios);

            case "ANUAL":
                return new PlanAnual(
                        idPlan,
                        nombre,
                        precioBase,
                        rs.getDouble("descuento_pct"),
                        beneficios);

            case "VIP":
                return new PlanVIP(
                        idPlan,
                        nombre,
                        precioBase,
                        rs.getDouble("recargo"),
                        beneficios);

            default:
                throw new SQLException(
                        "Tipo de plan desconocido: " + tipo);
        }
    }
}