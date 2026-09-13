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

        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "LoginView.fxml"
                        )
                );

        Scene scene =
                new Scene(
                        fxmlLoader.load(),
                        600,
                        400
                );

        stage.setTitle(
                "Control de Membresías - Gimnasio"
        );

        stage.setScene(scene);

        stage.show();
    }

    /**
     * Ejecuta la aplicación JavaFX.
     *
     * @param args argumentos de ejecución
     */
    public static void main(String[] args) {

        launch();
    }
}