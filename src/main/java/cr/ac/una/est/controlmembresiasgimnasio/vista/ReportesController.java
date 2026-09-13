package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.interfaces.IRenovable;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.service.PagoService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controlador encargado de mostrar el reporte
 * de membresías y de exportarlo a un archivo CSV.
 */
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

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private PagoService pagoService;

    /**
     * Inicializa la pantalla de reportes.
     */
    @FXML
    public void initialize() {

        pagoService =
                DatosAplicacion.getPagoService();

        colSocio.setCellValueFactory(
                new PropertyValueFactory<>("socio")
        );

        colCedula.setCellValueFactory(
                new PropertyValueFactory<>("cedula")
        );

        colPlan.setCellValueFactory(
                new PropertyValueFactory<>("plan")
        );

        colFechaInicio.setCellValueFactory(
                new PropertyValueFactory<>("fechaInicio")
        );

        colFechaVencimiento.setCellValueFactory(
                new PropertyValueFactory<>("fechaVencimiento")
        );

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        colRenovacionAutomatica.setCellValueFactory(
                new PropertyValueFactory<>("renovacionAutomatica")
        );

        colCostoRenovacion.setCellValueFactory(
                new PropertyValueFactory<>("costoRenovacion")
        );

        cargarReporte();
    }

    /**
     * Carga en la tabla todas las membresías registradas.
     */
    @FXML
    public void cargarReporte() {

        ObservableList<ReporteFila> filas =
                FXCollections.observableArrayList();

        for (Membresia membresia : pagoService.getMembresias()) {

            filas.add(
                    convertirAFila(membresia)
            );
        }

        tablaReporte.setItems(filas);
    }

    /**
     * Exporta el contenido actual de la tabla
     * a un archivo CSV elegido por el usuario.
     */
    @FXML
    public void exportarCsv() {

        if (tablaReporte.getItems().isEmpty()) {

            mostrarAlerta(
                    "No hay datos para exportar."
            );

            return;
        }

        FileChooser selector =
                new FileChooser();

        selector.setTitle(
                "Guardar reporte como CSV"
        );

        selector.setInitialFileName(
                "reporte_membresias.csv"
        );

        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Archivo CSV",
                        "*.csv"
                )
        );

        File archivo =
                selector.showSaveDialog(
                        tablaReporte.getScene().getWindow()
                );

        if (archivo == null) {
            return;
        }

        try (PrintWriter escritor =
                     new PrintWriter(
                             new FileWriter(archivo)
                     )) {

            escritor.println(
                    "Socio,Cedula,Plan,Fecha Inicio,Fecha Vencimiento,"
                            + "Estado,Renovacion Automatica,Costo Renovacion"
            );

            for (ReporteFila fila : tablaReporte.getItems()) {

                escritor.println(
                        escaparCsv(fila.getSocio()) + ","
                                + escaparCsv(fila.getCedula()) + ","
                                + escaparCsv(fila.getPlan()) + ","
                                + escaparCsv(fila.getFechaInicio()) + ","
                                + escaparCsv(fila.getFechaVencimiento()) + ","
                                + escaparCsv(fila.getEstado()) + ","
                                + escaparCsv(fila.getRenovacionAutomatica()) + ","
                                + escaparCsv(fila.getCostoRenovacion())
                );
            }

            mostrarAlerta(
                    "Reporte exportado correctamente a:\n"
                            + archivo.getAbsolutePath()
            );

        } catch (IOException e) {

            mostrarAlerta(
                    "No se pudo exportar el archivo CSV."
            );
        }
    }

    /**
     * Convierte una membresía en una fila
     * para mostrarla en la tabla.
     *
     * También consulta, si el plan implementa
     * IRenovable, si se renueva automáticamente
     * y cuánto costaría renovarlo.
     *
     * @param membresia membresía que se desea mostrar
     * @return fila preparada para la tabla
     */
    private ReporteFila convertirAFila(
            Membresia membresia) {

        String fechaInicio =
                membresia.getFechaInicio() == null
                        ? "-"
                        : membresia.getFechaInicio()
                        .format(FORMATO_FECHA);

        String fechaVencimiento =
                membresia.getFechaVencimiento() == null
                        ? "-"
                        : membresia.getFechaVencimiento()
                        .format(FORMATO_FECHA);

        String estado =
                membresia.estaVigente(LocalDate.now())
                        ? "Vigente"
                        : "Vencida";

        Plan plan =
                membresia.getPlan();

        /*
         * Si el plan implementa IRenovable, consultamos
         * si se renueva automáticamente y cuánto costaría
         * la renovación. Si no implementa la interfaz,
         * mostramos "N/A".
         */
        String renovacionAutomatica = "N/A";

        String costoRenovacion = "N/A";

        if (plan instanceof IRenovable renovable) {

            renovacionAutomatica =
                    renovable.esRenovableAutomaticamente()
                            ? "Sí"
                            : "No";

            costoRenovacion =
                    String.format(
                            "₡%,.0f",
                            renovable.costoRenovacion()
                    );
        }

        return new ReporteFila(
                membresia.getSocio().getNombre(),
                membresia.getSocio().getCedula(),
                plan.getNombre(),
                fechaInicio,
                fechaVencimiento,
                estado,
                renovacionAutomatica,
                costoRenovacion
        );
    }

    /**
     * Escapa un valor para que sea válido
     * dentro de una celda de un archivo CSV.
     *
     * @param valor texto que se desea escapar
     * @return texto listo para escribir en el CSV
     */
    private String escaparCsv(
            String valor) {

        if (valor == null) {
            return "";
        }

        String limpio =
                valor.replace(
                        "\"",
                        "\"\""
                );

        return "\"" + limpio + "\"";
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
                            tablaReporte
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

            mostrarAlerta(
                    "No se pudo regresar al menú principal."
            );
        }
    }

    /**
     * Muestra una alerta informativa.
     *
     * @param mensaje mensaje que se mostrará
     */
    private void mostrarAlerta(
            String mensaje) {

        Alert alerta =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alerta.setTitle(
                "Reporte de membresías"
        );

        alerta.setHeaderText(
                null
        );

        alerta.setContentText(
                mensaje
        );

        alerta.showAndWait();
    }

    /**
     * Clase auxiliar utilizada para mostrar
     * los datos de una membresía en la tabla.
     */
    public static class ReporteFila {

        private final String socio;

        private final String cedula;

        private final String plan;

        private final String fechaInicio;

        private final String fechaVencimiento;

        private final String estado;

        private final String renovacionAutomatica;

        private final String costoRenovacion;

        /**
         * Crea una fila para la tabla.
         */
        public ReporteFila(
                String socio,
                String cedula,
                String plan,
                String fechaInicio,
                String fechaVencimiento,
                String estado,
                String renovacionAutomatica,
                String costoRenovacion) {

            this.socio = socio;
            this.cedula = cedula;
            this.plan = plan;
            this.fechaInicio = fechaInicio;
            this.fechaVencimiento = fechaVencimiento;
            this.estado = estado;
            this.renovacionAutomatica = renovacionAutomatica;
            this.costoRenovacion = costoRenovacion;
        }

        public String getSocio() {
            return socio;
        }

        public String getCedula() {
            return cedula;
        }

        public String getPlan() {
            return plan;
        }

        public String getFechaInicio() {
            return fechaInicio;
        }

        public String getFechaVencimiento() {
            return fechaVencimiento;
        }

        public String getEstado() {
            return estado;
        }

        public String getRenovacionAutomatica() {
            return renovacionAutomatica;
        }

        public String getCostoRenovacion() {
            return costoRenovacion;
        }
    }
}