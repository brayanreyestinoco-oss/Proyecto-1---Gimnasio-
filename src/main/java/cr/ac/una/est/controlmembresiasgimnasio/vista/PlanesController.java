package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.Pantalla;
import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanAnual;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanMensual;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanVIP;
import cr.ac.una.est.controlmembresiasgimnasio.repository.RepositorioException;
import cr.ac.una.est.controlmembresiasgimnasio.service.PlanService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * Controlador de la pantalla de planes.
 * Consulta, crea, modifica y elimina planes en MySQL a través de PlanService.
 */
public class PlanesController {

    // Textos del combo de tipo.
    private static final String TXT_MENSUAL = "Mensual";
    private static final String TXT_ANUAL = "Anual";
    private static final String TXT_VIP = "VIP";

    @FXML
    private TableView<PlanFila> tablaPlanes;

    @FXML
    private TableColumn<PlanFila, String> colNombre;

    @FXML
    private TableColumn<PlanFila, String> colPrecio;

    @FXML
    private TableColumn<PlanFila, String> colBeneficios;

    // Campo de búsqueda por nombre.
    @FXML
    private TextField txtBuscar;

    // Formulario de creación y edición.
    @FXML
    private ComboBox<String> cmbTipo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecioBase;

    @FXML
    private Label lblAjuste;

    @FXML
    private TextField txtAjuste;

    @FXML
    private TextField txtBeneficios;

    @FXML
    private Button btnEliminar;

    private PlanService planService;

    // Plan que se está editando; null cuando el formulario está en modo "nuevo".
    private Plan planEnEdicion;

    /**
     * Inicializa la pantalla de planes.
     */
    @FXML
    public void initialize() {

        planService = DatosAplicacion.getPlanService();

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colBeneficios.setCellValueFactory(new PropertyValueFactory<>("beneficios"));

        cmbTipo.setItems(FXCollections.observableArrayList(
                TXT_MENSUAL, TXT_ANUAL, TXT_VIP));

        // Al cambiar el tipo se adapta el campo de ajuste.
        cmbTipo.setOnAction(event -> actualizarCampoAjuste());

        // Al seleccionar una fila se carga el plan en el formulario.
        tablaPlanes.getSelectionModel().selectedItemProperty().addListener(
                (obs, anterior, seleccionada) -> {
                    if (seleccionada != null) {
                        cargarEnFormulario(seleccionada.getPlan());
                    }
                });

        nuevoPlan();
        mostrarTodos();
    }

    /**
     * Busca un plan por su nombre.
     */
    @FXML
    public void buscarPlan() {

        String nombreBuscado = txtBuscar.getText().trim();

        if (nombreBuscado.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Escribe el nombre de un plan para buscar.");
            return;
        }

        try {
            Plan planEncontrado = planService.buscarPorNombre(nombreBuscado);

            if (planEncontrado == null) {
                mostrarAlerta(Alert.AlertType.INFORMATION,
                        "No se encontró ningún plan llamado \""
                                + nombreBuscado + "\".");
                return;
            }

            ObservableList<PlanFila> filas = FXCollections.observableArrayList();
            filas.add(convertirAFila(planEncontrado));
            tablaPlanes.setItems(filas);

        } catch (RepositorioException e) {
            mostrarAlerta(Alert.AlertType.ERROR, e.getMessage()
                    + " Revisa la conexión con MySQL.");
        }
    }

    /**
     * Muestra todos los planes guardados en la base de datos.
     */
    @FXML
    public void mostrarTodos() {

        try {
            ObservableList<PlanFila> filas = FXCollections.observableArrayList();

            for (Plan plan : planService.getPlanesDisponibles()) {
                filas.add(convertirAFila(plan));
            }

            tablaPlanes.setItems(filas);
            txtBuscar.clear();

        } catch (RepositorioException e) {
            mostrarAlerta(Alert.AlertType.ERROR, e.getMessage()
                    + " Revisa la conexión con MySQL.");
        }
    }

    /**
     * Limpia el formulario para registrar un plan nuevo.
     */
    @FXML
    private void nuevoPlan() {

        planEnEdicion = null;
        tablaPlanes.getSelectionModel().clearSelection();

        cmbTipo.setDisable(false);
        cmbTipo.getSelectionModel().select(TXT_MENSUAL);
        txtNombre.clear();
        txtPrecioBase.clear();
        txtAjuste.clear();
        txtBeneficios.clear();
        btnEliminar.setDisable(true);

        actualizarCampoAjuste();
    }

    /**
     * Guarda el plan del formulario: lo crea si es nuevo o lo modifica
     * si hay uno seleccionado.
     */
    @FXML
    private void guardarPlan() {

        try {
            double precioBase = leerNumero(txtPrecioBase.getText(), "El precio base");
            double ajuste = 0;

            // El ajuste solo aplica a los planes Anual y VIP.
            if (!TXT_MENSUAL.equals(cmbTipo.getValue())) {
                ajuste = leerNumero(txtAjuste.getText(),
                        TXT_ANUAL.equals(cmbTipo.getValue())
                                ? "El descuento" : "El recargo");
            }

            if (planEnEdicion == null) {
                planService.crear(
                        codigoTipo(cmbTipo.getValue()),
                        txtNombre.getText(),
                        precioBase,
                        ajuste,
                        txtBeneficios.getText());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Plan creado correctamente.");
            } else {
                planService.modificar(
                        planEnEdicion.getIdPlan(),
                        txtNombre.getText(),
                        precioBase,
                        ajuste,
                        txtBeneficios.getText());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Plan modificado correctamente.");
            }

            nuevoPlan();
            mostrarTodos();

        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarAlerta(Alert.AlertType.WARNING, e.getMessage());
        } catch (RepositorioException e) {
            mostrarAlerta(Alert.AlertType.ERROR, e.getMessage()
                    + " Revisa la conexión con MySQL.");
        }
    }

