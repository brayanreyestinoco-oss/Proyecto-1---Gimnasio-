package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Pago;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz que define las operaciones de persistencia
 * para los pagos realizados por los socios del gimnasio.
 *
 * Las operaciones serán implementadas utilizando
 * JDBC y una base de datos MySQL.
 */
public interface PagoRepository {

    /**
     * Guarda un pago nuevo en la base de datos.
     *
     * Recibe una conexión para permitir que el registro
     * del pago y la actualización de la membresía
     * formen parte de una misma transacción.
     *
     * @param pago pago que se desea registrar
     * @param conexion conexión activa a la base de datos
     * @throws SQLException si ocurre un error de SQL
     */
    void guardar(Pago pago, Connection conexion)
            throws SQLException;

    /**
     * Busca un pago utilizando su identificador.
     *
     * @param idPago identificador del pago
     * @return pago encontrado o null si no existe
     * @throws SQLException si ocurre un error de SQL
     */
    Pago buscarPorId(int idPago)
            throws SQLException;

    /**
     * Obtiene todos los pagos registrados.
     *
     * @return lista de pagos
     * @throws SQLException si ocurre un error de SQL
     */
    List<Pago> listar()
            throws SQLException;

    /**
     * Obtiene los pagos asociados a una membresía.
     *
     * @param idMembresia identificador de la membresía
     * @return lista de pagos correspondientes
     * @throws SQLException si ocurre un error de SQL
     */
    List<Pago> buscarPorMembresia(int idMembresia)
            throws SQLException;

    /**
     * Actualiza los datos de un pago existente.
     *
     * @param pago pago con los datos actualizados
     * @param conexion conexión activa a la base de datos
     * @return true si se actualizó algún registro
     * @throws SQLException si ocurre un error de SQL
     */
    boolean actualizar(Pago pago, Connection conexion)
            throws SQLException;

    /**
     * Elimina un pago de la base de datos.
     *
     * @param idPago identificador del pago
     * @return true si se eliminó algún registro
     * @throws SQLException si ocurre un error de SQL
     */
    boolean eliminar(int idPago)
            throws SQLException;
}