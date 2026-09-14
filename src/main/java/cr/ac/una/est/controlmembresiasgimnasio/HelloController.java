package cr.ac.una.est.controlmembresiasgimnasio;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

// Controlador asociado a una vista de JavaFX.
public class HelloController {

    // Etiqueta utilizada para mostrar el mensaje de bienvenida.
    @FXML
    private Label welcomeText;

    // Método ejecutado cuando se presiona el botón correspondiente.
    @FXML
    protected void onHelloButtonClick() {

        // Cambia el texto de la etiqueta por el mensaje de bienvenida.
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}