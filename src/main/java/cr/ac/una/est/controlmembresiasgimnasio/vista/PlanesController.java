package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.service.PlanService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

        cargarPlanes();
    }

    /**
     * Convierte los objetos Plan del servicio en filas visuales (PlanFila)
     * y las coloca en la tabla.
     */
    private void cargarPlanes() {
        ObservableList<PlanFila> filas = FXCollections.observableArrayList();
        for (Plan plan : planService.getPlanesDisponibles()) {
            filas.add(new PlanFila(
                    plan.getNombre(),
                    String.format("₡%,.0f", plan.calcularPrecioFinal()),
                    plan.getBeneficios()
            ));
        }
        tablaPlanes.setItems(filas);
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

        /**
         * Crea una fila con los datos ya formateados para mostrar.
         * @param nombre nombre del plan
         * @param precio precio ya formateado como texto
         * @param beneficios descripción de beneficios
         */
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
