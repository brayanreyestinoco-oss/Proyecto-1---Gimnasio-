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

    // Campo de texto utilizado para buscar socios por cédula o nombre.
    @FXML
    private TextField txtBuscar;

    // Tabla donde se muestran los socios registrados.
    @FXML
    private TableView<Socio> tablaSocios;

    // Columna donde se muestra la cédula de cada socio.
    @FXML
    private TableColumn<Socio, String> colCedula;

    // Columna donde se muestra el nombre de cada socio.
    @FXML
    private TableColumn<Socio, String> colNombre;

    // Columna donde se muestra el teléfono de cada socio.
    @FXML
    private TableColumn<Socio, String> colTelefono;

    // Columna donde se muestra el correo electrónico de cada socio.
    @FXML
    private TableColumn<Socio, String> colCorreo;

    // Etiqueta utilizada para mostrar el contacto de emergencia del socio seleccionado.
    @FXML
    private Label lblContactoEmergencia;

    // Etiqueta utilizada para mostrar las condiciones médicas del socio seleccionado.
    @FXML
    private Label lblCondicionesMedicas;

    // Servicio encargado de gestionar las operaciones relacionadas con socios.
    private SocioService socioService;

    // Servicio encargado de gestionar las operaciones relacionadas con pagos y membresías.
    private PagoService pagoService;

    /**
     * Inicializa la pantalla de socios.
     */
    @FXML
    public void initialize() {

        // Obtiene el servicio de socios utilizado por la aplicación.
        socioService =
                DatosAplicacion.getSocioService();

        // Obtiene el servicio de pagos y membresías utilizado por la aplicación.
        pagoService =
                DatosAplicacion.getPagoService();

        // Configura la columna de cédula para obtener el valor del atributo "cedula".
        colCedula.setCellValueFactory(
                new PropertyValueFactory<>("cedula")
        );

        // Configura la columna de nombre para obtener el valor del atributo "nombre".
        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        // Configura la columna de teléfono para obtener el valor del atributo "telefono".
        colTelefono.setCellValueFactory(
                new PropertyValueFactory<>("telefono")
        );

        // Configura la columna de correo para obtener el valor del atributo "correo".
        colCorreo.setCellValueFactory(
                new PropertyValueFactory<>("correo")
        );

        // Agrega un listener para detectar cuando cambia el socio seleccionado en la tabla.
        tablaSocios
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (observable,
                         socioAnterior,
                         socioSeleccionado) -> {

                            // Muestra la información adicional del nuevo socio seleccionado.
                            mostrarInformacion(
                                    socioSeleccionado
                            );
                        }
                );

        // Carga inicialmente todos los socios en la tabla.
        actualizarTabla();
    }

    /**
     * Actualiza la tabla con todos
     * los socios registrados.
     */
    @FXML
    public void actualizarTabla() {

        // Obtiene la lista de socios del servicio y la convierte en una lista observable.
        tablaSocios.setItems(
                FXCollections.observableArrayList(
                        socioService.getSocios()
                )
        );

        // Limpia el campo de búsqueda después de actualizar la tabla.
        txtBuscar.clear();
    }

    /**
     * Busca un socio por cédula o nombre.
     */
    @FXML
    public void buscarSocio() {

        // Obtiene el texto de búsqueda, elimina espacios y lo convierte a minúsculas.
        String texto =
                txtBuscar
                        .getText()
                        .trim()
                        .toLowerCase();

        // Comprueba si el campo de búsqueda está vacío.
        if (texto.isEmpty()) {

            // Si está vacío, muestra nuevamente todos los socios.
            actualizarTabla();

            // Detiene el método después de actualizar la tabla.
            return;
        }

        // Filtra la lista de socios utilizando la cédula o el nombre.
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

        // Comprueba si no existe ningún socio seleccionado.
        if (socio == null) {

            // Limpia la información adicional mostrada.
            limpiarInformacion();

            // Detiene el método porque no hay un socio seleccionado.
            return;
        }

        // Muestra el nombre del contacto de emergencia.
        lblContactoEmergencia.setText(
                socio.getContactoEmergencia()
        );

        // Muestra las condiciones médicas del socio.
        lblCondicionesMedicas.setText(
                socio.getCondicionesMedicas()
        );
    }

    /**
     * Limpia la información adicional mostrada.
     */
    private void limpiarInformacion() {

        // Coloca un guion cuando no hay contacto de emergencia seleccionado.
        lblContactoEmergencia.setText(
                "-"
        );

        // Coloca un guion cuando no hay condiciones médicas seleccionadas.
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

        // Obtiene el socio actualmente seleccionado en la tabla.
        Socio seleccionado =
                tablaSocios
                        .getSelectionModel()
                        .getSelectedItem();

        // Comprueba si no se seleccionó ningún socio.
        if (seleccionado == null) {

            // Informa al usuario que debe seleccionar un socio.
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Eliminar socio",
                    "Seleccione un socio de la tabla."
            );

            // Detiene el proceso de eliminación.
            return;
        }

        // Busca si el socio seleccionado tiene una membresía registrada.
        Membresia membresia =
                pagoService.buscarMembresiaPorSocio(
                        seleccionado.getIdSocio()
                );

        // Comprueba si el socio tiene una membresía asociada.
        if (membresia != null) {

            // Informa que no se puede eliminar debido a la membresía existente.
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "No se puede eliminar",
                    "Este socio ya tiene una membresía registrada.\n"
                            + "Elimine o transfiera su membresía antes "
                            + "de eliminar al socio."
            );

            // Detiene el proceso de eliminación.
            return;
        }

        // Solicita al servicio que elimine al socio seleccionado.
        boolean eliminado =
                socioService.eliminar(
                        seleccionado.getIdSocio()
                );

        // Comprueba si la eliminación se realizó correctamente.
        if (eliminado) {

            // Actualiza la tabla para reflejar la eliminación.
            actualizarTabla();

            // Limpia la información adicional del socio seleccionado.
            limpiarInformacion();

            // Informa al usuario que el socio fue eliminado correctamente.
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

        // Obtiene el socio seleccionado actualmente en la tabla.
        Socio seleccionado =
                tablaSocios
                        .getSelectionModel()
                        .getSelectedItem();

        // Comprueba si no se seleccionó ningún socio.
        if (seleccionado == null) {

            // Informa al usuario que debe seleccionar un socio.
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Modificar socio",
                    "Seleccione un socio de la tabla."
            );

            // Detiene el proceso de modificación.
            return;
        }

        try {

            // Crea el cargador utilizado para abrir el formulario de socios.
            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "RegistroSocioView.fxml"
                                    )
                    );

            // Carga la estructura visual del formulario.
            Parent root =
                    loader.load();

            // Obtiene el controlador asociado al formulario cargado.
            RegistroSocioController controlador =
                    loader.getController();

            // Envía al controlador el socio que se desea modificar.
            controlador.setSocioModificar(
                    seleccionado
            );

            // Obtiene la ventana actual desde la tabla de socios.
            Stage stage =
                    (Stage)
                            tablaSocios
                                    .getScene()
                                    .getWindow();

            // Cambia la escena actual por el formulario de modificación.
            stage.setScene(
                    new Scene(
                            root,
                            600,
                            400
                    )
            );

        } catch (IOException e) {

            // Muestra un mensaje si ocurrió un error al abrir el formulario.
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

            // Crea el cargador utilizado para abrir el formulario de registro.
            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "RegistroSocioView.fxml"
                                    )
                    );

            // Carga la estructura visual del formulario.
            Parent root =
                    loader.load();

            // Obtiene la ventana actual desde la tabla de socios.
            Stage stage =
                    (Stage)
                            tablaSocios
                                    .getScene()
                                    .getWindow();

            // Cambia la escena actual por el formulario de registro.
            stage.setScene(
                    new Scene(
                            root,
                            600,
                            400
                    )
            );

        } catch (IOException e) {

            // Muestra un mensaje si ocurrió un error al abrir el formulario.
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

        // Crea el cargador utilizado para abrir la vista del menú principal.
        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class
                                .getResource(
                                        "MenuPrincipalView.fxml"
                                )
                );

        // Carga la estructura visual del menú principal.
        Parent root =
                loader.load();

        // Obtiene la ventana actual desde la tabla de socios.
        Stage stage =
                (Stage)
                        tablaSocios
                                .getScene()
                                .getWindow();

        // Cambia la escena actual por la del menú principal.
        stage.setScene(
                new Scene(
                        root,
                        600,
                        400
                )
        );

        // Centra nuevamente la ventana en la pantalla.
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

        // Crea una alerta utilizando el tipo indicado.
        Alert alerta =
                new Alert(tipo);

        // Establece el título de la ventana de alerta.
        alerta.setTitle(
                titulo
        );

        // Elimina el encabezado de la alerta.
        alerta.setHeaderText(
                null
        );

        // Establece el mensaje que se mostrará al usuario.
        alerta.setContentText(
                mensaje
        );

        // Muestra la alerta y espera a que el usuario la cierre.
        alerta.showAndWait();
    }
}