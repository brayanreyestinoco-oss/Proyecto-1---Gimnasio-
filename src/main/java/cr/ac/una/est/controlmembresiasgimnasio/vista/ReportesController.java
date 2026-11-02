package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.Pantalla;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.reportes.ReporteFila;
import cr.ac.una.est.controlmembresiasgimnasio.repository.ReporteRepositoryJdbc;
import cr.ac.una.est.controlmembresiasgimnasio.service.ReporteService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class ReportesController {

    @FXML
    private TableView<ReporteFila> tablaReporte;
    @FXML
    private TableColumn<ReporteFila, String> colSocio;
    @FXML
    private TableColumn<ReporteFila, String> colCedula;
    @FXML
    private TableColumn<ReporteFila, String> colPlan;
    @FXML
    private TableColumn<ReporteFila, String> colFechaInicio;
    @FXML
    private TableColumn<ReporteFila, String> colFechaVencimiento;
    @FXML
    private TableColumn<ReporteFila, String> colEstado;
    @FXML
    private TableColumn<ReporteFila, String> colRenovacionAutomatica;
    @FXML
    private TableColumn<ReporteFila, String> colCostoRenovacion;
    @FXML
    private TableColumn<ReporteFila, String> colMetodoPago;
    @FXML
    private ComboBox<String> selectorPlazo;
    @FXML
    private Label lblEstadoConsulta;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnExportarCsv;

    private final ReporteService reporteService =
            new ReporteService(new ReporteRepositoryJdbc());
    private Task<List<ReporteFila>> tareaConsulta;

    @FXML
    public void initialize() {
        colSocio.setCellValueFactory(new PropertyValueFactory<>("socio"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colPlan.setCellValueFactory(new PropertyValueFactory<>("plan"));
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaVencimiento.setCellValueFactory(
                new PropertyValueFactory<>("fechaVencimiento"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colRenovacionAutomatica.setCellValueFactory(
                new PropertyValueFactory<>("renovacionAutomatica"));
        colCostoRenovacion.setCellValueFactory(
                new PropertyValueFactory<>("costoRenovacion"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));

        selectorPlazo.setItems(FXCollections.observableArrayList(
                "Todas", "7 días", "15 días", "30 días"));
        selectorPlazo.getSelectionModel().selectFirst();
        selectorPlazo.valueProperty().addListener(
                (observable, anterior, seleccionado) -> cargarReporte());
        btnExportarCsv.setDisable(true);
        cargarReporte();
    }

    @FXML
    public void cargarReporte() {
        if (tareaConsulta != null && tareaConsulta.isRunning()) {
            return;
        }

        Integer dias = obtenerDiasSeleccionados();
        cambiarEstadoConsulta(true);
        lblEstadoConsulta.setText("Consultando membresías en MySQL...");

        tareaConsulta = new Task<>() {
            @Override
            protected List<ReporteFila> call() throws Exception {
                return reporteService.consultar(dias);
            }
        };

        tareaConsulta.setOnSucceeded(event -> {
            List<ReporteFila> filas = tareaConsulta.getValue();
            tablaReporte.setItems(FXCollections.observableArrayList(filas));
            lblEstadoConsulta.setText(filas.size() + " membresías encontradas.");
            cambiarEstadoConsulta(false);
            btnExportarCsv.setDisable(filas.isEmpty());
        });

        tareaConsulta.setOnFailed(event -> {
            Throwable error = tareaConsulta.getException();
            tablaReporte.getItems().clear();
            lblEstadoConsulta.setText("No se pudo consultar MySQL.");
            cambiarEstadoConsulta(false);
            btnExportarCsv.setDisable(true);
            String detalle = error == null || error.getMessage() == null
                    ? "Error de conexión o consulta."
                    : error.getMessage();
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudo conectar o consultar la base de datos MySQL.\n"
                            + "Verifique la conexión y la configuración.\nDetalle: " + detalle);
        });

        tareaConsulta.setOnCancelled(event -> cambiarEstadoConsulta(false));
        Thread hilo = new Thread(tareaConsulta, "consulta-reportes");
        hilo.setDaemon(true);
        hilo.start();
    }

    private Integer obtenerDiasSeleccionados() {
        String seleccionado = selectorPlazo.getValue();
        if (seleccionado == null || "Todas".equals(seleccionado)) {
            return null;
        }
        return switch (seleccionado) {
            case "7 días" -> 7;
            case "15 días" -> 15;
            case "30 días" -> 30;
            default -> throw new IllegalStateException("Plazo de reporte no reconocido.");
        };
    }

    private void cambiarEstadoConsulta(boolean consultando) {
        selectorPlazo.setDisable(consultando);
        btnActualizar.setDisable(consultando);
        if (consultando) {
            btnExportarCsv.setDisable(true);
        }
    }

    @FXML
    public void exportarCsv() {
        if (tablaReporte.getItems().isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "No hay datos para exportar.");
            return;
        }

        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte como CSV");
        selector.setInitialFileName("reporte_membresias.csv");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));
        File archivo = selector.showSaveDialog(tablaReporte.getScene().getWindow());
        if (archivo == null) {
            return;
        }

        try (BufferedWriter escritor = Files.newBufferedWriter(
                archivo.toPath(), StandardCharsets.UTF_8)) {
            escribirFila(escritor, List.of(
                    "Socio", "Cedula", "Plan", "Fecha Inicio", "Fecha Vencimiento",
                    "Estado", "Renovacion Automatica", "Costo Renovacion", "Metodo Pago"));
            for (ReporteFila fila : tablaReporte.getItems()) {
                escribirFila(escritor, List.of(
                        fila.getSocio(), fila.getCedula(), fila.getPlan(),
                        fila.getFechaInicio(), fila.getFechaVencimiento(), fila.getEstado(),
                        fila.getRenovacionAutomatica(), fila.getCostoRenovacion(),
                        fila.getMetodoPago()));
            }
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Reporte exportado correctamente a:\n" + archivo.getAbsolutePath());
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudo exportar el archivo CSV:\n" + e.getMessage());
        }
    }

    private void escribirFila(BufferedWriter escritor, List<String> valores)
            throws IOException {
        for (int indice = 0; indice < valores.size(); indice++) {
            if (indice > 0) {
                escritor.write(',');
            }
            escritor.write(escaparCsv(valores.get(indice)));
        }
        escritor.write("\r\n");
    }

    private String escaparCsv(String valor) {
        String sinSaltos = valor == null
                ? ""
                : valor.replace("\r\n", " ").replace('\r', ' ').replace('\n', ' ');
        return "\"" + sinSaltos.replace("\"", "\"\"") + "\"";
    }

    @FXML
    private void volverMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource("MenuPrincipalView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tablaReporte.getScene().getWindow();
            Pantalla.cambiar(stage, root);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudo regresar al menú principal:\n" + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Reporte de membresías");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
