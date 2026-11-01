
package cr.ac.una.est.controlmembresiasgimnasio.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/defaultdb?serverTimezone=UTC&useSSL=false";

    private static final String USUARIO =
            System.getenv().getOrDefault("GIMNASIO_DB_USER", "root");

    private static final String PASSWORD =
            System.getenv().getOrDefault("GIMNASIO_DB_PASSWORD", "");

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}

