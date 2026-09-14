package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador encargado de manejar
 * el inicio de sesión de la aplicación.
 */
public class LoginController {

    /*
     * Usuario y contraseña válidos para ingresar.
     * Cámbialos aquí si necesitas otras credenciales.
     */

    // Usuario establecido como válido para iniciar sesión.
    private static final String USUARIO_VALIDO = "admin";

    // Contraseña establecida como válida para iniciar sesión.
    private static final String CONTRASENA_VALIDA = "admin";

    // Campo donde el usuario ingresa su nombre de usuario.
    @FXML
    private TextField txtUsuario;

    // Campo donde el usuario ingresa su contraseña.
    @FXML
    private PasswordField txtContrasena;

    // Etiqueta utilizada para mostrar mensajes al usuario.
    @FXML
    private Label lblMensaje;

    /**
     * Valida las credenciales ingresadas y,
     * si son correctas, abre el menú principal.
     */
    @FXML
    private void ingresar() {

        // Obtiene el usuario ingresado y elimina espacios al inicio y al final.
        String usuario =
                txtUsuario
                        .getText()
                        .trim();

        // Obtiene la contraseña ingresada y elimina espacios al inicio y al final.
        String contrasena =
                txtContrasena
                        .getText()
                        .trim();

        // Comprueba si alguno de los campos está vacío.
        if (usuario.isBlank() || contrasena.isBlank()) {

            // Muestra un mensaje indicando que se deben completar ambos campos.
            lblMensaje.setText(
                    "Ingrese usuario y contraseña."
            );

            // Detiene la ejecución del método porque faltan datos.
            return;
        }

        // Comprueba si el usuario y la contraseña coinciden con las credenciales válidas.
        if (usuario.equals(USUARIO_VALIDO)
                && contrasena.equals(CONTRASENA_VALIDA)) {

            // Abre la pantalla del menú principal.
            abrirMenuPrincipal();

        } else {

            // Muestra un mensaje cuando las credenciales no son correctas.
            lblMensaje.setText(
                    "Usuario o contraseña incorrectos."
            );
        }
    }

    /**
     * Carga la pantalla del menú principal
     * en la misma ventana.
     */
    private void abrirMenuPrincipal() {

        try {

            // Crea un FXMLLoader para cargar la vista del menú principal.
            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class.getResource(
                                    "MenuPrincipalView.fxml"
                            )
                    );

            // Carga la estructura visual definida en el archivo FXML.
            Parent root =
                    loader.load();

            // Obtiene la ventana actual desde el campo de usuario.
            Stage stage =
                    (Stage)
                            txtUsuario
                                    .getScene()
                                    .getWindow();

            // Reemplaza la escena actual por la escena del menú principal.
            stage.setScene(
                    new Scene(
                            root,
                            600,
                            400
                    )
            );

            // Centra nuevamente la ventana en la pantalla.
            stage.centerOnScreen();

        } catch (IOException e) {

            // Muestra un mensaje si ocurre un error al cargar el menú principal.
            lblMensaje.setText(
                    "No se pudo abrir el menú principal."
            );
        }
    }

    /**
     * Cierra la aplicación por completo.
     */
    @FXML
    private void salir() {

        // Cierra completamente la aplicación JavaFX.
        Platform.exit();
    }
}