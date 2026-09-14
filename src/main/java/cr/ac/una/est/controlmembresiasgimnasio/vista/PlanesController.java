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

    // Tabla donde se muestran los planes disponibles.
    @FXML
    private TableView<PlanFila> tablaPlanes;

    // Columna que muestra el nombre del plan.
    @FXML
    private TableColumn<PlanFila, String> colNombre;

    // Columna que muestra el precio del plan.
    @FXML
    private TableColumn<PlanFila, String> colPrecio;

    // Columna que muestra los beneficios del plan.
    @FXML
    private TableColumn<PlanFila, String> colBeneficios;

    // Campo de texto utilizado para buscar un plan por su nombre.
    @FXML
    private TextField txtBuscar;

    // Servicio encargado de gestionar los planes.
    private PlanService planService;

    /**
     * Inicializa la pantalla de planes.
     */
    @FXML
    public void initialize() {

        // Obtiene el servicio de planes utilizado por la aplicación.
        planService =
                DatosAplicacion.getPlanService();

        // Indica que la columna de nombre utilizará el atributo "nombre".
        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        // Indica que la columna de precio utilizará el atributo "precio".
        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio")
        );

        // Indica que la columna de beneficios utilizará el atributo "beneficios".
        colBeneficios.setCellValueFactory(
                new PropertyValueFactory<>("beneficios")
        );

        // Carga y muestra todos los planes disponibles.
        mostrarTodos();
    }

    /**
     * Busca un plan por su nombre.
     */
    @FXML
    public void buscarPlan() {

        // Obtiene el texto ingresado en el campo de búsqueda y elimina espacios.
        String nombreBuscado =
                txtBuscar
                        .getText()
                        .trim();

        // Comprueba si el campo de búsqueda está vacío.
        if (nombreBuscado.isEmpty()) {

            // Muestra un mensaje indicando que se debe ingresar un nombre.
            mostrarAlerta(
                    "Escribe el nombre de un plan para buscar."
            );

            // Detiene la ejecución porque no existe un criterio de búsqueda.
            return;
        }

        // Busca un plan utilizando el nombre proporcionado.
        Plan planEncontrado =
                planService.buscarPorNombre(
                        nombreBuscado
                );

        // Comprueba si no se encontró ningún plan.
        if (planEncontrado == null) {

            // Informa al usuario que no existe un plan con ese nombre.
            mostrarAlerta(
                    "No se encontró ningún plan llamado \""
                            + nombreBuscado
                            + "\"."
            );

            // Detiene la ejecución porque no existe un plan para mostrar.
            return;
        }

        // Crea una lista observable para almacenar el resultado de la búsqueda.
        ObservableList<PlanFila> filas =
                FXCollections.observableArrayList();

        // Convierte el plan encontrado en una fila para la tabla.
        filas.add(
                convertirAFila(
                        planEncontrado
                )
        );

        // Muestra en la tabla únicamente el plan encontrado.
        tablaPlanes.setItems(
                filas
        );
    }

    /**
     * Muestra todos los planes disponibles.
     */
    @FXML
    public void mostrarTodos() {

        // Crea una lista observable para almacenar los planes que se mostrarán.
        ObservableList<PlanFila> filas =
                FXCollections.observableArrayList();

        // Recorre todos los planes disponibles en el servicio.
        for (Plan plan :
                planService.getPlanesDisponibles()) {

            // Convierte cada plan en una fila para la tabla.
            filas.add(
                    convertirAFila(
                            plan
                    )
            );
        }

        // Coloca todas las filas en la tabla.
        tablaPlanes.setItems(
                filas
        );

        // Limpia el campo de búsqueda después de mostrar todos los planes.
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

        // Crea y devuelve una fila con los datos necesarios del plan.
        return new PlanFila(
                // Obtiene el nombre del plan.
                plan.getNombre(),

                // Calcula el precio final y lo convierte a formato de colones.
                String.format(
                        "₡%,.0f",
                        plan.calcularPrecioFinal()
                ),

                // Obtiene los beneficios asociados al plan.
                plan.getBeneficios()
        );
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

            // Obtiene la ventana actual desde la tabla de planes.
            Stage stage =
                    (Stage)
                            tablaPlanes
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

            // Muestra un mensaje si no se pudo cargar el menú principal.
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

        // Crea una alerta de tipo informativo.
        Alert alerta =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        // Establece el título de la ventana de alerta.
        alerta.setTitle(
                "Gestión de planes"
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
     * los datos de un plan en la tabla.
     */
    public static class PlanFila {

        // Nombre del plan que se mostrará en la tabla.
        private final String nombre;

        // Precio formateado del plan.
        private final String precio;

        // Beneficios que ofrece el plan.
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

            // Guarda el nombre recibido para la fila.
            this.nombre =
                    nombre;

            // Guarda el precio recibido para la fila.
            this.precio =
                    precio;

            // Guarda los beneficios recibidos para la fila.
            this.beneficios =
                    beneficios;
        }

        /**
         * Obtiene el nombre.
         *
         * @return nombre del plan
         */
        public String getNombre() {

            // Devuelve el nombre almacenado.
            return nombre;
        }

        /**
         * Obtiene el precio.
         *
         * @return precio del plan
         */
        public String getPrecio() {

            // Devuelve el precio almacenado.
            return precio;
        }

        /**
         * Obtiene los beneficios.
         *
         * @return beneficios del plan
         */
        public String getBeneficios() {

            // Devuelve los beneficios almacenados.
            return beneficios;
        }
    }
}