    /**
     * Elimina el plan seleccionado, previa confirmación.
     */
    @FXML
    private void eliminarPlan() {

        if (planEnEdicion == null) {
            mostrarAlerta(Alert.AlertType.INFORMATION,
                    "Selecciona un plan de la tabla para eliminarlo.");
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Eliminar el plan \"" + planEnEdicion.getNombre() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Gestión de planes");
        confirmacion.setHeaderText(null);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.YES) {
            return;
        }

        try {
            planService.eliminar(planEnEdicion.getIdPlan());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Plan eliminado.");
            nuevoPlan();
            mostrarTodos();

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Plan inexistente o en uso por alguna membresía.
            mostrarAlerta(Alert.AlertType.WARNING, e.getMessage());
        } catch (RepositorioException e) {
            mostrarAlerta(Alert.AlertType.ERROR, e.getMessage()
                    + " Revisa la conexión con MySQL.");
        }
    }

    /**
     * Pone los datos de un plan en el formulario para editarlo.
     */
    private void cargarEnFormulario(Plan plan) {

        planEnEdicion = plan;

        // El tipo de un plan guardado no se puede cambiar.
        cmbTipo.setValue(textoTipo(plan));
        cmbTipo.setDisable(true);

        txtNombre.setText(plan.getNombre());
        txtPrecioBase.setText(formatearNumero(plan.getPrecioBase()));
        txtAjuste.setText(formatearNumero(plan.getAjuste()));
        txtBeneficios.setText(plan.getBeneficios());
        btnEliminar.setDisable(false);

        actualizarCampoAjuste();
    }

    /**
     * Cambia la etiqueta del campo de ajuste según el tipo elegido.
     */
    private void actualizarCampoAjuste() {

        String tipo = cmbTipo.getValue();

        if (TXT_ANUAL.equals(tipo)) {
            lblAjuste.setText("Descuento (%)");
            txtAjuste.setPromptText("Ej. 15");
            txtAjuste.setDisable(false);
        } else if (TXT_VIP.equals(tipo)) {
            lblAjuste.setText("Recargo VIP (₡)");
            txtAjuste.setPromptText("Ej. 10000");
            txtAjuste.setDisable(false);
        } else {
            lblAjuste.setText("Ajuste (no aplica)");
            txtAjuste.setPromptText("");
            txtAjuste.clear();
            txtAjuste.setDisable(true);
        }
    }

    // Convierte el texto del combo en el código guardado en MySQL.
    private String codigoTipo(String texto) {
        if (TXT_ANUAL.equals(texto)) {
            return PlanAnual.TIPO;
        }
        if (TXT_VIP.equals(texto)) {
            return PlanVIP.TIPO;
        }
        return PlanMensual.TIPO;
    }

    // Convierte el tipo de un plan en el texto que muestra el combo.
    private String textoTipo(Plan plan) {
        if (plan instanceof PlanAnual) {
            return TXT_ANUAL;
        }
        if (plan instanceof PlanVIP) {
            return TXT_VIP;
        }
        return TXT_MENSUAL;
    }

    // Lee un número del formulario; acepta coma o punto decimal.
    private double leerNumero(String texto, String nombreCampo) {
        String limpio = texto == null ? "" : texto.trim().replace(",", ".");
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException(nombreCampo + " es obligatorio.");
        }
        try {
            return Double.parseDouble(limpio);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nombreCampo + " debe ser un número.");
        }
    }

    // Muestra 15000 en lugar de 15000.0.
    private String formatearNumero(double valor) {
        return valor == Math.floor(valor)
                ? String.valueOf((long) valor)
                : String.valueOf(valor);
    }

    /**
     * Convierte un Plan en una fila para mostrarlo en la tabla.
     */
    private PlanFila convertirAFila(Plan plan) {
        return new PlanFila(
                plan,
                plan.getNombre(),
                String.format("₡%,.0f", plan.calcularPrecioFinal()),
                plan.getBeneficios()
        );
    }

    /**
     * Regresa al menú principal.
     */
    @FXML
    private void volverMenu() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(
                            "MenuPrincipalView.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage) tablaPlanes.getScene().getWindow();

            Pantalla.cambiar(stage, root);
        } catch (IOException e) {

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo regresar al menú principal."
            );
        }

    }


    /**
     * Muestra una alerta del tipo indicado.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Gestión de planes");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Fila de la tabla: datos para mostrar más el Plan original,
     * que se usa para llenar el formulario al seleccionarla.
     */
    public static class PlanFila {

        private final Plan plan;
        private final String nombre;
        private final String precio;
        private final String beneficios;

        public PlanFila(Plan plan, String nombre, String precio, String beneficios) {
            this.plan = plan;
            this.nombre = nombre;
            this.precio = precio;
            this.beneficios = beneficios;
        }

        public Plan getPlan() {
            return plan;
        }

        public String getNombre() {
            return nombre;
        }

        public String getPrecio() {
            return precio;
        }

        public String getBeneficios() {
            return beneficios;
        }
    }
}