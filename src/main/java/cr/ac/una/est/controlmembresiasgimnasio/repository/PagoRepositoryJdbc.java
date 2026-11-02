package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.bd.ConexionBD;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Pago;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// ============================================================
// PagoRepositoryJdbc.java
// QUÉ HACE:      Lee y escribe la tabla pago en MySQL. Guarda dentro de una transacción que le pasa
//                PagoService.
// CONECTADO A:   ConexionBD y los modelos Pago y Membresía. Lo usa PagoService.
// ============================================================
/**
 * Implementación JDBC del repositorio de pagos.
 *
 * Permite guardar, consultar, modificar y eliminar
 * registros de la tabla pago de MySQL.
 *
 * Utiliza la conexión compartida del proyecto.
 */
public class PagoRepositoryJdbc implements PagoRepository {

    // Repositorio que permite recuperar las membresías
    // relacionadas con los pagos.
    private final MembresiaRepository membresiaRepository;

    /**
     * Constructor del repositorio.
     */
    public PagoRepositoryJdbc() {
        this.membresiaRepository =
                new MembresiaRepositoryJdbc();
    }

    /**
     * Guarda un nuevo pago en MySQL.
     *
     * La conexión es recibida desde el servicio
     * para permitir transacciones con otras operaciones.
     *
     * Este método NO cierra la conexión recibida.
     */
    @Override
    public void guardar(Pago pago, Connection conexion)
            throws SQLException {

        validarPago(pago);

        String sql =
                "INSERT INTO pago "
                        + "(id_membresia, monto, fecha_pago, metodo_pago) "
                        + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps =
                     conexion.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            asignarDatos(ps, pago);

            int filas = ps.executeUpdate();

            if (filas != 1) {
                throw new SQLException(
                        "No se pudo registrar el pago.");
            }

            // Recuperar el ID generado por MySQL.
            try (ResultSet claves = ps.getGeneratedKeys()) {

                if (claves.next()) {
                    pago.setIdPago(claves.getInt(1));
                } else {
                    throw new SQLException(
                            "MySQL no devolvió el ID del pago.");
                }
            }
        }
    }

    /**
     * Busca un pago utilizando su identificador.
     */
    @Override
    public Pago buscarPorId(int idPago)
            throws SQLException {

        String sql =
                "SELECT id_pago, id_membresia, monto, "
                        + "fecha_pago, metodo_pago "
                        + "FROM pago "
                        + "WHERE id_pago = ?";

        try (Connection conexion =
                     ConexionBD.obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idPago);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return reconstruirPago(rs);
                }
            }
        }

        return null;
    }

    /**
     * Obtiene todos los pagos registrados.
     */
    @Override
    public List<Pago> listar()
            throws SQLException {

        List<Pago> pagos = new ArrayList<>();

        String sql =
                "SELECT id_pago, id_membresia, monto, "
                        + "fecha_pago, metodo_pago "
                        + "FROM pago "
                        + "ORDER BY fecha_pago DESC, id_pago DESC";

        try (Connection conexion =
                     ConexionBD.obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                pagos.add(reconstruirPago(rs));
            }
        }

        return pagos;
    }

    /**
     * Busca los pagos relacionados con una membresía.
     */
    @Override
    public List<Pago> buscarPorMembresia(int idMembresia)
            throws SQLException {

        List<Pago> pagos = new ArrayList<>();

        String sql =
                "SELECT id_pago, id_membresia, monto, "
                        + "fecha_pago, metodo_pago "
                        + "FROM pago "
                        + "WHERE id_membresia = ? "
                        + "ORDER BY fecha_pago DESC, id_pago DESC";

        try (Connection conexion =
                     ConexionBD.obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idMembresia);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    pagos.add(reconstruirPago(rs));
                }
            }
        }

        return pagos;
    }

    /**
     * Actualiza los datos de un pago existente.
     *
     * La conexión se recibe desde el servicio
     * y no se cierra dentro de este método.
     */
    @Override
    public boolean actualizar(
            Pago pago,
            Connection conexion) throws SQLException {

        validarPago(pago);

        if (pago.getIdPago() <= 0) {
            throw new SQLException(
                    "El pago debe tener un ID válido.");
        }

        String sql =
                "UPDATE pago SET "
                        + "id_membresia = ?, "
                        + "monto = ?, "
                        + "fecha_pago = ?, "
                        + "metodo_pago = ? "
                        + "WHERE id_pago = ?";

        try (PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            asignarDatos(ps, pago);

            ps.setInt(5, pago.getIdPago());

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un pago por su identificador.
     */
    @Override
    public boolean eliminar(int idPago)
            throws SQLException {

        String sql =
                "DELETE FROM pago WHERE id_pago = ?";

        try (Connection conexion =
                     ConexionBD.obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idPago);

            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Asigna los valores utilizados en INSERT y UPDATE.
     */
    private void asignarDatos(
            PreparedStatement ps,
            Pago pago) throws SQLException {

        ps.setInt(
                1,
                pago.getMembresia().getIdMembresia());

        ps.setDouble(2, pago.getMonto());

        ps.setDate(
                3,
                Date.valueOf(pago.getFechaPago()));

        ps.setString(4, pago.getMetodoPago());
    }

    /**
     * Comprueba que el pago tenga los datos
     * mínimos necesarios antes de guardarlo.
     */
    private void validarPago(Pago pago)
            throws SQLException {

        if (pago == null) {
            throw new SQLException(
                    "El pago no puede ser nulo.");
        }

        if (pago.getMembresia() == null
                || pago.getMembresia().getIdMembresia() <= 0) {
            throw new SQLException(
                    "El pago necesita una membresía guardada.");
        }

        if (!Double.isFinite(pago.getMonto())
                || pago.getMonto() <= 0) {
            throw new SQLException(
                    "El monto del pago debe ser mayor que cero.");
        }

        if (pago.getFechaPago() == null) {
            throw new SQLException(
                    "La fecha del pago es obligatoria.");
        }

        if (pago.getMetodoPago() == null
                || pago.getMetodoPago().isBlank()) {
            throw new SQLException(
                    "El método de pago es obligatorio.");
        }

        if (pago.getMetodoPago().length() > 30) {
            throw new SQLException(
                    "El método de pago no puede superar 30 caracteres.");
        }
    }

    /**
     * Convierte una fila de MySQL en un objeto Pago.
     *
     * Recupera también la membresía relacionada
     * mediante MembresiaRepositoryJdbc.
     */
    private Pago reconstruirPago(ResultSet rs)
            throws SQLException {

        int idMembresia =
                rs.getInt("id_membresia");

        Membresia membresia =
                membresiaRepository.buscarPorId(idMembresia);

        if (membresia == null) {
            throw new SQLException(
                    "No se encontró la membresía del pago: "
                            + idMembresia);
        }

        Date fecha = rs.getDate("fecha_pago");

        if (fecha == null) {
            throw new SQLException(
                    "El pago no tiene una fecha válida.");
        }

        return new Pago(
                rs.getInt("id_pago"),
                membresia,
                rs.getDouble("monto"),
                fecha.toLocalDate(),
                rs.getString("metodo_pago")
        );
    }
}