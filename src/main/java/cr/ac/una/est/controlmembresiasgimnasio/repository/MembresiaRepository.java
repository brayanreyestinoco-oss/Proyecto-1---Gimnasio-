package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz encargada de definir las operaciones
 * de persistencia de las membresías del gimnasio.
 *
 * Su implementación utilizará JDBC y MySQL.
 */
public interface MembresiaRepository {

    /**
     * Guarda una nueva membresía en la base de datos.
     *
     * Recibe una conexión para poder participar
     * en una transacción cuando sea necesario.
     *
     * @param membresia membresía que se desea guardar
     * @param conexion conexión activa a la base de datos
     * @throws SQLException si ocurre un error de SQL
     */
    void guardar(Membresia membresia, Connection conexion)
            throws SQLException;

    /**
     * Busca una membresía mediante su identificador.
     *
     * @param idMembresia identificador de la membresía
     * @return membresía encontrada o null
     * @throws SQLException si ocurre un error de SQL
     */
    Membresia buscarPorId(int idMembresia)
            throws SQLException;

    /**
     * Busca la membresía asociada a un socio.
     *
     * @param idSocio identificador del socio
     * @return membresía encontrada o null
     * @throws SQLException si ocurre un error de SQL
     */
    Membresia buscarPorSocio(int idSocio)
            throws SQLException;
    Membresia buscarPorCedula(String cedula)
            throws SQLException;

    /**
     * Obtiene las membresías registradas.
     *
     * @return lista de membresías
     * @throws SQLException si ocurre un error de SQL
     */
    List<Membresia> listar()
            throws SQLException;

    /**
     * Actualiza una membresía existente.
     *
     * @param membresia membresía con los datos actualizados
     * @param conexion conexión activa
     * @return true si se actualizó algún registro
     * @throws SQLException si ocurre un error de SQL
     */
    boolean actualizar(Membresia membresia, Connection conexion)
            throws SQLException;

    /**
     * Elimina una membresía por su identificador.
     *
     * @param idMembresia identificador de la membresía
     * @return true si se eliminó algún registro
     * @throws SQLException si ocurre un error de SQL
     */
    boolean eliminar(int idMembresia)
            throws SQLException;
}