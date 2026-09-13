package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;
import cr.ac.una.est.controlmembresiasgimnasio.service.PagoService;
import cr.ac.una.est.controlmembresiasgimnasio.service.SocioService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador encargado de administrar
 * la pantalla de socios.
 */
public class SociosController {

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Socio> tablaSocios;

    @FXML
    private TableColumn<Socio, String> colCedula;

    @FXML
    private TableColumn<Socio, String> colNombre;

    @FXML
    private TableColumn<Socio, String> colTelefono;

    @FXML
    private TableColumn<Socio, String> colCorreo;

    @FXML
    private Label lblContactoEmergencia;

    @FXML
    private Label lblCondicionesMedicas;

    private SocioService socioService;

    private PagoService pagoService;

    /**
     * Inicializa la pantalla de socios.
     */
    @FXML
    public void initialize() {

        socioService =
                DatosAplicacion.getSocioService();

        pagoService =
                DatosAplicacion.getPagoService();

        colCedula.setCellValueFactory(
                new PropertyValueFactory<>("cedula")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colTelefono.setCellValueFactory(
                new PropertyValueFactory<>("telefono")
        );

        colCorreo.setCellValueFactory(
                new PropertyValueFactory<>("correo")
        );

        tablaSocios
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable,
                         socioAnterior,
                         socioSeleccionado) -> {

                            mostrarInformacion(
                                    socioSeleccionado
                            );
                        }
                );

        actualizarTabla();
    }

    /**
     * Actualiza la tabla con todos
     * los socios registrados.
     */
    @FXML
    public void actualizarTabla() {

        tablaSocios.setItems(
                FXCollections.observableArrayList(
                        socioService.getSocios()
                )
        );

        txtBuscar.clear();
    }

    /**
     * Busca un socio por cédula o nombre.
     */
    @FXML
    public void buscarSocio() {

        String texto =
                txtBuscar
                        .getText()
                        .trim()
                        .toLowerCase();

        if (texto.isEmpty()) {

            actualizarTabla();

            return;
        }

        tablaSocios.setItems(
                FXCollections.observableArrayList(
                        socioService
                                .getSocios()
                                .stream()
                                .filter(
                                        s -> s.getCedula()
                                                .toLowerCase()
                                                .contains(texto)
                                                || s.getNombre()
                                                .toLowerCase()
                                                .contains(texto)
                                )
                                .toList()
                )
        );
    }

    /**
     * Muestra la información adicional
     * del socio seleccionado.
     *
     * @param socio socio seleccionado en la tabla
     */
    private void mostrarInformacion(
            Socio socio) {

        if (socio == null) {

            limpiarInformacion();

            return;
        }

        lblContactoEmergencia.setText(
                socio.getContactoEmergencia()
        );

        lblCondicionesMedicas.setText(
                socio.getCondicionesMedicas()
        );
    }

    /**
     * Limpia la información adicional mostrada.
     */
    private void limpiarInformacion() {

        lblContactoEmergencia.setText(
                "-"
        );

        lblCondicionesMedicas.setText(
                "-"
        );
    }

    /**
     * Elimina el socio seleccionado.
     *
     * No permite eliminar a un socio que ya
     * tenga una membresía registrada, para no
     * dejar pagos o membresías huérfanas en el sistema.
     */
    @FXML
    private void eliminarSocio() {

        Socio seleccionado =
                tablaSocios
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Eliminar socio",
                    "Seleccione un socio de la tabla."
            );

            return;
        }

        Membresia membresia =
                pagoService.buscarMembresiaPorSocio(
                        seleccionado.getIdSocio()
                );

        if (membresia != null) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "No se puede eliminar",
                    "Este socio ya tiene una membresía registrada.\n"
                            + "Elimine o transfiera su membresía antes "
                            + "de eliminar al socio."
            );

            return;
        }

        boolean eliminado =
                socioService.eliminar(
                        seleccionado.getIdSocio()
                );

        if (eliminado) {

            actualizarTabla();

            limpiarInformacion();

            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Socio eliminado",
                    "El socio fue eliminado correctamente."
            );
        }
    }

    /**
     * Abre el formulario de registro
     * cargando los datos del socio seleccionado
     * para poder modificarlos.
     */
    @FXML
    private void modificarSocio() {

        Socio seleccionado =
                tablaSocios
                        .getSelectionModel()
                        .getSelectedItem();

        if (seleccionado == null) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Modificar socio",
                    "Seleccione un socio de la tabla."
            );

            return;
        }

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "RegistroSocioView.fxml"
                                    )
                    );

            Parent root =
                    loader.load();

            RegistroSocioController controlador =
                    loader.getController();

            controlador.setSocioModificar(
                    seleccionado
            );

            Stage stage =
                    (Stage)
                            tablaSocios
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
                    "No se pudo abrir el formulario de modificación."
            );
        }
    }

    /**
     * Abre el formulario para registrar un nuevo socio.
     */
    @FXML
    private void registrarSocio() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "RegistroSocioView.fxml"
                                    )
                    );

            Parent root =
                    loader.load();

            Stage stage =
                    (Stage)
                            tablaSocios
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
                    "No se pudo abrir el formulario de registro."
            );
        }
    }

    /**
     * Regresa al menú principal.
     *
     * @throws IOException si ocurre un error al cargar el menú
     */
    @FXML
    private void volverMenu()
            throws IOException {

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
                        tablaSocios
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
    }

    /**
     * Muestra un mensaje al usuario.
     *
     * @param tipo tipo de alerta
     * @param titulo título de la alerta
     * @param mensaje mensaje que se mostrará
     */
    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta =
                new Alert(tipo);

        alerta.setTitle(
                titulo
        );

        alerta.setHeaderText(
                null
        );

        alerta.setContentText(
                mensaje
        );

        alerta.showAndWait();
    }
}