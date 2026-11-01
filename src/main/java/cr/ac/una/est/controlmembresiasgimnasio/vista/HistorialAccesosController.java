package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.Pantalla;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.accesos.RegistroAcceso;
import cr.ac.una.est.controlmembresiasgimnasio.service.HistorialAccesoService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Pantalla con el historial de entradas al gimnasio
 * (tabla acceso_historial) y de consultas de membresía
 * (tabla consulta_membresia).
 */
public class HistorialAccesosController {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final int LIMITE_FILAS = 5000;

    private static final String HOY = "Hoy";
    private static final String SIETE_DIAS = "Últimos 7 días";
    private static final String TODOS = "Todos";

    private static final String RES_TODOS = "Todos";
    private static final String RES_PERMITIDOS = "Permitidos / Vigentes";
    private static final String RES_DENEGADOS = "Denegados / No vigentes";

    private static final String VER_ENTRADAS = "Intentos de acceso";
    private static final String VER_CONSULTAS = "Consultas de membresía";

    @FXML
    private ComboBox<String> cmbTipo;

    @FXML
    private ComboBox<String> cmbPeriodo;

    @FXML
    private ComboBox<String> cmbResultado;

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<RegistroAcceso> tablaAccesos;

    @FXML
    private TableColumn<RegistroAcceso, String> colFechaHora;

    @FXML
    private TableColumn<RegistroAcceso, String> colSocio;

    @FXML
    private TableColumn<RegistroAcceso, String> colCedula;

    @FXML
    private TableColumn<RegistroAcceso, String> colResultado;

    @FXML
    private TableColumn<RegistroAcceso, String> colMotivo;

    @FXML private javafx.scene.control.ProgressIndicator indicadorCarga;
    @FXML private javafx.scene.control.DatePicker fechaDesde;
    @FXML private javafx.scene.control.DatePicker fechaHasta;
    private Task<List<RegistroAcceso>> tareaActual;
    @FXML
    private Label lblResumen;

    private final HistorialAccesoService servicio =
            new HistorialAccesoService();

    private final ObservableList<RegistroAcceso> registros =
            FXCollections.observableArrayList();

    private final FilteredList<RegistroAcceso> filtrados =
            new FilteredList<>(registros, r -> true);

