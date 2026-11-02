package cr.ac.una.est.controlmembresiasgimnasio.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://mysql-28e8e88b-est-3642.h.aivencloud.com:15134/defaultdb"
                    + "?sslMode=REQUIRED"
                    + "&connectTimeout=8000"
                    + "&serverTimezone=America/Costa_Rica";

    private static final String USUARIO =
            System.getenv().getOrDefault("GIMNASIO_DB_USER", "avnadmin");

    private static final String PASSWORD =
            System.getenv().getOrDefault("GIMNASIO_DB_PASSWORD", "");

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
