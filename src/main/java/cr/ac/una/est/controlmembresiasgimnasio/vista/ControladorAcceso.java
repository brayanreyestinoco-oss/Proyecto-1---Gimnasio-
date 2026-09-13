package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador encargado de manejar la pantalla
 * de simulación de acceso al gimnasio.
 */
public class ControladorAcceso {

    @FXML
    private TextField txtNumeroSocio;

    @FXML
    private Label lblResultado;

    private ControlAcceso controlAcceso;

    /**
     * Inicializa los componentes de la pantalla.
     */
    @FXML
    public void initialize() {

        lblResultado.setText(
                "Ingrese el número del socio"
        );
    }

    /**
     * Asigna el servicio utilizado para comprobar
     * el acceso de los socios.
     *
     * @param controlAcceso servicio de control de acceso
     */
    public void setControlAcceso(
            ControlAcceso controlAcceso) {

        this.controlAcceso = controlAcceso;
    }

    /**
     * Verifica si el número de socio ingresado
     * posee una membresía vigente.
     */
    @FXML
    private void verificarAcceso() {

        String textoNumero =
                txtNumeroSocio.getText();

        if (textoNumero == null
                || textoNumero.isBlank()) {

            lblResultado.setText(
                    "Ingrese un número de socio."
            );

            return;
        }

        try {

            int idSocio =
                    Integer.parseInt(
                            textoNumero
                    );

            if (controlAcceso == null) {

                lblResultado.setText(
                        "Los datos del sistema no han sido cargados."
                );

                return;
            }

            boolean accesoPermitido =
                    controlAcceso.verificarAcceso(
                            idSocio
                    );

            if (accesoPermitido) {

                lblResultado.setText(
                        "ACCESO PERMITIDO"
                );

            } else {

                lblResultado.setText(
                        "ACCESO DENEGADO POR MOROSIDAD"
                );
            }

        } catch (NumberFormatException e) {

            lblResultado.setText(
                    "Ingrese un número de socio válido."
            );
        }
    }

    /**
     * Regresa desde la pantalla de acceso
     * hasta el menú principal.
     *
     * @throws IOException si ocurre un error al cargar el menú
     */
    @FXML
    private void volverMenu() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "MenuPrincipal.fxml"
                        )
                );

        Parent root =
                loader.load();

        Stage stage =
                (Stage)
                        txtNumeroSocio
                                .getScene()
                                .getWindow();

        Scene scene =
                new Scene(
                        root,
                        600,
                        400
                );

        stage.setScene(scene);
    }
}