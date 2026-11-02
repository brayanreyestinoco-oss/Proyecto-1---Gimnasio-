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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

// ============================================================
// ReportesController.java
// QUÉ HACE:      Pantalla de Reportes: lista membresías (todas, por vencer o ya vencidas) y exporta
//                a CSV.
// CONECTADO A:   ReportesView.fxml y ReporteService.
// ============================================================
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

    // Opciones del filtro: "por vencer" = membresías activas que vencen entre hoy y dentro de N días.
    private static final String OPCION_TODAS = "Todas las membresías";
    private static final String OPCION_7 = "Por vencer: próximos 7 días";
    private static final String OPCION_15 = "Por vencer: próximos 15 días";
    private static final String OPCION_30 = "Por vencer: próximos 30 días";
    private static final String OPCION_VENCIDAS = "Ya vencidas";
    @FXML
    private Label lblEstadoConsulta;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnExportarCsv;

    private final ReporteService reporteService =
            new ReporteService(new ReporteRepositoryJdbc());
    private Task<List<ReporteFila>> tareaConsulta;

    // Configura las columnas y la lista de filtros.
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
                OPCION_TODAS, OPCION_7, OPCION_15, OPCION_30, OPCION_VENCIDAS));
        selectorPlazo.getSelectionModel().selectFirst();
        selectorPlazo.valueProperty().addListener(
                (observable, anterior, seleccionado) -> cargarReporte());
        btnExportarCsv.setDisable(true);
        cargarReporte();
    }

    // Consulta en segundo plano según el filtro elegido.
    @FXML
    public void cargarReporte() {
        if (tareaConsulta != null && tareaConsulta.isRunning()) {
            return;
        }

        boolean soloVencidas = OPCION_VENCIDAS.equals(selectorPlazo.getValue());
        Integer dias = soloVencidas ? null : obtenerDiasSeleccionados();
        cambiarEstadoConsulta(true);
        lblEstadoConsulta.setText("Consultando membresías en MySQL...");

        tareaConsulta = new Task<>() {
            @Override
            protected List<ReporteFila> call() throws Exception {
                return soloVencidas
                        ? reporteService.consultarVencidas()
                        : reporteService.consultar(dias);
            }
        };

        tareaConsulta.setOnSucceeded(event -> {
            List<ReporteFila> filas = tareaConsulta.getValue();
            tablaReporte.setItems(FXCollections.observableArrayList(filas));
            lblEstadoConsulta.setText(soloVencidas
                    ? describirVencidas(filas.size())
                    : describirResultado(filas.size(), dias));
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

    // Convierte la opcion elegida en 7, 15 o 30 días (o ninguno).
    private Integer obtenerDiasSeleccionados() {
        String seleccionado = selectorPlazo.getValue();
        if (seleccionado == null || OPCION_TODAS.equals(seleccionado)
                || OPCION_VENCIDAS.equals(seleccionado)) {
            return null;
        }
        return switch (seleccionado) {
            case OPCION_7 -> 7;
            case OPCION_15 -> 15;
            case OPCION_30 -> 30;
            default -> throw new IllegalStateException("Plazo de reporte no reconocido.");
        };
    }

    /**
     * Texto del estado de la consulta, específico para lo que se pidió.
     * "Por vencer" = membresía activa cuya fecha de vencimiento cae entre
     * hoy y dentro de N días (ver ReporteRepositoryJdbc.consultarProximasVencer).
     */
    private String describirResultado(int cantidad, Integer dias) {
        String membresias = cantidad == 1 ? "1 membresía" : cantidad + " membresías";

        if (dias == null) {
            return membresias + " en total (activas, vencidas e inactivas).";
        }

        LocalDate limite = LocalDate.now().plusDays(dias);
        String hasta = limite.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return cantidad == 0
                ? "Ninguna membresía activa vence en los próximos " + dias + " días (hasta el " + hasta + ")."
                : membresias + " activas vencen entre hoy y el " + hasta + ".";
    }

    // Frase de resumen para 'ya vencidas'.
    private String describirVencidas(int cantidad) {
        return cantidad == 0
                ? "No hay membresías vencidas."
                : (cantidad == 1 ? "1 membresía vencida" : cantidad + " membresías vencidas")
                + " (su fecha de vencimiento ya pasó).";
    }

    // Bloquea los botones mientras se consulta.
    private void cambiarEstadoConsulta(boolean consultando) {
        selectorPlazo.setDisable(consultando);
        btnActualizar.setDisable(consultando);
        if (consultando) {
            btnExportarCsv.setDisable(true);
        }
    }

    // Guarda la lista en un archivo CSV.
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

    // Escribe una linea del CSV.
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

    // Protege comillas y saltos de linea en el CSV.
    private String escaparCsv(String valor) {
        String sinSaltos = valor == null
                ? ""
                : valor.replace("\r\n", " ").replace('\r', ' ').replace('\n', ' ');
        return "\"" + sinSaltos.replace("\"", "\"\"") + "\"";
    }

    // Regresa al menú principal.
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

    // Muestra una ventanita de aviso.
    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Reporte de membresías");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
