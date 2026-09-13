package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;
import cr.ac.una.est.controlmembresiasgimnasio.service.SocioService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador encargado de registrar
 * y modificar socios.
 */
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

    private SocioService socioService;

    /*
     * Guarda el socio que se está modificando.
     * Si es null, significa que estamos registrando
     * un socio nuevo.
     */
    private Socio socioModificar;

    /**
     * Inicializa el controlador.
     */
    @FXML
    public void initialize() {

        socioService =
                DatosAplicacion.getSocioService();

        socioModificar = null;
    }

    /**
     * Recibe el socio que se desea modificar
     * y carga sus datos en el formulario.
     *
     * @param socio socio que se desea modificar
     */
    public void setSocioModificar(Socio socio) {

        this.socioModificar = socio;

        if (socio == null) {
            return;
        }

        txtCedula.setText(
                socio.getCedula()
        );

        txtNombre.setText(
                socio.getNombre()
        );

        txtTelefono.setText(
                socio.getTelefono()
        );

        txtCorreo.setText(
                socio.getCorreo()
        );

        txtContactoEmergencia.setText(
                socio.getContactoEmergencia()
        );

        txtTelefonoEmergencia.setText(
                socio.getTelefonoEmergencia()
        );

        txtCondicionesMedicas.setText(
                socio.getCondicionesMedicas()
        );
    }

    /**
     * Guarda un socio nuevo o modifica
     * un socio existente.
     */
    @FXML
    private void guardarSocio() {

        if (!camposValidos()) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Debe completar los campos obligatorios."
            );

            return;
        }

        String cedula =
                txtCedula.getText().trim();

        /*
         * Si estamos registrando un socio nuevo,
         * revisamos que la cédula no exista.
         */
        if (socioModificar == null) {

            if (socioService.buscarPorCedula(cedula) != null) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Cédula registrada",
                        "Ya existe un socio con esa cédula."
                );

                return;
            }

            registrarNuevoSocio();

        } else {

            modificarSocioExistente();
        }
    }

    /**
     * Registra un socio nuevo.
     */
    private void registrarNuevoSocio() {

        Socio socio =
                new Socio(
                        txtCedula.getText().trim(),
                        txtNombre.getText().trim(),
                        txtTelefono.getText().trim(),
                        txtCorreo.getText().trim(),
                        txtContactoEmergencia.getText().trim(),
                        txtTelefonoEmergencia.getText().trim(),
                        txtCondicionesMedicas.getText().trim()
                );

        socioService.registrar(socio);

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Socio registrado",
                "El socio fue registrado correctamente.\n"
                        + "Número de socio: "
                        + socio.getIdSocio()
        );

        limpiarFormulario();
    }

    /**
     * Modifica los datos del socio seleccionado.
     */
    private void modificarSocioExistente() {

        /*
         * Revisamos si la nueva cédula pertenece
         * a otro socio.
         */
        Socio socioMismaCedula =
                socioService.buscarPorCedula(
                        txtCedula.getText().trim()
                );

        if (socioMismaCedula != null
                && socioMismaCedula.getIdSocio()
                != socioModificar.getIdSocio()) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Cédula registrada",
                    "Ya existe otro socio con esa cédula."
            );

            return;
        }

        socioModificar.setCedula(
                txtCedula.getText().trim()
        );

        socioModificar.setNombre(
                txtNombre.getText().trim()
        );

        socioModificar.setTelefono(
                txtTelefono.getText().trim()
        );

        socioModificar.setCorreo(
                txtCorreo.getText().trim()
        );

        socioModificar.setContactoEmergencia(
                txtContactoEmergencia
                        .getText()
                        .trim()
        );

        socioModificar.setTelefonoEmergencia(
                txtTelefonoEmergencia
                        .getText()
                        .trim()
        );

        socioModificar.setCondicionesMedicas(
                txtCondicionesMedicas
                        .getText()
                        .trim()
        );

        boolean modificado =
                socioService.modificar(
                        socioModificar
                );

        if (modificado) {

            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Socio modificado",
                    "Los datos del socio fueron modificados correctamente."
            );

            volverSocios();
        }
    }

    /**
     * Limpia el formulario.
     */
    @FXML
    private void cancelar() {

        limpiarFormulario();
    }

    /**
     * Verifica los campos obligatorios.
     *
     * @return true si los campos contienen datos
     */
    private boolean camposValidos() {

        return !txtCedula.getText().isBlank()
                && !txtNombre.getText().isBlank()
                && !txtTelefono.getText().isBlank();
    }

    /**
     * Limpia todos los campos.
     */
    private void limpiarFormulario() {

        txtCedula.clear();
        txtNombre.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtContactoEmergencia.clear();
        txtTelefonoEmergencia.clear();
        txtCondicionesMedicas.clear();
    }

    /**
     * Regresa a la pantalla de socios.
     */
    @FXML
    private void volverSocios() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "SociosView.fxml"
                                    )
                    );

            Parent root =
                    loader.load();

            Stage stage =
                    (Stage)
                            txtCedula
                                    .getScene()
                                    .getWindow();

            stage.setScene(
                    new Scene(
                            root,
                            600,
                            400
                    )
            );

        } catch (IOException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo regresar a la pantalla de socios."
            );
        }
    }

    /**
     * Muestra una alerta al usuario.
     *
     * @param tipo tipo de alerta
     * @param titulo título de la ventana
     * @param mensaje mensaje que se mostrará
     */
    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta =
                new Alert(tipo);

        alerta.setTitle(titulo);

        alerta.setHeaderText(null);

        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}
