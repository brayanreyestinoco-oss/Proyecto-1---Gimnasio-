package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.service.PlanService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controlador de la vista de Gestión de Planes. Se encarga únicamente
 * de mostrar los datos que le entrega PlanService; no hace cálculos.
 */
public class PlanesController {

    @FXML
    private TableView<PlanFila> tablaPlanes;

    @FXML
    private TableColumn<PlanFila, String> colNombre;

    @FXML
    private TableColumn<PlanFila, String> colPrecio;

    @FXML
    private TableColumn<PlanFila, String> colBeneficios;

    @FXML
    private TextField txtBuscar;

    private final PlanService planService = new PlanService();

    /**
     * Método que JavaFX ejecuta automáticamente al cargar el FXML.
     * Configura las columnas de la tabla y carga los planes disponibles.
     */
    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colBeneficios.setCellValueFactory(new PropertyValueFactory<>("beneficios"));

        mostrarTodos();
    }

    /**
     * Busca un plan por el nombre escrito en el campo de texto y muestra
     * únicamente ese resultado en la tabla. Si no existe, muestra una
     * alerta al usuario.
     */
    @FXML
    public void buscarPlan() {
        String nombreBuscado = txtBuscar.getText().trim();

        if (nombreBuscado.isEmpty()) {
            mostrarAlerta("Escribe el nombre de un plan para buscar.");
            return;
        }

        Plan planEncontrado = planService.buscarPorNombre(nombreBuscado);

        if (planEncontrado == null) {
            mostrarAlerta("No se encontró ningún plan llamado \"" + nombreBuscado + "\".");
            return;
        }

        ObservableList<PlanFila> filas = FXCollections.observableArrayList();
        filas.add(convertirAFila(planEncontrado));
        tablaPlanes.setItems(filas);
    }

    /**
     * Vuelve a mostrar todos los planes disponibles en la tabla,
     * deshaciendo cualquier filtro de búsqueda anterior.
     */
    @FXML
    public void mostrarTodos() {
        ObservableList<PlanFila> filas = FXCollections.observableArrayList();
        for (Plan plan : planService.getPlanesDisponibles()) {
            filas.add(convertirAFila(plan));
        }
        tablaPlanes.setItems(filas);
        txtBuscar.clear();
    }

    /**
     * Convierte un objeto Plan del modelo en una fila lista para mostrar
     * en la tabla (con el precio ya formateado como texto).
     * @param plan el plan a convertir
     * @return la fila correspondiente
     */
    private PlanFila convertirAFila(Plan plan) {
        return new PlanFila(
                plan.getNombre(),
                String.format("₡%,.0f", plan.calcularPrecioFinal()),
                plan.getBeneficios()
        );
    }

    /**
     * Muestra una ventana emergente de alerta con el mensaje indicado.
     * @param mensaje texto a mostrar al usuario
     */
    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Búsqueda de planes");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Clase auxiliar interna que representa una fila de la tabla.
     * TableView necesita objetos "planos" con getters, no la jerarquía
     * de Plan directamente.
     */
    public static class PlanFila {
        private final String nombre;
        private final String precio;
        private final String beneficios;

        public PlanFila(String nombre, String precio, String beneficios) {
            this.nombre = nombre;
            this.precio = precio;
            this.beneficios = beneficios;
        }

        public String getNombre() { return nombre; }
        public String getPrecio() { return precio; }
        public String getBeneficios() { return beneficios; }
    }
}
