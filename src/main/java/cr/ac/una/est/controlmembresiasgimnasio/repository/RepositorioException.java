package cr.ac.una.est.controlmembresiasgimnasio.repository;

import java.sql.SQLException;

/**
 * Excepción no verificada que envuelve un error de base de datos.
 *
 * Los servicios la lanzan para no obligar a cada controlador a atrapar
 * SQLException. Conserva el SQLException original como causa, de modo que
 * se puede consultar el código de error de MySQL con getCodigoSql().
 */
public class RepositorioException extends RuntimeException {

    /**
     * @param mensaje mensaje entendible para el usuario
     * @param causa error SQL original
     */
    public RepositorioException(String mensaje, SQLException causa) {
        super(mensaje, causa);
    }

    /**
     * Código de error de MySQL (ej. 1062 = valor duplicado,
     * 1451 = registro referenciado por otra tabla).
     *
     * @return código de error SQL, o 0 si no se conoce
     */
    public int getCodigoSql() {
        return getCause() instanceof SQLException sql ? sql.getErrorCode() : 0;
    }
}