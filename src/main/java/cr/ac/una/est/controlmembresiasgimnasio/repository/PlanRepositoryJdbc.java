package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.bd.ConexionBD;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanAnual;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanMensual;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanVIP;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// ============================================================
// PlanRepositoryJdbc.java
// QUÉ HACE:      Lee y escribe la tabla plan en MySQL y convierte cada fila en PlanMensual,
//                PlanAnual o PlanVIP.
// CONECTADO A:   ConexionBD y los modelos de plan. Lo usa PlanService.
// ============================================================
/**
 * Implementación JDBC de PlanRepository sobre la tabla "plan" de MySQL.
 * Todas las consultas con datos variables usan PreparedStatement.
 */
public class PlanRepositoryJdbc implements PlanRepository {

    private static final String COLUMNAS =
            "id_plan, tipo, nombre, precio_base, descuento_pct, recargo, beneficios";

    // Inserta un plan nuevo.
    @Override
    public void guardar(Plan plan) throws SQLException {
        String sql = "INSERT INTO plan "
                + "(tipo, nombre, precio_base, descuento_pct, recargo, beneficios) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            asignarDatos(ps, plan);
            ps.executeUpdate();

            // Recupera el id generado por MySQL.
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    plan.setIdPlan(claves.getInt(1));
                }
            }
        }
    }

    // Busca un plan por su id.
    @Override
    public Plan buscarPorId(int idPlan) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM plan WHERE id_plan = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPlan);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? reconstruir(rs) : null;
            }
        }
    }

    // Busca un plan por su nombre.
    @Override
    public Plan buscarPorNombre(String nombre) throws SQLException {
        // La colación de la columna no distingue mayúsculas/minúsculas.
        String sql = "SELECT " + COLUMNAS + " FROM plan WHERE nombre = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? reconstruir(rs) : null;
            }
        }
    }

    // Lista todos los planes.
    @Override
    public List<Plan> listar() throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM plan ORDER BY id_plan";
        List<Plan> planes = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                planes.add(reconstruir(rs));
            }
        }
        return planes;
    }

    // Modifica un plan existente.
    @Override
    public boolean actualizar(Plan plan) throws SQLException {
        String sql = "UPDATE plan SET nombre = ?, precio_base = ?, "
                + "descuento_pct = ?, recargo = ?, beneficios = ? "
                + "WHERE id_plan = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, plan.getNombre());
            ps.setDouble(2, plan.getPrecioBase());
            ps.setDouble(3, descuentoDe(plan));
            ps.setDouble(4, recargoDe(plan));
            ps.setString(5, plan.getBeneficios());
            ps.setInt(6, plan.getIdPlan());

            return ps.executeUpdate() > 0;
        }
    }

    // Borra un plan por su id.
    @Override
    public boolean eliminar(int idPlan) throws SQLException {
        String sql = "DELETE FROM plan WHERE id_plan = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPlan);
            return ps.executeUpdate() > 0;
        }
    }

    // Parámetros del INSERT: tipo, nombre, precio, descuento, recargo, beneficios.
    private void asignarDatos(PreparedStatement ps, Plan plan) throws SQLException {
        ps.setString(1, plan.getTipo());
        ps.setString(2, plan.getNombre());
        ps.setDouble(3, plan.getPrecioBase());
        ps.setDouble(4, descuentoDe(plan));
        ps.setDouble(5, recargoDe(plan));
        ps.setString(6, plan.getBeneficios());
    }

    // Solo el plan anual guarda descuento; los demás guardan 0.
    private double descuentoDe(Plan plan) {
        return plan instanceof PlanAnual anual ? anual.getDescuentoPorcentaje() : 0;
    }

    // Solo el plan VIP guarda recargo; los demás guardan 0.
    private double recargoDe(Plan plan) {
        return plan instanceof PlanVIP vip ? vip.getRecargo() : 0;
    }

    /**
     * Reconstruye la subclase correcta de Plan según la columna "tipo".
     * Así PlanMensual, PlanAnual y PlanVIP conservan su comportamiento
     * aunque los datos vengan de MySQL.
     */
    private Plan reconstruir(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_plan");
        String tipo = rs.getString("tipo");
        String nombre = rs.getString("nombre");
        double precioBase = rs.getDouble("precio_base");
        double descuento = rs.getDouble("descuento_pct");
        double recargo = rs.getDouble("recargo");
        String beneficios = rs.getString("beneficios");

        return switch (tipo) {
            case PlanMensual.TIPO -> new PlanMensual(id, nombre, precioBase, beneficios);
            case PlanAnual.TIPO -> new PlanAnual(id, nombre, precioBase, descuento, beneficios);
            case PlanVIP.TIPO -> new PlanVIP(id, nombre, precioBase, recargo, beneficios);
            default -> throw new SQLException("Tipo de plan desconocido: " + tipo);
        };
    }
}