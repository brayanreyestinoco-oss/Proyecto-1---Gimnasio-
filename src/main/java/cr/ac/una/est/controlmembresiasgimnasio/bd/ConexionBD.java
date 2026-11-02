package cr.ac.una.est.controlmembresiasgimnasio.bd;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// ============================================================
// ConexionBD.java
// QUÉ HACE:      Crea la conexión a la base de datos MySQL (Aiven) leyendo db.properties o
//                variables de entorno.
// CONECTADO A:   Todos los repositorios *Jdbc piden aquí su conexión.
// ============================================================
/**
 * Entrega conexiones JDBC a la base de datos MySQL (local o en línea).
 *
 * Prioridad de configuración:
 *   1. Variables de entorno GYM_DB_URL, GYM_DB_USER y GYM_DB_PASSWORD.
 *   2. Archivo db.properties en la raíz del proyecto (no se sube al repositorio).
 *   3. Valores por defecto: MySQL local, base gimnasio_db, usuario root.
 *
 * Cada llamada abre una conexión nueva; quien la pida debe cerrarla
 * (try-with-resources).
 */
public final class ConexionBD {

    private static final String ARCHIVO_CONFIG = "db.properties";

    private static final String URL_POR_DEFECTO =
            "jdbc:mysql://localhost:3306/gimnasio_db"
                    + "?useSSL=false&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=America/Costa_Rica";

    private static final String USUARIO_POR_DEFECTO = "root";
    private static final String CLAVE_POR_DEFECTO = "";

    // Segundos máximos de espera al conectar (evita ventanas congeladas).
    private static final int TIEMPO_MAXIMO_SEGUNDOS = 8;

    private static final String url;
    private static final String usuario;
    private static final String clave;

    static {
        Properties config = new Properties();
        Path archivo = Path.of(ARCHIVO_CONFIG);

        if (Files.isReadable(archivo)) {
            try (InputStream in = Files.newInputStream(archivo)) {
                config.load(in);
            } catch (IOException e) {
                System.err.println("No se pudo leer " + ARCHIVO_CONFIG
                        + ", se usarán los valores por defecto: " + e.getMessage());
            }
        }

        url = valor("GYM_DB_URL", config.getProperty("db.url"), URL_POR_DEFECTO);
        usuario = valor("GYM_DB_USER", config.getProperty("db.user"), USUARIO_POR_DEFECTO);
        clave = valor("GYM_DB_PASSWORD", config.getProperty("db.password"), CLAVE_POR_DEFECTO);

        DriverManager.setLoginTimeout(TIEMPO_MAXIMO_SEGUNDOS);
    }

    private ConexionBD() {
    }

    // Variable de entorno > archivo > valor por defecto.
    private static String valor(String variableEntorno, String delArchivo, String porDefecto) {
        String entorno = System.getenv(variableEntorno);
        if (entorno != null && !entorno.isBlank()) {
            return entorno;
        }
        if (delArchivo != null && !delArchivo.isBlank()) {
            return delArchivo.trim();
        }
        return porDefecto;
    }

    /**
     * Abre una conexión nueva a MySQL.
     *
     * @return conexión lista para usar; el llamador debe cerrarla
     * @throws SQLException si MySQL no responde o las credenciales son incorrectas
     */
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }

    /**
     * Comprueba que se puede conectar a la base de datos.
     *
     * @return true si la conexión se abrió correctamente
     */
    public static boolean probarConexion() {
        try (Connection conexion = obtenerConexion()) {
            return conexion.isValid(3);
        } catch (SQLException e) {
            System.err.println("No se pudo conectar a MySQL: " + e.getMessage());
            return false;
        }
    }
}