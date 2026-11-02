package cr.ac.una.est.controlmembresiasgimnasio;

import javafx.application.Application;

// ============================================================
// Launcher.java
// QUÉ HACE:      Punto de entrada del programa; solo arranca HelloApplication.
// CONECTADO A:   HelloApplication (que abre la ventana).
// ============================================================
// Clase encargada de iniciar la aplicación JavaFX.
public class Launcher {

    // Método principal que inicia la ejecución del programa.
    public static void main(String[] args) {

        // Inicia la aplicación JavaFX utilizando HelloApplication como clase principal.
        Application.launch(HelloApplication.class, args);
    }
}