    @FXML
    public void initialize() {

        cmbTipo.getItems().setAll(VER_ENTRADAS, VER_CONSULTAS);
        cmbTipo.setValue(VER_ENTRADAS);
        colMotivo.setText("Motivo");

        cmbPeriodo.getItems().setAll(HOY, SIETE_DIAS, TODOS);
        cmbPeriodo.setValue(HOY);

        cmbResultado.getItems().setAll(
                RES_TODOS, RES_PERMITIDOS, RES_DENEGADOS);
        cmbResultado.setValue(RES_TODOS);

        colFechaHora.setCellValueFactory(c -> new SimpleStringProperty(
                FORMATO_FECHA.format(c.getValue().fechaHora())));
        colSocio.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().nombre() == null ? "-" : c.getValue().nombre()));
        colCedula.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().cedula()));
        colResultado.setCellValueFactory(c -> new SimpleStringProperty(
                textoResultado(c.getValue())));
        colMotivo.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().motivo()));

        // Colorea el resultado: verde permitido/vigente, rojo en otro caso.
        colResultado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String texto, boolean vacio) {
                super.updateItem(texto, vacio);

                if (vacio || texto == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(texto);
                setStyle("-fx-font-weight: bold; -fx-text-fill: "
                        + (esPositivo(texto)
                        ? "#15803D" : "#DC2626") + ";");
            }
        });

        tablaAccesos.setItems(filtrados);

        txtBuscar.setOnAction(e -> cargarHistorial());
        cmbResultado.valueProperty().addListener(
                (obs, anterior, actual) -> aplicarFiltros());
        cmbPeriodo.valueProperty().addListener(
                (obs, anterior, actual) -> cargarHistorial());
        cmbTipo.valueProperty().addListener(
                (obs, anterior, actual) -> {
                    colMotivo.setText(VER_CONSULTAS.equals(actual)
                            ? "Estado" : "Motivo");
                    tablaAccesos.refresh();
                    cargarHistorial();
                });

        filtrados.addListener(
                (javafx.collections.ListChangeListener<RegistroAcceso>)
                        cambio -> actualizarResumen());

        cargarHistorial();
    }

    /** Consulta MySQL en segundo plano según el período elegido. */
    @FXML
    private void cargarHistorial() {

        if (fechaDesde.getValue() != null && fechaHasta.getValue() != null
                && fechaDesde.getValue().isAfter(fechaHasta.getValue())) {
            mostrarAlerta(Alert.AlertType.WARNING, "La fecha inicial no puede superar la final.");
            return;
        }
        if (tareaActual != null) tareaActual.cancel();
        LocalDateTime desde = fechaDesde.getValue() == null ? calcularDesde()
                : fechaDesde.getValue().atStartOfDay();
        LocalDateTime hasta = fechaHasta.getValue() == null ? null
                : fechaHasta.getValue().plusDays(1).atStartOfDay();
        String socio = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim();
        boolean consultas = VER_CONSULTAS.equals(cmbTipo.getValue());

        registros.clear();
        indicadorCarga.setVisible(true);
        lblResumen.setText("Cargando historial...");

        Task<List<RegistroAcceso>> tarea = new Task<>() {
            @Override
            protected List<RegistroAcceso> call() {
                return consultas
                        ? servicio.buscarConsultas(desde, hasta, socio, LIMITE_FILAS)
                        : servicio.buscar(desde, hasta, socio, LIMITE_FILAS);
            }
        };

        tareaActual = tarea;
        tarea.setOnSucceeded(e -> {
            if (tareaActual != tarea) return;
            indicadorCarga.setVisible(false);
            registros.setAll(tarea.getValue());
            aplicarFiltros();
            actualizarResumen();
        });

        tarea.setOnFailed(e -> {
            if (tareaActual != tarea) return;
            indicadorCarga.setVisible(false);
            lblResumen.setText("No se pudo cargar el historial.");
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudo cargar el historial:\n"
                            + tarea.getException().getMessage());
        });

        Thread hilo = new Thread(tarea, "cargar-historial-accesos");
        hilo.setDaemon(true);
        hilo.start();
    }

    private String textoResultado(RegistroAcceso r) {
        boolean consulta = VER_CONSULTAS.equals(cmbTipo.getValue());

        if (consulta) {
            return r.permitido() ? "VIGENTE" : "NO VIGENTE";
        }

        return r.permitido() ? "PERMITIDO" : "DENEGADO";
    }

    private boolean esPositivo(String texto) {
        return "PERMITIDO".equals(texto) || "VIGENTE".equals(texto);
    }

    private LocalDateTime calcularDesde() {

        String periodo = cmbPeriodo.getValue();

        if (SIETE_DIAS.equals(periodo)) {
            return LocalDate.now().minusDays(6).atStartOfDay();
        }

        if (TODOS.equals(periodo)) {
            return null;
        }

        return LocalDate.now().atStartOfDay();
    }

    private void aplicarFiltros() {

        String texto = txtBuscar.getText() == null
                ? "" : txtBuscar.getText().trim().toLowerCase();

        String resultado = cmbResultado.getValue();
        LocalDate inicio = fechaDesde.getValue();
        LocalDate fin = fechaHasta.getValue();

        filtrados.setPredicate(r -> {
            LocalDate fecha = r.fechaHora().toLocalDate();
            if (inicio != null && fecha.isBefore(inicio)) return false;
            if (fin != null && fecha.isAfter(fin)) return false;

            if (RES_PERMITIDOS.equals(resultado) && !r.permitido()) {
                return false;
            }

            if (RES_DENEGADOS.equals(resultado) && r.permitido()) {
                return false;
            }

            return texto.isEmpty()
                    || r.cedula().equalsIgnoreCase(texto)
                    || (r.nombre() != null
                    && r.nombre().toLowerCase().contains(texto));
        });
    }

    private void actualizarResumen() {

        long permitidas = filtrados.stream()
                .filter(RegistroAcceso::permitido).count();

        long total = filtrados.size();

        lblResumen.setText((registros.size() == LIMITE_FILAS ? "Límite de 5000: acorte el período. " : "") + total + " registro(s): "
                + permitidas + " permitido(s)/vigente(s), "
                + (total - permitidas) + " denegado(s)/no vigente(s)");
    }

    @FXML
    private void exportarCsv() {

        if (tareaActual != null && tareaActual.isRunning()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Espere a que termine la consulta.");
            return;
        }
        if (filtrados.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "No hay datos para exportar.");
            return;
        }

        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar historial como CSV");
        selector.setInitialFileName("historial_accesos.csv");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo CSV", "*.csv"));

        File archivo = selector.showSaveDialog(
                tablaAccesos.getScene().getWindow());

        if (archivo == null) {
            return;
        }

        try (BufferedWriter escritor = Files.newBufferedWriter(
                archivo.toPath(), StandardCharsets.UTF_8)) {

            // BOM para que Excel muestre bien las tildes.
            escritor.write('\uFEFF');
            escribirFila(escritor, List.of(
                    "Fecha y hora", "Socio", "Cedula",
                    "Resultado",
                    VER_CONSULTAS.equals(cmbTipo.getValue())
                            ? "Estado" : "Motivo"));

            for (RegistroAcceso r : filtrados) {
                escribirFila(escritor, List.of(
                        FORMATO_FECHA.format(r.fechaHora()),
                        r.nombre() == null ? "" : r.nombre(),
                        r.cedula(),
                        textoResultado(r),
                        r.motivo()));
            }

            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Historial exportado correctamente a:\n"
                            + archivo.getAbsolutePath());

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudo exportar el archivo CSV:\n"
                            + e.getMessage());
        }
    }

    private void escribirFila(BufferedWriter escritor, List<String> valores)
            throws IOException {

        for (int i = 0; i < valores.size(); i++) {
            if (i > 0) {
                escritor.write(',');
            }
            escritor.write("\"" + valores.get(i).replace("\"", "\"\"") + "\"");
        }

        escritor.write("\r\n");
    }

    @FXML
    private void volverMenu() {
        if (tareaActual != null) tareaActual.cancel();

        try {
            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(
                            "MenuPrincipalView.fxml"));

            Parent root = loader.load();

            Stage stage = (Stage) tablaAccesos.getScene().getWindow();

            Pantalla.cambiar(stage, root);

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                    "No se pudo regresar al menú principal.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Historial de accesos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}