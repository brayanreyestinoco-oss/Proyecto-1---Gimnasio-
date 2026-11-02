package cr.ac.una.est.controlmembresiasgimnasio;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// ============================================================
// Pantalla.java
// QUÉ HACE:      Cambia de una pantalla a otra dentro de la misma ventana, sin que se encoja.
// CONECTADO A:   La usan todos los controladores de vista al abrir o volver a una pantalla.
// ============================================================
/**
 * Utilidad para cambiar de pantalla manteniendo la ventana
 * maximizada. En lugar de crear una escena nueva (lo que haría
 * que la ventana recupere el tamaño de la escena), se reemplaza
 * únicamente el contenido de la escena actual.
 */
public final class Pantalla {

    private Pantalla() {
    }

    /**
     * Muestra el contenido indicado en la ventana, a pantalla completa.
     *
     * @param stage ventana en la que se mostrará la pantalla
     * @param root  contenido ya cargado desde el FXML
     */
    public static void cambiar(Stage stage, Parent root) {

        if (stage.getScene() == null) {
            stage.setScene(new Scene(root));
        } else {
            stage.getScene().setRoot(root);
        }

        if (!stage.isMaximized()) {
            stage.setMaximized(true);
        }
    }
}
