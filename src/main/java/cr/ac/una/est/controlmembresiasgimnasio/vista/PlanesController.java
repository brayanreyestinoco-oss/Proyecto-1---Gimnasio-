package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.service.PlanService;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador encargado de mostrar
 * y consultar los planes disponibles.
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

    private PlanService planService;

    /**
     * Inicializa la pantalla de planes.
     */
    @FXML
    public void initialize() {

        planService =
                DatosAplicacion.getPlanService();

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio")
        );

        colBeneficios.setCellValueFactory(
                new PropertyValueFactory<>("beneficios")
        );

        mostrarTodos();
    }

    /**
     * Busca un plan por su nombre.
     */
    @FXML
    public void buscarPlan() {

        String nombreBuscado =
                txtBuscar
                        .getText()
                        .trim();

        if (nombreBuscado.isEmpty()) {

            mostrarAlerta(
                    "Escribe el nombre de un plan para buscar."
            );

            return;
        }

        Plan planEncontrado =
                planService.buscarPorNombre(
                        nombreBuscado
                );

        if (planEncontrado == null) {

            mostrarAlerta(
                    "No se encontró ningún plan llamado \""
                            + nombreBuscado
                            + "\"."
            );

            return;
        }

        ObservableList<PlanFila> filas =
                FXCollections.observableArrayList();

        filas.add(
                convertirAFila(
                        planEncontrado
                )
        );

        tablaPlanes.setItems(
                filas
        );
    }

    /**
     * Muestra todos los planes disponibles.
     */
    @FXML
    public void mostrarTodos() {

        ObservableList<PlanFila> filas =
                FXCollections.observableArrayList();

        for (Plan plan :
                planService.getPlanesDisponibles()) {

            filas.add(
                    convertirAFila(
                            plan
                    )
            );
        }

        tablaPlanes.setItems(
                filas
        );

        txtBuscar.clear();
    }

    /**
     * Convierte un Plan en una fila
     * para mostrarlo en la tabla.
     *
     * @param plan plan que se desea mostrar
     * @return fila preparada para la tabla
     */
    private PlanFila convertirAFila(
            Plan plan) {

        return new PlanFila(
                plan.getNombre(),
                String.format(
                        "₡%,.0f",
                        plan.calcularPrecioFinal()
                ),
                plan.getBeneficios()
        );
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
                            tablaPlanes
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
     * Muestra una alerta.
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
                "Gestión de planes"
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
     * los datos de un plan en la tabla.
     */
    public static class PlanFila {

        private final String nombre;

        private final String precio;

        private final String beneficios;

        /**
         * Crea una fila para la tabla.
         *
         * @param nombre nombre del plan
         * @param precio precio del plan
         * @param beneficios beneficios del plan
         */
        public PlanFila(
                String nombre,
                String precio,
                String beneficios) {

            this.nombre =
                    nombre;

            this.precio =
                    precio;

            this.beneficios =
                    beneficios;
        }

        /**
         * Obtiene el nombre.
         *
         * @return nombre del plan
         */
        public String getNombre() {

            return nombre;
        }

        /**
         * Obtiene el precio.
         *
         * @return precio del plan
         */
        public String getPrecio() {

            return precio;
        }

        /**
         * Obtiene los beneficios.
         *
         * @return beneficios del plan
         */
        public String getBeneficios() {

            return beneficios;
        }
    }
}