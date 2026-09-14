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

    // Tabla donde se muestran los datos del reporte.
    @FXML
    private TableView<ReporteFila> tablaReporte;

    // Columna que muestra el nombre del socio.
    @FXML
    private TableColumn<ReporteFila, String> colSocio;

    // Columna que muestra la cédula del socio.
    @FXML
    private TableColumn<ReporteFila, String> colCedula;

    // Columna que muestra el nombre del plan.
    @FXML
    private TableColumn<ReporteFila, String> colPlan;

    // Columna que muestra la fecha de inicio de la membresía.
    @FXML
    private TableColumn<ReporteFila, String> colFechaInicio;

    // Columna que muestra la fecha de vencimiento de la membresía.
    @FXML
    private TableColumn<ReporteFila, String> colFechaVencimiento;

    // Columna que muestra si la membresía está vigente o vencida.
    @FXML
    private TableColumn<ReporteFila, String> colEstado;

    // Columna que muestra si el plan tiene renovación automática.
    @FXML
    private TableColumn<ReporteFila, String> colRenovacionAutomatica;

    // Columna que muestra el costo de renovación del plan.
    @FXML
    private TableColumn<ReporteFila, String> colCostoRenovacion;

    // Formato utilizado para mostrar las fechas en el reporte.
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Servicio encargado de gestionar las membresías y pagos.
    private PagoService pagoService;

    /**
     * Inicializa la pantalla de reportes.
     */
    @FXML
    public void initialize() {

        // Obtiene el servicio de pagos utilizado por la aplicación.
        pagoService =
                DatosAplicacion.getPagoService();

        // Indica que la columna de socio utilizará el atributo "socio".
        colSocio.setCellValueFactory(
                new PropertyValueFactory<>("socio")
        );

        // Indica que la columna de cédula utilizará el atributo "cedula".
        colCedula.setCellValueFactory(
                new PropertyValueFactory<>("cedula")
        );

        // Indica que la columna de plan utilizará el atributo "plan".
        colPlan.setCellValueFactory(
                new PropertyValueFactory<>("plan")
        );

        // Indica que la columna de fecha de inicio utilizará el atributo correspondiente.
        colFechaInicio.setCellValueFactory(
                new PropertyValueFactory<>("fechaInicio")
        );

        // Indica que la columna de vencimiento utilizará el atributo correspondiente.
        colFechaVencimiento.setCellValueFactory(
                new PropertyValueFactory<>("fechaVencimiento")
        );

        // Indica que la columna de estado utilizará el atributo "estado".
        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        // Indica que la columna de renovación utilizará el atributo correspondiente.
        colRenovacionAutomatica.setCellValueFactory(
                new PropertyValueFactory<>("renovacionAutomatica")
        );

        // Indica que la columna del costo utilizará el atributo correspondiente.
        colCostoRenovacion.setCellValueFactory(
                new PropertyValueFactory<>("costoRenovacion")
        );

        // Carga los datos de las membresías en la tabla.
        cargarReporte();
    }

    /**
     * Carga en la tabla todas las membresías registradas.
     */
    @FXML
    public void cargarReporte() {

        // Crea una lista observable para almacenar las filas del reporte.
        ObservableList<ReporteFila> filas =
                FXCollections.observableArrayList();

        // Recorre todas las membresías registradas.
        for (Membresia membresia : pagoService.getMembresias()) {

            // Convierte cada membresía en una fila del reporte.
            filas.add(
                    convertirAFila(membresia)
            );
        }

        // Coloca todas las filas generadas dentro de la tabla.
        tablaReporte.setItems(filas);
    }

    /**
     * Exporta el contenido actual de la tabla
     * a un archivo CSV elegido por el usuario.
     */
    @FXML
    public void exportarCsv() {

        // Comprueba si la tabla no contiene ningún dato.
        if (tablaReporte.getItems().isEmpty()) {

            // Informa al usuario que no existen datos para exportar.
            mostrarAlerta(
                    "No hay datos para exportar."
            );

            // Detiene el proceso de exportación.
            return;
        }

        // Crea un selector de archivos para que el usuario elija dónde guardar el CSV.
        FileChooser selector =
                new FileChooser();

        // Establece el título de la ventana para guardar el archivo.
        selector.setTitle(
                "Guardar reporte como CSV"
        );

        // Establece el nombre inicial del archivo.
        selector.setInitialFileName(
                "reporte_membresias.csv"
        );

        // Limita el selector para mostrar archivos con extensión CSV.
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Archivo CSV",
                        "*.csv"
                )
        );

        // Muestra la ventana para seleccionar la ubicación del archivo.
        File archivo =
                selector.showSaveDialog(
                        tablaReporte.getScene().getWindow()
                );

        // Comprueba si el usuario canceló la selección del archivo.
        if (archivo == null) {
            return;
        }

        try (PrintWriter escritor =
                     new PrintWriter(
                             new FileWriter(archivo)
                     )) {

            // Escribe la primera fila del CSV con los nombres de las columnas.
            escritor.println(
                    "Socio,Cedula,Plan,Fecha Inicio,Fecha Vencimiento,"
                            + "Estado,Renovacion Automatica,Costo Renovacion"
            );

            // Recorre todas las filas que actualmente aparecen en la tabla.
            for (ReporteFila fila : tablaReporte.getItems()) {

                // Escribe los datos de cada fila en el archivo CSV.
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

            // Informa al usuario que el reporte fue exportado correctamente.
            mostrarAlerta(
                    "Reporte exportado correctamente a:\n"
                            + archivo.getAbsolutePath()
            );

        } catch (IOException e) {

            // Muestra un mensaje si ocurrió un error durante la exportación.
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

        // Obtiene y formatea la fecha de inicio de la membresía.
        String fechaInicio =
                membresia.getFechaInicio() == null
                        ? "-"
                        : membresia.getFechaInicio()
                        .format(FORMATO_FECHA);

        // Obtiene y formatea la fecha de vencimiento de la membresía.
        String fechaVencimiento =
                membresia.getFechaVencimiento() == null
                        ? "-"
                        : membresia.getFechaVencimiento()
                        .format(FORMATO_FECHA);

        // Determina si la membresía está vigente utilizando la fecha actual.
        String estado =
                membresia.estaVigente(LocalDate.now())
                        ? "Vigente"
                        : "Vencida";

        // Obtiene el plan asociado a la membresía.
        Plan plan =
                membresia.getPlan();

        /*
         * Si el plan implementa IRenovable, consultamos
         * si se renueva automáticamente y cuánto costaría
         * la renovación. Si no implementa la interfaz,
         * mostramos "N/A".
         */

        // Valor inicial utilizado cuando el plan no implementa IRenovable.
        String renovacionAutomatica = "N/A";

        // Valor inicial del costo cuando el plan no implementa IRenovable.
        String costoRenovacion = "N/A";

        // Comprueba si el plan implementa la interfaz IRenovable.
        if (plan instanceof IRenovable renovable) {

            // Obtiene si el plan se renueva automáticamente.
            renovacionAutomatica =
                    renovable.esRenovableAutomaticamente()
                            ? "Sí"
                            : "No";

            // Obtiene y formatea el costo de la renovación.
            costoRenovacion =
                    String.format(
                            "₡%,.0f",
                            renovable.costoRenovacion()
                    );
        }

        // Crea y devuelve una fila con toda la información de la membresía.
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

        // Comprueba si el valor recibido es nulo.
        if (valor == null) {

            // Devuelve una cadena vacía para evitar escribir un valor nulo.
            return "";
        }

        // Reemplaza las comillas dobles por dos comillas dobles para el formato CSV.
        String limpio =
                valor.replace(
                        "\"",
                        "\"\""
                );

        // Coloca el valor entre comillas para tratarlo como una celda del CSV.
        return "\"" + limpio + "\"";
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

            // Obtiene la ventana actual desde la tabla del reporte.
            Stage stage =
                    (Stage)
                            tablaReporte
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

        } catch (IOException e) {

            // Muestra un mensaje si no se pudo regresar al menú principal.
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

        // Crea una alerta de tipo informativo.
        Alert alerta =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        // Establece el título de la ventana de alerta.
        alerta.setTitle(
                "Reporte de membresías"
        );

        // Elimina el encabezado de la alerta.
        alerta.setHeaderText(
                null
        );

        // Establece el mensaje que se mostrará.
        alerta.setContentText(
                mensaje
        );

        // Muestra la alerta y espera a que el usuario la cierre.
        alerta.showAndWait();
    }

    /**
     * Clase auxiliar utilizada para mostrar
     * los datos de una membresía en la tabla.
     */
    public static class ReporteFila {

        // Nombre del socio asociado a la membresía.
        private final String socio;

        // Cédula del socio.
        private final String cedula;

        // Nombre del plan contratado.
        private final String plan;

        // Fecha de inicio de la membresía.
        private final String fechaInicio;

        // Fecha de vencimiento de la membresía.
        private final String fechaVencimiento;

        // Estado actual de la membresía.
        private final String estado;

        // Indica si el plan tiene renovación automática.
        private final String renovacionAutomatica;

        // Costo de la renovación del plan.
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

            // Guarda el nombre del socio.
            this.socio = socio;

            // Guarda la cédula del socio.
            this.cedula = cedula;

            // Guarda el nombre del plan.
            this.plan = plan;

            // Guarda la fecha de inicio.
            this.fechaInicio = fechaInicio;

            // Guarda la fecha de vencimiento.
            this.fechaVencimiento = fechaVencimiento;

            // Guarda el estado de la membresía.
            this.estado = estado;

            // Guarda la información sobre la renovación automática.
            this.renovacionAutomatica = renovacionAutomatica;

            // Guarda el costo de renovación.
            this.costoRenovacion = costoRenovacion;
        }

        // Obtiene el nombre del socio.
        public String getSocio() {

            // Devuelve el nombre almacenado.
            return socio;
        }

        // Obtiene la cédula del socio.
        public String getCedula() {

            // Devuelve la cédula almacenada.
            return cedula;
        }

        // Obtiene el nombre del plan.
        public String getPlan() {

            // Devuelve el nombre del plan almacenado.
            return plan;
        }

        // Obtiene la fecha de inicio.
        public String getFechaInicio() {

            // Devuelve la fecha de inicio almacenada.
            return fechaInicio;
        }

        // Obtiene la fecha de vencimiento.
        public String getFechaVencimiento() {

            // Devuelve la fecha de vencimiento almacenada.
            return fechaVencimiento;
        }

        // Obtiene el estado de la membresía.
        public String getEstado() {

            // Devuelve el estado almacenado.
            return estado;
        }

        // Obtiene la información de renovación automática.
        public String getRenovacionAutomatica() {

            // Devuelve la información de renovación almacenada.
            return renovacionAutomatica;
        }

        // Obtiene el costo de renovación.
        public String getCostoRenovacion() {

            // Devuelve el costo de renovación almacenado.
            return costoRenovacion;
        }
    }
}