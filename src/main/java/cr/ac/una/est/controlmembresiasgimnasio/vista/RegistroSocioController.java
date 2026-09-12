package cr.ac.una.est.controlmembresiasgimnasio.vista;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegistroSocioController {

    @FXML
    private TextField txtCedula;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtContactoEmergencia;

    @FXML
    private TextField txtTelefonoEmergencia;

    @FXML
    private TextArea txtCondicionesMedicas;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;


    @FXML
    private void guardarSocio() {
        System.out.println("Botón guardar presionado");
    }


    @FXML
    private void cancelar() {
        limpiarFormulario();
    }


    private void limpiarFormulario() {
        txtCedula.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtContactoEmergencia.clear();
        txtTelefonoEmergencia.clear();
        txtCondicionesMedicas.clear();
    }
}

