package cr.ac.una.est.controlmembresiasgimnasio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal encargada de iniciar
 * la aplicación del gimnasio.
 */
public class HelloApplication extends Application {

    /**
     * Inicia la aplicación mostrando la pantalla de inicio de sesión.
     *
     * @param stage ventana principal
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @Override
    public void start(Stage stage) throws IOException {

        // Crea el cargador para obtener la vista de inicio de sesión.
        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "LoginView.fxml"
                        )
                );

        // Crea la escena utilizando la vista cargada y establece sus dimensiones.
        Scene scene =
                new Scene(
                        fxmlLoader.load(),
                        600,
                        400
                );

        // Establece el título que aparecerá en la ventana principal.
        stage.setTitle(
                "Control de Membresías - Gimnasio"
        );

        // Asigna la escena creada a la ventana principal.
        stage.setScene(scene);

        // Muestra la ventana principal de la aplicación.
        stage.show();
    }

    /**
     * Ejecuta la aplicación JavaFX.
     *
     * @param args argumentos de ejecución
     */
    public static void main(String[] args) {

        // Inicia la aplicación JavaFX.
        launch();
    }
}