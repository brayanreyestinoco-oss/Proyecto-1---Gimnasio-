package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Pago;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;
import cr.ac.una.est.controlmembresiasgimnasio.service.PagoService;
import cr.ac.una.est.controlmembresiasgimnasio.service.PlanService;
import cr.ac.una.est.controlmembresiasgimnasio.service.SocioService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Controlador encargado de registrar
 * los pagos de membresías de los socios.
 */
public class RegistrarPagoController {

    // Campo donde se ingresa la cédula del socio.
    @FXML
    private TextField txtCedula;

    // Lista desplegable donde se selecciona el plan.
    @FXML
    private ComboBox<String> cmbPlan;

    // Campo donde se muestra o ingresa el monto del pago.
    @FXML
    private TextField txtMonto;

    // Selector utilizado para elegir la fecha de inicio del pago.
    @FXML
    private DatePicker dpFechaInicio;

    // Lista desplegable donde se selecciona el método de pago.
    @FXML
    private ComboBox<String> cmbMetodoPago;

    // Etiqueta donde se muestra la fecha de vencimiento calculada.
    @FXML
    private Label lblVencimiento;

    // Servicio encargado de buscar y gestionar los socios.
    private SocioService socioService;

    // Servicio encargado de consultar los planes disponibles.
    private PlanService planService;

    // Servicio encargado de registrar pagos y membresías.
    private PagoService pagoService;

    /**
     * Inicializa el controlador y carga
     * los planes y métodos de pago.
     */
    @FXML
    public void initialize() {

        // Obtiene el servicio de socios de la aplicación.
        socioService =
                DatosAplicacion.getSocioService();

        // Obtiene el servicio de planes de la aplicación.
        planService =
                DatosAplicacion.getPlanService();

        // Obtiene el servicio de pagos de la aplicación.
        pagoService =
                DatosAplicacion.getPagoService();

        // Carga los planes disponibles en el ComboBox.
        cargarPlanes();

        // Carga los métodos de pago disponibles.
        cargarMetodosPago();

        // Establece la fecha actual como fecha inicial.
        dpFechaInicio.setValue(
                LocalDate.now()
        );

        /*
         * Cuando el usuario selecciona un plan,
         * mostramos automáticamente su precio.
         */
        // Ejecuta la actualización de datos cuando cambia el plan seleccionado.
        cmbPlan.setOnAction(
                event -> actualizarDatosPlan()
        );

        /*
         * Si cambia la fecha también actualizamos
         * la fecha estimada de vencimiento.
         */
        // Ejecuta la actualización cuando cambia la fecha seleccionada.
        dpFechaInicio.setOnAction(
                event -> actualizarDatosPlan()
        );
    }

    /**
     * Carga los planes disponibles
     * dentro del ComboBox.
     */
    private void cargarPlanes() {

        // Elimina cualquier elemento que estuviera previamente cargado.
        cmbPlan.getItems().clear();

        // Recorre todos los planes disponibles.
        for (Plan plan :
                planService.getPlanesDisponibles()) {

            // Agrega el nombre del plan al ComboBox.
            cmbPlan.getItems().add(
                    plan.getNombre()
            );
        }
    }

    /**
     * Carga los métodos de pago disponibles.
     */
    private void cargarMetodosPago() {

        // Agrega las opciones disponibles para realizar el pago.
        cmbMetodoPago
                .getItems()
                .addAll(
                        "Efectivo",
                        "Tarjeta",
                        "Transferencia"
                );
    }

    /**
     * Actualiza automáticamente el monto
     * y el vencimiento estimado según
     * el plan seleccionado.
     */
    private void actualizarDatosPlan() {

        // Obtiene el nombre del plan seleccionado.
        String nombrePlan =
                cmbPlan.getValue();

        // Obtiene la fecha seleccionada como inicio de la membresía.
        LocalDate fecha =
                dpFechaInicio.getValue();

        // Comprueba si todavía no se ha seleccionado ningún plan.
        if (nombrePlan == null) {

            // Finaliza el método porque no hay un plan que consultar.
            return;
        }

        // Busca el plan seleccionado por su nombre.
        Plan plan =
                planService.buscarPorNombre(
                        nombrePlan
                );

        // Comprueba si el plan no fue encontrado.
        if (plan == null) {

            // Finaliza el método porque no existe un plan válido.
            return;
        }

        // Muestra automáticamente el precio final del plan.
        txtMonto.setText(
                String.valueOf(
                        plan.calcularPrecioFinal()
                )
        );

        // Comprueba que exista una fecha de inicio.
        if (fecha != null) {

            // Calcula la fecha de vencimiento según la duración del plan.
            LocalDate vencimiento =
                    fecha.plusMonths(
                            plan.getDuracionEnMeses()
                    );

            // Muestra la fecha de vencimiento calculada.
            lblVencimiento.setText(
                    vencimiento.toString()
            );
        }
    }

