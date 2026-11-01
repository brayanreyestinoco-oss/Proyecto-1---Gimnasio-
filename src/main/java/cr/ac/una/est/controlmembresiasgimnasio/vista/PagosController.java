package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.Pantalla;
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
import java.util.List;

/**
 * Controlador de la pantalla de gestión de pagos.
 *
 * Consulta los pagos a través de PagoService,
 * que utiliza los repositorios JDBC de MySQL.
 *
 * Conserva la búsqueda, las columnas y la navegación
 * existentes en la interfaz original.
 */
public class PagosController {

    // Campo utilizado para buscar por cédula o nombre.
    @FXML
    private TextField txtBuscar;

    // Tabla principal de pagos.
    @FXML
    private TableView<Pago> tablaPagos;

    // Columna de la cédula del socio.
    @FXML
    private TableColumn<Pago, String> colCedula;

    // Columna del nombre del socio.
    @FXML
    private TableColumn<Pago, String> colSocio;

    // Columna del plan contratado.
    @FXML
    private TableColumn<Pago, String> colPlan;

    // Columna de la fecha del pago.
    @FXML
    private TableColumn<Pago, LocalDate> colFechaPago;

    // Columna del monto pagado.
    @FXML
    private TableColumn<Pago, Number> colMonto;

    // Columna de la fecha de vencimiento.
    @FXML
    private TableColumn<Pago, LocalDate> colVencimiento;

    // Columna del método de pago.
    @FXML
    private TableColumn<Pago, String> colMetodoPago;

    // Servicio compartido de pagos.
    private PagoService pagoService;

    /**
     * Se ejecuta automáticamente al cargar el FXML.
     */
    @FXML
    public void initialize() {

        // Recuperamos la instancia compartida del servicio.
        pagoService = DatosAplicacion.getPagoService();

        // Indicamos qué información muestra cada columna.
        configurarColumnas();

        // Intentamos cargar los pagos desde MySQL.
        actualizarTabla();
    }

