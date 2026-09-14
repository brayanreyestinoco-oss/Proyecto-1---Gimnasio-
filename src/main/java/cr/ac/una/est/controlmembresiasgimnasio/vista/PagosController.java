package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Pago;
import cr.ac.una.est.controlmembresiasgimnasio.service.PagoService;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Controlador encargado de administrar
 * la pantalla principal de pagos.
 */
public class PagosController {

    // Campo de texto utilizado para buscar pagos.
    @FXML
    private TextField txtBuscar;

    // Tabla donde se muestran los pagos registrados.
    @FXML
    private TableView<Pago> tablaPagos;

    // Columna que muestra la cédula del socio.
    @FXML
    private TableColumn<Pago, String> colCedula;

    // Columna que muestra el nombre del socio.
    @FXML
    private TableColumn<Pago, String> colSocio;

    // Columna que muestra el nombre del plan contratado.
    @FXML
    private TableColumn<Pago, String> colPlan;

    // Columna que muestra la fecha en que se realizó el pago.
    @FXML
    private TableColumn<Pago, LocalDate> colFechaPago;

    // Columna que muestra el monto del pago.
    @FXML
    private TableColumn<Pago, Number> colMonto;

    // Columna que muestra la fecha de vencimiento de la membresía.
    @FXML
    private TableColumn<Pago, LocalDate> colVencimiento;

    // Servicio encargado de gestionar los pagos.
    private PagoService pagoService;

    /**
     * Inicializa el controlador de pagos.
     */
    @FXML
    public void initialize() {

        // Obtiene el servicio de pagos utilizado por la aplicación.
        pagoService =
                DatosAplicacion.getPagoService();

        // Configura la información que mostrará cada columna.
        configurarColumnas();

        // Carga los pagos registrados en la tabla.
        actualizarTabla();
    }

    /**
     * Configura las columnas de la tabla
     * para mostrar la información de cada pago.
     */
    private void configurarColumnas() {

        // Configura la columna para mostrar la cédula del socio.
        colCedula.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue()
                                        .getMembresia()
                                        .getSocio()
                                        .getCedula()
                        )
        );

        // Configura la columna para mostrar el nombre del socio.
        colSocio.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue()
                                        .getMembresia()
                                        .getSocio()
                                        .getNombre()
                        )
        );

        // Configura la columna para mostrar el nombre del plan.
        colPlan.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue()
                                        .getMembresia()
                                        .getPlan()
                                        .getNombre()
                        )
        );

        // Configura la columna para mostrar la fecha del pago.
        colFechaPago.setCellValueFactory(
                dato ->
                        new SimpleObjectProperty<>(
                                dato.getValue()
                                        .getFechaPago()
                        )
        );

        // Configura la columna para mostrar el monto pagado.
        colMonto.setCellValueFactory(
                dato ->
                        new SimpleDoubleProperty(
                                dato.getValue()
                                        .getMonto()
                        )
        );

        // Configura la columna para mostrar la fecha de vencimiento.
        colVencimiento.setCellValueFactory(
                dato ->
                        new SimpleObjectProperty<>(
                                dato.getValue()
                                        .getMembresia()
                                        .getFechaVencimiento()
                        )
        );
    }

    /**
     * Muestra todos los pagos registrados
     * en la tabla.
     */
    private void actualizarTabla() {

        // Obtiene la lista de pagos registrados y la convierte en una lista observable.
        tablaPagos.setItems(
                FXCollections.observableArrayList(
                        pagoService.getPagos()
                )
        );

        // Actualiza visualmente el contenido de la tabla.
        tablaPagos.refresh();
    }

    /**
     * Busca pagos utilizando la cédula
     * o el nombre del socio.
     */
    @FXML
    private void buscarPago() {

        // Obtiene el texto de búsqueda, elimina espacios y lo convierte a minúsculas.
        String busqueda =
                txtBuscar
                        .getText()
                        .trim()
                        .toLowerCase();

        /*
         * Si el campo está vacío mostramos
         * nuevamente todos los pagos.
         */
        if (busqueda.isBlank()) {

            // Vuelve a cargar todos los pagos en la tabla.
            actualizarTabla();

            // Finaliza el método porque no hay ningún criterio de búsqueda.
            return;
        }

        // Crea una lista observable donde se almacenarán los pagos encontrados.
        ObservableList<Pago> encontrados =
                FXCollections.observableArrayList();

        // Recorre todos los pagos registrados en el servicio.
        for (Pago pago :
                pagoService.getPagos()) {

            // Obtiene la cédula del socio asociado al pago.
            String cedula =
                    pago.getMembresia()
                            .getSocio()
                            .getCedula()
                            .toLowerCase();

            // Obtiene el nombre del socio asociado al pago.
            String nombre =
                    pago.getMembresia()
                            .getSocio()
                            .getNombre()
                            .toLowerCase();

            // Comprueba si la búsqueda coincide con la cédula o el nombre.
            if (cedula.contains(busqueda)
                    || nombre.contains(busqueda)) {

                // Agrega el pago encontrado a la lista de resultados.
                encontrados.add(
                        pago
                );
            }
        }

        // Muestra en la tabla únicamente los pagos encontrados.
        tablaPagos.setItems(
                encontrados
        );

        // Comprueba si no se encontró ningún pago.
        if (encontrados.isEmpty()) {

            // Muestra un mensaje indicando que no hubo coincidencias.
            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Búsqueda",
                    "No se encontraron pagos con esos datos."
            );
        }
    }

    /**
     * Muestra nuevamente todos los pagos.
     */
    @FXML
    private void mostrarTodos() {

        // Limpia el contenido del campo de búsqueda.
        txtBuscar.clear();

        // Vuelve a cargar todos los pagos en la tabla.
        actualizarTabla();
    }

    /**
     * Abre la pantalla para registrar
     * un nuevo pago.
     */
    @FXML
    private void registrarPago() {

        try {

            // Crea el cargador encargado de abrir la vista de registro de pagos.
            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "RegistrarPagoView.fxml"
                                    )
                    );

            // Carga la estructura visual de la pantalla de registro.
            Parent root =
                    loader.load();

            // Obtiene la ventana actual desde la tabla de pagos.
            Stage stage =
                    (Stage)
                            tablaPagos
                                    .getScene()
                                    .getWindow();

            // Cambia la escena actual por la pantalla de registro de pagos.
            stage.setScene(
                    new Scene(
                            root,
                            600,
                            400
                    )
            );

            // Centra nuevamente la ventana en la pantalla.
            stage.centerOnScreen();

        } catch (IOException e) {

            // Muestra una alerta si no se pudo cargar la pantalla.
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo abrir la pantalla para registrar el pago."
            );
        }
    }

    /**
     * Regresa al menú principal.
     */
    @FXML
    private void volverMenu() {

        try {

            // Crea el cargador para abrir la vista del menú principal.
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

            // Obtiene la ventana actual desde la tabla de pagos.
            Stage stage =
                    (Stage)
                            tablaPagos
                                    .getScene()
                                    .getWindow();

            // Reemplaza la escena actual por la del menú principal.
            stage.setScene(
                    new Scene(
                            root,
                            600,
                            400
                    )
            );

            // Centra nuevamente la ventana en la pantalla.
            stage.centerOnScreen();

        } catch (IOException e) {

            // Muestra una alerta si no se pudo regresar al menú.
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo regresar al menú principal."
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

        // Crea una nueva ventana de alerta con el tipo indicado.
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