    /**
     * Registra el pago de una membresía.
     */
    @FXML
    private void guardarPago() {

        // Comprueba que todos los campos necesarios estén completos.
        if (!camposValidos()) {

            // Muestra una advertencia si faltan datos.
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Debe completar todos los datos del pago."
            );

            // Detiene el proceso de registro.
            return;
        }

        /*
         * Primero buscamos al socio utilizando
         * la cédula escrita en el formulario.
         */
        // Busca al socio utilizando la cédula ingresada.
        Socio socio =
                socioService.buscarPorCedula(
                        txtCedula
                                .getText()
                                .trim()
                );

        // Comprueba si el socio no existe.
        if (socio == null) {

            // Muestra una advertencia indicando que no se encontró el socio.
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Socio no encontrado",
                    "No existe un socio registrado con esa cédula."
            );

            // Detiene el proceso de registro.
            return;
        }

        /*
         * Obtenemos el plan seleccionado.
         */
        // Busca el plan seleccionado por su nombre.
        Plan plan =
                planService.buscarPorNombre(
                        cmbPlan.getValue()
                );

        // Comprueba si el plan no es válido.
        if (plan == null) {

            // Informa al usuario que debe seleccionar un plan válido.
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Plan inválido",
                    "Debe seleccionar un plan válido."
            );

            // Detiene el proceso de registro.
            return;
        }

        // Variable que almacenará el monto del pago.
        double monto;

        try {

            // Convierte el texto del monto a un número decimal.
            monto =
                    Double.parseDouble(
                            txtMonto
                                    .getText()
                                    .trim()
                    );

        } catch (NumberFormatException e) {

            // Muestra una advertencia si el monto no tiene un formato válido.
            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Monto inválido",
                    "El monto debe ser un número válido."
            );

            // Detiene el proceso de registro.
            return;
        }

        // Obtiene la fecha seleccionada para realizar el pago.
        LocalDate fechaPago =
                dpFechaInicio.getValue();

        // Obtiene el método de pago seleccionado.
        String metodoPago =
                cmbMetodoPago.getValue();

        try {

            /*
             * Buscamos si el socio ya posee
             * una membresía.
             */
            // Busca una membresía asociada al socio.
            Membresia membresia =
                    pagoService
                            .buscarMembresiaPorSocio(
                                    socio.getIdSocio()
                            );

            /*
             * Si todavía no tiene membresía,
             * se crea una nueva.
             */
            // Comprueba si el socio todavía no tiene una membresía.
            if (membresia == null) {

                // Obtiene un identificador disponible para la nueva membresía.
                int idMembresia =
                        obtenerSiguienteIdMembresia();

                // Registra una nueva membresía para el socio.
                membresia =
                        pagoService.registrarMembresia(
                                idMembresia,
                                socio,
                                plan,
                                fechaPago
                        );

            } else {

                /*
                 * Si ya existe, permitimos actualizar
                 * el plan antes de registrar
                 * la nueva renovación.
                 */
                // Actualiza el plan de la membresía existente.
                membresia.setPlan(
                        plan
                );
            }

            // Obtiene un identificador disponible para el nuevo pago.
            int idPago =
                    obtenerSiguienteIdPago();

            // Registra el nuevo pago asociado a la membresía.
            Pago pago =
                    pagoService.registrarPago(
                            idPago,
                            membresia,
                            monto,
                            fechaPago,
                            metodoPago
                    );

            // Muestra la fecha de vencimiento de la membresía actualizada.
            lblVencimiento.setText(
                    membresia
                            .getFechaVencimiento()
                            .toString()
            );

            // Muestra un mensaje confirmando que el pago fue registrado.
            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Pago registrado",
                    "El pago se registró correctamente.\n"
                            + "Número de pago: "
                            + pago.getIdPago()
                            + "\nVencimiento: "
                            + membresia.getFechaVencimiento()
            );

            // Limpia los datos ingresados después de registrar el pago.
            limpiarFormulario();

        } catch (IllegalArgumentException e) {

            // Muestra el mensaje generado cuando ocurre un error de validación.
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "No se pudo registrar",
                    e.getMessage()
            );
        }
    }

    /**
     * Obtiene el siguiente identificador
     * disponible para una membresía.
     *
     * @return siguiente identificador
     */
    private int obtenerSiguienteIdMembresia() {

        // Variable utilizada para almacenar el identificador más alto encontrado.
        int mayorId = 0;

        // Recorre todas las membresías registradas.
        for (Membresia membresia :
                pagoService.getMembresias()) {

            // Comprueba si el identificador actual es mayor que el registrado.
            if (membresia.getIdMembresia()
                    > mayorId) {

                // Actualiza el mayor identificador encontrado.
                mayorId =
                        membresia.getIdMembresia();
            }
        }

        // Devuelve el siguiente identificador disponible.
        return mayorId + 1;
    }

    /**
     * Obtiene el siguiente identificador
     * disponible para un pago.
     *
     * @return siguiente identificador
     */
    private int obtenerSiguienteIdPago() {

        // Variable utilizada para almacenar el identificador más alto encontrado.
        int mayorId = 0;

        // Recorre todos los pagos registrados.
        for (Pago pago :
                pagoService.getPagos()) {

            // Comprueba si el identificador actual es mayor que el registrado.
            if (pago.getIdPago()
                    > mayorId) {

                // Actualiza el mayor identificador encontrado.
                mayorId =
                        pago.getIdPago();
            }
        }

        // Devuelve el siguiente identificador disponible.
        return mayorId + 1;
    }

    /**
     * Verifica que los campos necesarios
     * estén completos.
     *
     * @return true si los datos son válidos
     */
    private boolean camposValidos() {

        // Comprueba que la cédula no esté vacía,
        // que exista un plan, que haya un monto,
        // que exista una fecha y que se haya seleccionado un método de pago.
        return !txtCedula
                .getText()
                .isBlank()

                && cmbPlan.getValue() != null

                && !txtMonto
                .getText()
                .isBlank()

                && dpFechaInicio.getValue() != null

                && cmbMetodoPago.getValue() != null;
    }

    /**
     * Limpia el formulario.
     */
    @FXML
    private void cancelar() {

        // Limpia todos los datos ingresados en el formulario.
        limpiarFormulario();
    }

    /**
     * Limpia los campos del formulario.
     */
    private void limpiarFormulario() {

        // Limpia el campo de cédula.
        txtCedula.clear();

        // Quita la selección actual del plan.
        cmbPlan.getSelectionModel()
                .clearSelection();

        // Limpia el campo del monto.
        txtMonto.clear();

        // Restablece la fecha de inicio a la fecha actual.
        dpFechaInicio.setValue(
                LocalDate.now()
        );

        // Quita la selección actual del método de pago.
        cmbMetodoPago
                .getSelectionModel()
                .clearSelection();

        // Restablece el mensaje de vencimiento.
        lblVencimiento.setText(
                "Se calculará automáticamente"
        );
    }

    /**
     * Regresa a la pantalla de pagos.
     */
    @FXML
    private void volverPagos() {

        try {

            // Crea el cargador para abrir la vista de pagos.
            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "PagosView.fxml"
                                    )
                    );

            // Carga la estructura visual de la pantalla de pagos.
            Parent root =
                    loader.load();

            // Obtiene la ventana actual desde el campo de cédula.
            Stage stage =
                    (Stage)
                            txtCedula
                                    .getScene()
                                    .getWindow();

            // Cambia la escena actual por la pantalla de pagos.
            stage.setScene(
                    new Scene(
                            root,
                            600,
                            400
                    )
            );

        } catch (IOException e) {

            // Muestra una alerta si no se pudo cargar la pantalla de pagos.
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo regresar a la pantalla de pagos."
            );
        }
    }

    /**
     * Muestra un mensaje al usuario.
     *
     * @param tipo tipo de alerta
     * @param titulo título de la alerta
     * @param mensaje mensaje que se mostrará
     */
    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        // Crea una alerta utilizando el tipo indicado.
        Alert alerta =
                new Alert(tipo);

        // Establece el título de la ventana de alerta.
        alerta.setTitle(
                titulo
        );

        // Elimina el encabezado de la alerta.
        alerta.setHeaderText(
                null
        );

        // Establece el mensaje que se mostrará al usuario.
        alerta.setContentText(
                mensaje
        );

        // Muestra la alerta y espera a que el usuario la cierre.
        alerta.showAndWait();
    }
}