    /**
     * Configura las columnas de la tabla.
     *
     * Cada columna extrae su valor de un objeto Pago
     * y de sus objetos relacionados: Membresia y Socio.
     */
    private void configurarColumnas() {

        // Mostrar la cédula del socio.
        colCedula.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue()
                                .getMembresia()
                                .getSocio()
                                .getCedula()
                )
        );

        // Mostrar el nombre del socio.
        colSocio.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue()
                                .getMembresia()
                                .getSocio()
                                .getNombre()
                )
        );

        // Mostrar el nombre del plan.
        colPlan.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue()
                                .getMembresia()
                                .getPlan()
                                .getNombre()
                )
        );

        // Mostrar la fecha del pago.
        colFechaPago.setCellValueFactory(dato ->
                new SimpleObjectProperty<>(
                        dato.getValue().getFechaPago()
                )
        );

        // Mostrar el monto pagado.
        colMonto.setCellValueFactory(dato ->
                new SimpleDoubleProperty(
                        dato.getValue().getMonto()
                )
        );

        // Mostrar el vencimiento de la membresía.
        colVencimiento.setCellValueFactory(dato ->
                new SimpleObjectProperty<>(
                        dato.getValue()
                                .getMembresia()
                                .getFechaVencimiento()
                )
        );

        // Mostrar el método utilizado para pagar.
        colMetodoPago.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getMetodoPago()
                )
        );
    }

    /**
     * Consulta todos los pagos de MySQL y los muestra.
     *
     * Si ocurre un error JDBC, muestra una alerta
     * y evita que la pantalla falle inesperadamente.
     */
    private void actualizarTabla() {

        try {

            // Solicitar los pagos al servicio.
            List<Pago> pagos = pagoService.getPagos();

            // Convertirlos en una lista observable de JavaFX.
            ObservableList<Pago> datos =
                    FXCollections.observableArrayList(pagos);

            // Colocar los pagos en la tabla.
            tablaPagos.setItems(datos);

            // Refrescar visualmente la tabla.
            tablaPagos.refresh();

        } catch (IllegalStateException e) {

            // Evitar mostrar información desactualizada.
            tablaPagos.setItems(
                    FXCollections.observableArrayList()
            );

            // Informar que falló la consulta.
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error al consultar pagos",
                    "No fue posible cargar los pagos desde MySQL.\n"
                            + e.getMessage()
            );
        }
    }

    /**
     * Busca pagos por cédula o nombre del socio.
     */
    @FXML
    private void buscarPago() {

        // Recuperar el texto ingresado.
        String busqueda = txtBuscar.getText();

        // Proteger el método si el campo está vacío.
        if (busqueda == null || busqueda.isBlank()) {

            // Sin criterio, mostrar todos los pagos.
            actualizarTabla();
            return;
        }

        // Eliminar espacios y normalizar a minúsculas.
        busqueda = busqueda.trim().toLowerCase();

        // Lista para almacenar las coincidencias.
        ObservableList<Pago> encontrados =
                FXCollections.observableArrayList();

        try {

            // Consultar los pagos actuales desde MySQL.
            List<Pago> pagos = pagoService.getPagos();

            // Revisar cada pago.
            for (Pago pago : pagos) {

                // Recuperar la cédula del socio.
                String cedula = pago.getMembresia()
                        .getSocio()
                        .getCedula()
                        .toLowerCase();

                // Recuperar el nombre del socio.
                String nombre = pago.getMembresia()
                        .getSocio()
                        .getNombre()
                        .toLowerCase();

                // Verificar coincidencia parcial.
                if (cedula.contains(busqueda)
                        || nombre.contains(busqueda)) {

                    // Agregar el pago encontrado.
                    encontrados.add(pago);
                }
            }

            // Mostrar solamente las coincidencias.
            tablaPagos.setItems(encontrados);

            // Refrescar la tabla.
            tablaPagos.refresh();

            // Informar si no hubo resultados.
            if (encontrados.isEmpty()) {

                mostrarMensaje(
                        Alert.AlertType.INFORMATION,
                        "Búsqueda",
                        "No se encontraron pagos con esos datos."
                );
            }

        } catch (IllegalStateException e) {

            // Limpiar la tabla para evitar que permanezcan
            // resultados de una consulta anterior.
            tablaPagos.setItems(
                    FXCollections.observableArrayList()
            );

            // Mostrar el error ocurrido al consultar MySQL.
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de búsqueda",
                    "No fue posible consultar los pagos.\n"
                            + e.getMessage()
            );
        }
    }

    /**
     * Limpia la búsqueda y vuelve a consultar todos los pagos.
     */
    @FXML
    private void mostrarTodos() {

        // Vaciar el campo de búsqueda.
        txtBuscar.clear();

        // Volver a consultar los pagos desde MySQL.
        actualizarTabla();
    }

    /**
     * Abre el formulario para registrar un pago.
     *
     * Conserva la navegación del proyecto original.
     */
    @FXML
    private void registrarPago() {

        try {

            // Localizar el FXML del formulario.
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(
                            "RegistrarPagoView.fxml"
                    )
            );

            // Cargar la interfaz.
            Parent root = loader.load();

            // Recuperar la ventana actual.
            Stage stage = (Stage) tablaPagos
                    .getScene()
                    .getWindow();

            // Mostrar la pantalla de registro.
            Pantalla.cambiar(stage, root);
        } catch (IOException | RuntimeException e) {

            // Informar si no se pudo abrir la pantalla.
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo abrir la pantalla de registro.\n"
                            + e.getMessage()
            );
        }
    }

    /**
     * Regresa al menú principal.
     */
    @FXML
    private void volverMenu() {

        try {

            // Localizar el FXML del menú principal.
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(
                            "MenuPrincipalView.fxml"
                    )
            );

            // Cargar la interfaz.
            Parent root = loader.load();

            // Recuperar la ventana actual.
            Stage stage = (Stage) tablaPagos
                    .getScene()
                    .getWindow();

            // Mostrar el menú principal.
            Pantalla.cambiar(stage, root);
        } catch (IOException | RuntimeException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo regresar al menú principal.\n"
                            + e.getMessage()
            );
        }
    }

    /**
     * Muestra mensajes informativos, advertencias o errores.
     */
    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        // Crear una alerta del tipo solicitado.
        Alert alerta = new Alert(tipo);

        // Configurar título y contenido.
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        // Mostrar la alerta.
        alerta.showAndWait();
    }
}