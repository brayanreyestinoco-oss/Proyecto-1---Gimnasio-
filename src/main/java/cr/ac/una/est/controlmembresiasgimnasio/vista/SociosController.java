package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;
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

    /**
     * Inicializa la pantalla de socios.
     */
    @FXML
    public void initialize() {

        socioService =
                DatosAplicacion.getSocioService();

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
    private void actualizarTabla() {

        tablaSocios.setItems(
                FXCollections.observableArrayList(
                        socioService.getSocios()
                )
        );

        tablaSocios.refresh();
    }

    /**
     * Busca un socio por nombre o cédula.
     */
    @FXML
    private void buscarSocio() {

        String busqueda =
                txtBuscar
                        .getText()
                        .trim();

        if (busqueda.isBlank()) {

            actualizarTabla();

            return;
        }

        for (Socio socio :
                socioService.getSocios()) {

            boolean coincideCedula =
                    socio.getCedula()
                            .equalsIgnoreCase(
                                    busqueda
                            );

            boolean coincideNombre =
                    socio.getNombre()
                            .toLowerCase()
                            .contains(
                                    busqueda.toLowerCase()
                            );

            if (coincideCedula
                    || coincideNombre) {

                tablaSocios
                        .getSelectionModel()
                        .select(socio);

                tablaSocios.scrollTo(
                        socio
                );

                mostrarInformacion(
                        socio
                );

                return;
            }
        }

        mostrarMensaje(
                Alert.AlertType.WARNING,
                "Socio no encontrado",
                "No se encontró ningún socio con esos datos."
        );
    }

    /**
     * Abre la pantalla para registrar
     * un nuevo socio.
     *
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void registrarSocio()
            throws IOException {

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
    }

    /**
     * Elimina el socio seleccionado.
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
     *
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void modificarSocio()
            throws IOException {

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

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class
                                .getResource(
                                        "RegistroSocioView.fxml"
                                )
                );

        Parent root =
                loader.load();

        RegistroSocioController controller =
                loader.getController();

        controller.setSocioModificar(
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
    }

    /**
     * Muestra la información adicional
     * del socio seleccionado.
     *
     * @param socio socio seleccionado
     */
    private void mostrarInformacion(
            Socio socio) {

        if (socio == null) {

            limpiarInformacion();

            return;
        }

        lblContactoEmergencia.setText(
                "Contacto de emergencia: "
                        + socio.getContactoEmergencia()
                        + " - "
                        + socio.getTelefonoEmergencia()
        );

        String condiciones =
                socio.getCondicionesMedicas();

        if (condiciones == null
                || condiciones.isBlank()) {

            condiciones =
                    "Ninguna";
        }

        lblCondicionesMedicas.setText(
                "Condiciones médicas: "
                        + condiciones
        );
    }

    /**
     * Limpia la información adicional.
     */
    private void limpiarInformacion() {

        lblContactoEmergencia.setText(
                "Contacto de emergencia: -"
        );

        lblCondicionesMedicas.setText(
                "Condiciones médicas: -"
        );
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
    }

    /**
     * Muestra mensajes al usuario.
     *
     * @param tipo tipo de alerta
     * @param titulo título
     * @param mensaje mensaje
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