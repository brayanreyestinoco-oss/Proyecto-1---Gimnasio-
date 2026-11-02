package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.Pantalla;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.facturas.Factura;
import cr.ac.una.est.controlmembresiasgimnasio.service.FacturaDocx;
import cr.ac.una.est.controlmembresiasgimnasio.service.FacturaService;
import cr.ac.una.est.controlmembresiasgimnasio.service.FormatoFactura;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

// ============================================================
// FacturacionController.java
// QUÉ HACE:      Pantalla de Facturacion: lista las facturas, muestra la vista previa y exporta a
//                Word.
// CONECTADO A:   FacturacionView.fxml, FacturaService (facturas), FormatoFactura (vista previa) y
//                FacturaDocx (Word).
// ============================================================
/**
 * Controlador de la pantalla de facturación: lista las facturas de los
 * clientes (una por pago), muestra una vista previa y las exporta a Word.
 */
public class FacturacionController {

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Factura> tablaFacturas;

    @FXML
    private TableColumn<Factura, String> colNumero;

    @FXML
    private TableColumn<Factura, String> colFecha;

    @FXML
    private TableColumn<Factura, String> colCliente;

    @FXML
    private TableColumn<Factura, String> colCedula;

    @FXML
    private TableColumn<Factura, String> colPlan;

    @FXML
    private TableColumn<Factura, String> colMetodo;

    @FXML
    private TableColumn<Factura, String> colTotal;

    @FXML
    private TextArea txtVistaPrevia;

    @FXML
    private Label lblEstado;

    @FXML
    private Button btnExportar;

    private final FacturaService facturaService =
            new FacturaService(DatosAplicacion.getPagoService());

    private final ObservableList<Factura> facturas =
            FXCollections.observableArrayList();

    private final FilteredList<Factura> facturasFiltradas =
            new FilteredList<>(facturas, f -> true);

    // Configura la tabla, el buscador y la vista previa.
    @FXML
    public void initialize() {

        colNumero.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().numero()));
        colFecha.setCellValueFactory(
                c -> new SimpleStringProperty(
                        FormatoFactura.fecha(c.getValue().fecha())));
        colCliente.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().clienteNombre()));
        colCedula.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().clienteCedula()));
        colPlan.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().planNombre()));
        colMetodo.setCellValueFactory(
                c -> new SimpleStringProperty(c.getValue().metodoPago()));
        colTotal.setCellValueFactory(
                c -> new SimpleStringProperty(
                        FormatoFactura.dinero(c.getValue().total())));

        tablaFacturas.setItems(facturasFiltradas);

        tablaFacturas.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> mostrarVistaPrevia(actual));

        txtBuscar.textProperty().addListener(
                (obs, anterior, actual) -> filtrar(actual));

        btnExportar.disableProperty().bind(
                tablaFacturas.getSelectionModel()
                        .selectedItemProperty().isNull());

        cargarFacturas();
    }

    /** Consulta los pagos en segundo plano para no congelar la ventana. */
    @FXML
    private void cargarFacturas() {

        lblEstado.setText("Cargando facturas...");

        Task<List<Factura>> tarea = new Task<>() {
            @Override
            protected List<Factura> call() {
                return facturaService.listar();
            }
        };

        tarea.setOnSucceeded(e -> {
            facturas.setAll(tarea.getValue());
            lblEstado.setText(facturas.size() + " factura(s)");
        });

        tarea.setOnFailed(e -> {
            lblEstado.setText("No se pudieron cargar las facturas.");
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudieron cargar las facturas:\n"
                            + tarea.getException().getMessage());
        });

        Thread hilo = new Thread(tarea, "cargar-facturas");
        hilo.setDaemon(true);
        hilo.start();
    }

    // Filtra la lista por número, cédula o nombre.
    private void filtrar(String texto) {

        String filtro = texto == null
                ? "" : texto.trim().toLowerCase();

        facturasFiltradas.setPredicate(f -> filtro.isEmpty()
                || f.numero().toLowerCase().contains(filtro)
                || f.clienteNombre().toLowerCase().contains(filtro)
                || f.clienteCedula().toLowerCase().contains(filtro));
    }

    // Borra el buscador para ver todas.
    @FXML
    private void mostrarTodas() {
        txtBuscar.clear();
    }

    // Muestra el texto de la factura seleccionada.
    private void mostrarVistaPrevia(Factura factura) {
        txtVistaPrevia.setText(factura == null
                ? "Seleccione una factura para ver su vista previa."
                : FormatoFactura.vistaPrevia(factura));
    }

    // Pregunta dónde guardar y crea el Word con FacturaDocx.
    @FXML
    private void exportarDocx() {

        Factura factura =
                tablaFacturas.getSelectionModel().getSelectedItem();

        if (factura == null) {
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Seleccione una factura para exportar.");
            return;
        }

        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar factura como Word");
        selector.setInitialFileName("Factura_" + factura.numero() + ".docx");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Documento de Word", "*.docx"));

        File archivo = selector.showSaveDialog(
                tablaFacturas.getScene().getWindow());

        if (archivo == null) {
            return;
        }

        if (!archivo.getName().toLowerCase().endsWith(".docx")) {
            archivo = new File(archivo.getPath() + ".docx");
        }

        try {
            FacturaDocx.exportar(factura, archivo.toPath());

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Factura exportada correctamente a:\n"
                            + archivo.getAbsolutePath());

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudo exportar la factura:\n" + e.getMessage());
        }
    }

    // Regresa al menú principal.
    @FXML
    private void volverMenu() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(
                            "MenuPrincipalView.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) tablaFacturas.getScene().getWindow();

            Pantalla.cambiar(stage, root);

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudo regresar al menú principal.");
        }
    }

    // Muestra una ventanita de aviso.
    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Facturación");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
