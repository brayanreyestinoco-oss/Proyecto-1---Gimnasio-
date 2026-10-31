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

public class ControladorAcceso {

    @FXML
    private TextField txtCedula;

    @FXML
    private Label lblNombre;

    @FXML
    private Label lblPlan;

    @FXML
    private Label lblVencimiento;

    @FXML
    private Label lblResultado;

    private ControlAcceso controlAcceso;

    @FXML
    public void initialize() {
        limpiarInformacion();
    }

    public void setControlAcceso(ControlAcceso controlAcceso) {
        this.controlAcceso = controlAcceso;
    }

    @FXML
    private void verificarAcceso() {

        String cedula = txtCedula.getText().trim();

        if (cedula.isEmpty()) {
            limpiarInformacion();

            lblResultado.setText(
                    "Ingrese el número de cédula del socio."
            );

            return;
        }

        if (controlAcceso == null) {
            limpiarInformacion();

            lblResultado.setText(
                    "Error: el servicio de acceso no está inicializado."
            );

            return;
        }

        Membresia membresia =
                controlAcceso.buscarMembresiaPorCedula(cedula);

        if (membresia == null) {
            limpiarInformacion();

            lblResultado.setText(
                    "ACCESO DENEGADO - SOCIO SIN MEMBRESÍA"
            );

            return;
        }

        mostrarInformacionMembresia(membresia);

        boolean accesoPermitido =
                controlAcceso.verificarAccesoPorCedula(cedula);

        if (accesoPermitido) {

            lblResultado.setText(
                    "ACCESO PERMITIDO"
            );

        } else {

            lblResultado.setText(
                    "ACCESO DENEGADO - MEMBRESÍA VENCIDA"
            );
        }
    }

    private void mostrarInformacionMembresia(Membresia membresia) {

        if (membresia.getSocio() != null) {

            lblNombre.setText(
                    membresia.getSocio().getNombre()
            );

        } else {

            lblNombre.setText("-");
        }

        if (membresia.getPlan() != null) {

            lblPlan.setText(
                    membresia.getPlan().getNombre()
            );

        } else {

            lblPlan.setText("-");
        }

        if (membresia.getFechaVencimiento() != null) {

            lblVencimiento.setText(
                    membresia.getFechaVencimiento().toString()
            );

        } else {

            lblVencimiento.setText(
                    "Sin pago registrado"
            );
        }
    }

    private void limpiarInformacion() {

        lblNombre.setText("-");
        lblPlan.setText("-");
        lblVencimiento.setText("-");

        lblResultado.setText(
                "Ingrese el número de cédula del socio"
        );
    }

    @FXML
    private void volverMenu() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class.getResource(
                                    "MenuPrincipalView.fxml"
                            )
                    );

            Parent root = loader.load();

            Stage stage =
                    (Stage) txtCedula.getScene().getWindow();

            stage.setScene(
                    new Scene(root, 600, 400)
            );

            stage.centerOnScreen();

        } catch (IOException e) {

            lblResultado.setText(
                    "Error al regresar al menú principal."
            );
        }
    }
}