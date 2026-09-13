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

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Pago> tablaPagos;

    @FXML
    private TableColumn<Pago, String> colCedula;

    @FXML
    private TableColumn<Pago, String> colSocio;

    @FXML
    private TableColumn<Pago, String> colPlan;

    @FXML
    private TableColumn<Pago, LocalDate> colFechaPago;

    @FXML
    private TableColumn<Pago, Number> colMonto;

    @FXML
    private TableColumn<Pago, LocalDate> colVencimiento;

    private PagoService pagoService;

    /**
     * Inicializa el controlador de pagos.
     */
    @FXML
    public void initialize() {

        pagoService =
                DatosAplicacion.getPagoService();

        configurarColumnas();

        actualizarTabla();
    }

    /**
     * Configura las columnas de la tabla
     * para mostrar la información de cada pago.
     */
    private void configurarColumnas() {

        colCedula.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue()
                                        .getMembresia()
                                        .getSocio()
                                        .getCedula()
                        )
        );

        colSocio.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue()
                                        .getMembresia()
                                        .getSocio()
                                        .getNombre()
                        )
        );

        colPlan.setCellValueFactory(
                dato ->
                        new SimpleStringProperty(
                                dato.getValue()
                                        .getMembresia()
                                        .getPlan()
                                        .getNombre()
                        )
        );

        colFechaPago.setCellValueFactory(
                dato ->
                        new SimpleObjectProperty<>(
                                dato.getValue()
                                        .getFechaPago()
                        )
        );

        colMonto.setCellValueFactory(
                dato ->
                        new SimpleDoubleProperty(
                                dato.getValue()
                                        .getMonto()
                        )
        );

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

        tablaPagos.setItems(
                FXCollections.observableArrayList(
                        pagoService.getPagos()
                )
        );

        tablaPagos.refresh();
    }

    /**
     * Busca pagos utilizando la cédula
     * o el nombre del socio.
     */
    @FXML
    private void buscarPago() {

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

            actualizarTabla();

            return;
        }

        ObservableList<Pago> encontrados =
                FXCollections.observableArrayList();

        for (Pago pago :
                pagoService.getPagos()) {

            String cedula =
                    pago.getMembresia()
                            .getSocio()
                            .getCedula()
                            .toLowerCase();

            String nombre =
                    pago.getMembresia()
                            .getSocio()
                            .getNombre()
                            .toLowerCase();

            if (cedula.contains(busqueda)
                    || nombre.contains(busqueda)) {

                encontrados.add(
                        pago
                );
            }
        }

        tablaPagos.setItems(
                encontrados
        );

        if (encontrados.isEmpty()) {

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

        txtBuscar.clear();

        actualizarTabla();
    }

    /**
     * Abre la pantalla para registrar
     * un nuevo pago.
     */
    @FXML
    private void registrarPago() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "RegistrarPagoView.fxml"
                                    )
                    );

            Parent root =
                    loader.load();

            Stage stage =
                    (Stage)
                            tablaPagos
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
                            tablaPagos
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