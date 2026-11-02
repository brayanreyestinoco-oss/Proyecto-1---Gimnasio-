
package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;
import cr.ac.una.est.controlmembresiasgimnasio.util.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SocioRepositoryJdbc implements SocioRepository {

    private static final String SQL_INSERTAR =
            "INSERT INTO socio (cedula, nombre, telefono, correo, " +
                    "contacto_emergencia, telefono_emergencia, condiciones_medicas) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_LISTAR =
            "SELECT id_socio, cedula, nombre, telefono, correo, " +
                    "contacto_emergencia, telefono_emergencia, condiciones_medicas " +
                    "FROM socio ORDER BY id_socio";

    private static final String SQL_BUSCAR_ID =
            "SELECT id_socio, cedula, nombre, telefono, correo, " +
                    "contacto_emergencia, telefono_emergencia, condiciones_medicas " +
                    "FROM socio WHERE id_socio = ?";

    private static final String SQL_BUSCAR_CEDULA =
            "SELECT id_socio, cedula, nombre, telefono, correo, " +
                    "contacto_emergencia, telefono_emergencia, condiciones_medicas " +
                    "FROM socio WHERE cedula = ?";

    private static final String SQL_ACTUALIZAR =
            "UPDATE socio SET cedula = ?, nombre = ?, telefono = ?, " +
                    "correo = ?, contacto_emergencia = ?, telefono_emergencia = ?, " +
                    "condiciones_medicas = ? WHERE id_socio = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM socio WHERE id_socio = ?";

    @Override
    public Socio guardar(Socio socio) {

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(
                     SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            establecerDatos(ps, socio);

            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    socio.setIdSocio(claves.getInt(1));
                    return socio;
                }
            }

            throw new IllegalStateException(
                    "No se pudo recuperar el ID generado para el socio.");

        } catch (SQLException e) {
            throw convertirError(e);
        }
    }

    @Override
    public List<Socio> listarTodos() {

        List<Socio> socios = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                socios.add(mapearSocio(rs));
            }

            return socios;

        } catch (SQLException e) {
            throw convertirError(e);
        }
    }

    @Override
    public Optional<Socio> buscarPorId(int id) {

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(SQL_BUSCAR_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearSocio(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw convertirError(e);
        }
    }

    @Override
    public Optional<Socio> buscarPorCedula(String cedula) {

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps =
                     conexion.prepareStatement(SQL_BUSCAR_CEDULA)) {

            ps.setString(1, cedula);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearSocio(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw convertirError(e);
        }
    }

    @Override
    public boolean actualizar(Socio socio) {

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            establecerDatos(ps, socio);
            ps.setInt(8, socio.getIdSocio());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw convertirError(e);
        }
    }

    @Override
    public boolean eliminar(int id) {

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(SQL_ELIMINAR)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new IllegalStateException(
                        "No se puede eliminar el socio porque tiene registros relacionados, " +
                                "como una membresía, pagos o historial de acceso.", e);
            }

            throw convertirError(e);
        }
    }

    private void establecerDatos(PreparedStatement ps, Socio socio)
            throws SQLException {

        ps.setString(1, socio.getCedula());
        ps.setString(2, socio.getNombre());
        ps.setString(3, socio.getTelefono());
        ps.setString(4, socio.getCorreo());
        ps.setString(5, socio.getContactoEmergencia());
        ps.setString(6, socio.getTelefonoEmergencia());
        ps.setString(7, socio.getCondicionesMedicas() == null
                ? "" : socio.getCondicionesMedicas());
    }

    private Socio mapearSocio(ResultSet rs) throws SQLException {

        Socio socio = new Socio(
                rs.getString("cedula"),
                rs.getString("nombre"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getString("contacto_emergencia"),
                rs.getString("telefono_emergencia"),
                rs.getString("condiciones_medicas")
        );

        socio.setIdSocio(rs.getInt("id_socio"));

        return socio;
    }

    private RuntimeException convertirError(SQLException e) {

        if (e.getErrorCode() == 1062) {
            return new IllegalArgumentException(
                    "Ya existe un socio registrado con esa cédula.", e);
        }

        return new IllegalStateException(
                "Error al trabajar con la base de datos: " + e.getMessage(), e);
    }
}
