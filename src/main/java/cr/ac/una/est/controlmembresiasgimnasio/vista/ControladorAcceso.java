package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
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
 * Controlador encargado de manejar
 * la simulación de acceso al gimnasio.
 */
public class ControladorAcceso {

    @FXML
    private TextField txtNumeroSocio;

    @FXML
    private Label lblNombre;

    @FXML
    private Label lblPlan;

    @FXML
    private Label lblVencimiento;

    @FXML
    private Label lblResultado;

    private ControlAcceso controlAcceso;

    /**
     * Inicializa la pantalla de acceso.
     */
    @FXML
    public void initialize() {

        limpiarInformacion();
    }

    /**
     * Asigna el servicio utilizado
     * para verificar el acceso.
     *
     * @param controlAcceso servicio de control de acceso
     */
    public void setControlAcceso(
            ControlAcceso controlAcceso) {

        this.controlAcceso =
                controlAcceso;
    }

    /**
     * Verifica si el socio ingresado
     * posee una membresía vigente.
     */
    @FXML
    private void verificarAcceso() {

        String textoNumero =
                txtNumeroSocio
                        .getText()
                        .trim();

        if (textoNumero.isBlank()) {

            limpiarInformacion();

            lblResultado.setText(
                    "Ingrese un número de socio."
            );

            return;
        }

        int idSocio;

        try {

            idSocio =
                    Integer.parseInt(
                            textoNumero
                    );

        } catch (NumberFormatException e) {

            limpiarInformacion();

            lblResultado.setText(
                    "Ingrese un número de socio válido."
            );

            return;
        }

        if (controlAcceso == null) {

            limpiarInformacion();

            lblResultado.setText(
                    "Los datos del sistema no han sido cargados."
            );

            return;
        }

        /*
         * Buscamos la membresía asociada
         * al número de socio.
         */
        Membresia membresia =
                controlAcceso.buscarMembresia(
                        idSocio
                );

        /*
         * Si no existe membresía,
         * el socio no puede ingresar.
         */
        if (membresia == null) {

            limpiarInformacion();

            lblResultado.setText(
                    "ACCESO DENEGADO - SIN MEMBRESÍA"
            );

            return;
        }

        mostrarInformacionMembresia(
                membresia
        );

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
    }

    /**
     * Muestra los datos de la membresía
     * encontrada.
     *
     * @param membresia membresía del socio
     */
    private void mostrarInformacionMembresia(
            Membresia membresia) {

        lblNombre.setText(
                membresia
                        .getSocio()
                        .getNombre()
        );

        lblPlan.setText(
                membresia
                        .getPlan()
                        .getNombre()
        );

        if (membresia.getFechaVencimiento()
                == null) {

            lblVencimiento.setText(
                    "Sin pago registrado"
            );

        } else {

            lblVencimiento.setText(
                    membresia
                            .getFechaVencimiento()
                            .toString()
            );
        }
    }

    /**
     * Limpia la información mostrada
     * en pantalla.
     */
    private void limpiarInformacion() {

        lblNombre.setText(
                "-"
        );

        lblPlan.setText(
                "-"
        );

        lblVencimiento.setText(
                "-"
        );

        lblResultado.setText(
                "Ingrese el número del socio"
        );
    }

    /**
     * Regresa al menú principal.
     */
    @FXML
    private void volverMenu() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "MenuPrincipalView.fxml"
                                    )
                    );

            Parent root =
                    loader.load();

            Stage stage =
                    (Stage)
                            txtNumeroSocio
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

            lblResultado.setText(
                    "No se pudo regresar al menú principal."
            );
        }
    }
}