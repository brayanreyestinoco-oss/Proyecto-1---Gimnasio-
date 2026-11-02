package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.Pantalla;
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

    // Campo donde se ingresa la cédula del socio.
    @FXML
    private TextField txtCedula;

    // Campo donde se ingresa el nombre del socio.
    @FXML
    private TextField txtNombre;

    // Campo donde se ingresa el número de teléfono del socio.
    @FXML
    private TextField txtTelefono;

    // Campo donde se ingresa el correo electrónico del socio.
    @FXML
    private TextField txtCorreo;

    // Campo donde se ingresa el contacto de emergencia.
    @FXML
    private TextField txtContactoEmergencia;

    // Campo donde se ingresa el teléfono del contacto de emergencia.
    @FXML
    private TextField txtTelefonoEmergencia;

    // Área donde se ingresan las condiciones médicas del socio.
    @FXML
    private TextArea txtCondicionesMedicas;

    // Botón utilizado para guardar los datos del socio.
    @FXML
    private Button btnGuardar;

    // Botón utilizado para cancelar el registro o modificación.
    @FXML
    private Button btnCancelar;

    // Servicio encargado de gestionar los socios.
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

        // Obtiene el servicio de socios utilizado por la aplicación.
        socioService =
                DatosAplicacion.getSocioService();

        // Inicialmente no se está modificando ningún socio.
        socioModificar = null;
    }

    /**
     * Recibe el socio que se desea modificar
     * y carga sus datos en el formulario.
     *
     * @param socio socio que se desea modificar
     */
    public void setSocioModificar(Socio socio) {

        // Guarda el socio recibido para poder modificarlo posteriormente.
        this.socioModificar = socio;

        // Comprueba si no se recibió ningún socio.
        if (socio == null) {

            // Finaliza el método porque no hay datos que cargar.
            return;
        }

        // Carga la cédula del socio en el formulario.
        txtCedula.setText(
                socio.getCedula()
        );

        // Carga el nombre del socio en el formulario.
        txtNombre.setText(
                socio.getNombre()
        );

        // Carga el teléfono del socio en el formulario.
        txtTelefono.setText(
                socio.getTelefono()
        );

        // Carga el correo del socio en el formulario.
        txtCorreo.setText(
                socio.getCorreo()
        );

        // Carga el contacto de emergencia en el formulario.
        txtContactoEmergencia.setText(
                socio.getContactoEmergencia()
        );

        // Carga el teléfono del contacto de emergencia.
        txtTelefonoEmergencia.setText(
                socio.getTelefonoEmergencia()
        );

        // Carga las condiciones médicas del socio.
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
                    "Debe completar todos los campos obligatorios."
            );
            return;
        }

        if (!txtTelefono.getText().trim().matches("[0-9]+")) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Teléfono inválido",
                    "El teléfono del socio debe contener únicamente números."
            );
            return;
        }

        String telefonoEmergencia =
                txtTelefonoEmergencia.getText().trim();

        if (!telefonoEmergencia.matches("[0-9]+")) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Teléfono de emergencia inválido",
                    "El teléfono de emergencia debe contener únicamente números."
            );
            return;
        }

        try {
            if (socioModificar == null) {
                registrarNuevoSocio();
            } else {
                modificarSocioExistente();
            }

        } catch (IllegalArgumentException e) {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Datos inválidos",
                    e.getMessage()
            );

        } catch (IllegalStateException e) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de base de datos",
                    e.getMessage()
            );

        } catch (Exception e) {
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo guardar la información del socio."
            );
            e.printStackTrace();
        }
    }

    /**
     * Registra un socio nuevo.
     */

    private void registrarNuevoSocio() {

        Socio socio = new Socio(
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

        volverSocios();
    }
    /**
     * Modifica los datos del socio seleccionado.
     */
    private void modificarSocioExistente() {

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
                txtContactoEmergencia.getText().trim()
        );

        socioModificar.setTelefonoEmergencia(
                txtTelefonoEmergencia.getText().trim()
        );

        socioModificar.setCondicionesMedicas(
                txtCondicionesMedicas.getText().trim()
        );

        boolean modificado =
                socioService.modificar(socioModificar);

        if (modificado) {
            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Socio modificado",
                    "Los datos del socio fueron modificados correctamente."
            );

            volverSocios();

        } else {
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Sin cambios",
                    "No se encontró el socio que desea modificar."
            );
        }
    }

    /**
     * Limpia el formulario.
     */
    @FXML
    private void cancelar() {

        // Limpia todos los campos del formulario.
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
                && !txtTelefono.getText().isBlank()
                && !txtCorreo.getText().isBlank()
                && !txtContactoEmergencia.getText().isBlank()
                && !txtTelefonoEmergencia.getText().isBlank();
    }

    /**
     * Limpia todos los campos.
     */
    private void limpiarFormulario() {

        // Limpia el campo de cédula.
        txtCedula.clear();

        // Limpia el campo de nombre.
        txtNombre.clear();

        // Limpia el campo de teléfono.
        txtTelefono.clear();

        // Limpia el campo de correo.
        txtCorreo.clear();

        // Limpia el campo de contacto de emergencia.
        txtContactoEmergencia.clear();

        // Limpia el campo del teléfono de emergencia.
        txtTelefonoEmergencia.clear();

        // Limpia el área de condiciones médicas.
        txtCondicionesMedicas.clear();
    }

    /**
     * Regresa a la pantalla de socios.
     */
    @FXML
    private void volverSocios() {

        try {

            // Crea el cargador para abrir la vista de socios.
            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "SociosView.fxml"
                                    )
                    );

            // Carga la estructura visual de la pantalla de socios.
            Parent root =
                    loader.load();

            // Obtiene la ventana actual desde el campo de cédula.
            Stage stage =
                    (Stage)
                            txtCedula
                                    .getScene()
                                    .getWindow();

            // Cambia la escena actual por la pantalla de socios.
            Pantalla.cambiar(stage, root);

        } catch (IOException e) {

            // Muestra una alerta si no se pudo cargar la pantalla de socios.
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

        // Crea una alerta utilizando el tipo indicado.
        Alert alerta =
                new Alert(tipo);

        // Establece el título de la ventana de alerta.
        alerta.setTitle(titulo);

        // Elimina el encabezado de la alerta.
        alerta.setHeaderText(null);

        // Establece el mensaje que se mostrará al usuario.
        alerta.setContentText(mensaje);

        // Muestra la alerta y espera a que el usuario cierre la alerta.
        alerta.showAndWait();
    }
}