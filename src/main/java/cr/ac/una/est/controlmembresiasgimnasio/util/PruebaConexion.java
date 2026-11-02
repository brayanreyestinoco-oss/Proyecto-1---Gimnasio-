package cr.ac.una.est.controlmembresiasgimnasio.util;

import java.sql.Connection;

public class PruebaConexion {

    public static void main(String[] args) {

        try (Connection conexion = ConexionBD.obtenerConexion()) {

            System.out.println("¡Conexión exitosa con MySQL!");
            System.out.println("Base de datos: " +
                    conexion.getCatalog());

        } catch (Exception e) {

            System.out.println("Error al conectar con MySQL:");
            e.printStackTrace();
        }
    }
}