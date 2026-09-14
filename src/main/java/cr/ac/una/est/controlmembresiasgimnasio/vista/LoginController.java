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
    private static final String USUARIO_VALIDO = "admin";
    private static final String CONTRASENA_VALIDA = "admin";

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Label lblMensaje;

    /**
     * Valida las credenciales ingresadas y,
     * si son correctas, abre el menú principal.
     */
    @FXML
    private void ingresar() {

        String usuario =
                txtUsuario
                        .getText()
                        .trim();

        String contrasena =
                txtContrasena
                        .getText()
                        .trim();

        if (usuario.isBlank() || contrasena.isBlank()) {

            lblMensaje.setText(
                    "Ingrese usuario y contraseña."
            );

            return;
        }

        if (usuario.equals(USUARIO_VALIDO)
                && contrasena.equals(CONTRASENA_VALIDA)) {

            abrirMenuPrincipal();

        } else {

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

            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class.getResource(
                                    "MenuPrincipalView.fxml"
                            )
                    );

            Parent root =
                    loader.load();

            Stage stage =
                    (Stage)
                            txtUsuario
                                    .getScene()
                                    .getWindow();

            stage.setScene(
                    new Scene(
                            root,
                            600,
                            400
                    )
            );

            stage.centerOnScreen();

        } catch (IOException e) {

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

        Platform.exit();
    }
}
