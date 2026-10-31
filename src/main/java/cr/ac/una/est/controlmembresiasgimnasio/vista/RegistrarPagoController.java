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
 * Controlador encargado de registrar pagos de membresías.
 *
 * Utiliza PagoService para guardar los datos en MySQL.
 * Los identificadores son generados por la base de datos.
 */
public class RegistrarPagoController {

    // Campo para ingresar la cédula del socio.
    @FXML
    private TextField txtCedula;

    // Lista de planes disponibles.
    @FXML
    private ComboBox<String> cmbPlan;

    // Campo para ingresar o mostrar el monto.
    @FXML
    private TextField txtMonto;

    // Selector de fecha del pago.
    @FXML
    private DatePicker dpFechaInicio;

    // Lista de métodos de pago.
    @FXML
    private ComboBox<String> cmbMetodoPago;

    // Etiqueta que muestra el vencimiento.
    @FXML
    private Label lblVencimiento;

    // Servicios existentes del proyecto.
    private SocioService socioService;
    private PlanService planService;
    private PagoService pagoService;

    /**
     * Inicializa el formulario.
     */
    @FXML
    public void initialize() {

        // Recuperar los servicios compartidos.
        socioService = DatosAplicacion.getSocioService();
        planService = DatosAplicacion.getPlanService();
        pagoService = DatosAplicacion.getPagoService();

        // Cargar opciones del formulario.
        cargarPlanes();
        cargarMetodosPago();

        // Establecer la fecha actual.
        dpFechaInicio.setValue(LocalDate.now());

        // Actualizar el monto al seleccionar un plan.
        cmbPlan.setOnAction(
                event -> actualizarDatosPlan()
        );

        // Actualizar el vencimiento al cambiar la fecha.
        dpFechaInicio.setOnAction(
                event -> actualizarDatosPlan()
        );
    }

    /**
     * Carga los planes registrados.
     */
    private void cargarPlanes() {

        cmbPlan.getItems().clear();

        for (Plan plan : planService.getPlanesDisponibles()) {

            cmbPlan.getItems().add(
                    plan.getNombre()
            );
        }
    }

    /**
     * Carga los métodos de pago disponibles.
     */
    private void cargarMetodosPago() {

        cmbMetodoPago.getItems().clear();

        cmbMetodoPago.getItems().addAll(
                "Efectivo",
                "Tarjeta",
                "Transferencia"
        );
    }

    /**
     * Actualiza el precio y el vencimiento estimado.
     */
    private void actualizarDatosPlan() {

        String nombrePlan = cmbPlan.getValue();
        LocalDate fecha = dpFechaInicio.getValue();

        if (nombrePlan == null || fecha == null) {
            return;
        }

        Plan plan = planService.buscarPorNombre(nombrePlan);

        if (plan == null) {
            return;
        }

        // Mostrar el precio calculado del plan.
        txtMonto.setText(
                String.valueOf(plan.calcularPrecioFinal())
        );

        // Calcular el vencimiento estimado.
        LocalDate vencimiento = fecha.plusMonths(
                plan.getDuracionEnMeses()
        );

        lblVencimiento.setText(
                vencimiento.toString()
        );
    }

    /**
     * Registra un pago nuevo o una renovación.
     *
     * Para un socio sin membresía utiliza registrarPrimerPago().
     * Para una membresía existente utiliza registrarPago().
     */
    @FXML
    private void guardarPago() {

        // 1. Verificar que los campos estén completos.
        if (!camposValidos()) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Debe completar todos los datos del pago."
            );

            return;
        }

        try {

            // 2. Obtener la cédula ingresada.
            String cedula = txtCedula.getText().trim();

            // 3. Buscar al socio en el servicio existente.
            Socio socio = socioService.buscarPorCedula(cedula);

            if (socio == null) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Socio no encontrado",
                        "No existe un socio registrado con esa cédula."
                );

                return;
            }

            // 4. Buscar el plan seleccionado.
            Plan plan = planService.buscarPorNombre(
                    cmbPlan.getValue()
            );

            if (plan == null) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Plan inválido",
                        "Debe seleccionar un plan válido."
                );

                return;
            }

            // 5. Convertir el monto a un número.
            double monto;

            try {

                monto = Double.parseDouble(
                        txtMonto.getText().trim()
                );

            } catch (NumberFormatException e) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Monto inválido",
                        "El monto debe ser un número válido."
                );

                return;
            }

            // 6. Validar que el monto sea positivo y finito.
            if (!Double.isFinite(monto) || monto <= 0) {

                mostrarMensaje(
                        Alert.AlertType.WARNING,
                        "Monto inválido",
                        "El monto debe ser mayor que cero."
                );

                return;
            }

            // 7. Recuperar fecha y método de pago.
            LocalDate fechaPago = dpFechaInicio.getValue();
            String metodoPago = cmbMetodoPago.getValue();

            // 8. Comprobar que el socio exista en MySQL.
            if (socio.getIdSocio() <= 0) {

                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "Socio sin identificador",
                        "El socio debe estar registrado en MySQL."
                );

                return;
            }

            // 9. Comprobar que el plan tenga un ID de MySQL.
            if (plan.getIdPlan() <= 0) {

                mostrarMensaje(
                        Alert.AlertType.ERROR,
                        "Plan sin identificador",
                        "El plan debe estar registrado en MySQL."
                );

                return;
            }

            // 10. Buscar si el socio ya tiene una membresía.
            Membresia membresia =
                    pagoService.buscarMembresiaPorCedula(cedula);

            Pago pago;

            if (membresia == null) {

                /*
                 * PRIMER PAGO
                 *
                 * Crear la membresía y registrar el pago
                 * dentro de una sola transacción.
                 *
                 * MySQL genera ambos identificadores.
                 */
                pago = pagoService.registrarPrimerPago(
                        socio,
                        plan,
                        monto,
                        fechaPago,
                        metodoPago
                );

                // Recuperar la membresía creada.
                membresia = pago.getMembresia();

            } else {

                /*
                 * RENOVACIÓN
                 *
                 * La membresía ya existe.
                 * Se permite cambiar el plan antes de pagar.
                 */

                // Conservar el plan anterior por si falla el pago.
                Plan planAnterior = membresia.getPlan();

                try {

                    // Actualizar el plan seleccionado.
                    membresia.setPlan(plan);

                    /*
                     * Registrar el pago y renovar
                     * la membresía mediante PagoService.
                     *
                     * MySQL genera el ID del pago.
                     */
                    pago = pagoService.registrarPago(
                            0,
                            membresia,
                            monto,
                            fechaPago,
                            metodoPago
                    );

                } catch (RuntimeException e) {

                    // Restaurar el plan anterior si falla.
                    membresia.setPlan(planAnterior);

                    throw e;
                }
            }

            // 11. Mostrar el vencimiento real guardado.
            LocalDate vencimiento =
                    membresia.getFechaVencimiento();

            if (vencimiento != null) {
                lblVencimiento.setText(
                        vencimiento.toString()
                );
            }

            // 12. Confirmar al usuario el registro.
            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Pago registrado",
                    "El pago se registró correctamente.\n"
                            + "Número de pago: "
                            + pago.getIdPago()
                            + "\nVencimiento: "
                            + vencimiento
            );

            // 13. Limpiar el formulario.
            limpiarFormulario();

        } catch (IllegalArgumentException e) {

            // Errores de validación.
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Datos inválidos",
                    e.getMessage()
            );

        } catch (IllegalStateException e) {

            // Errores de conexión o persistencia JDBC.
            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error al registrar el pago",
                    e.getMessage()
            );
        }
    }

    /**
     * Comprueba que los campos obligatorios estén completos.
     */
    private boolean camposValidos() {

        return txtCedula.getText() != null
                && !txtCedula.getText().isBlank()

                && cmbPlan.getValue() != null

                && txtMonto.getText() != null
                && !txtMonto.getText().isBlank()

                && dpFechaInicio.getValue() != null

                && cmbMetodoPago.getValue() != null;
    }

    /**
     * Cancela la entrada de datos del formulario.
     */
    @FXML
    private void cancelar() {

        limpiarFormulario();
    }

    /**
     * Restablece todos los campos del formulario.
     */
    private void limpiarFormulario() {

        txtCedula.clear();

        cmbPlan.getSelectionModel()
                .clearSelection();

        txtMonto.clear();

        dpFechaInicio.setValue(LocalDate.now());

        cmbMetodoPago.getSelectionModel()
                .clearSelection();

        lblVencimiento.setText(
                "Se calculará automáticamente"
        );
    }

    /**
     * Regresa a la pantalla de pagos.
     *
     * Conserva la navegación original del proyecto.
     */
    @FXML
    private void volverPagos() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    HelloApplication.class.getResource(
                            "PagosView.fxml"
                    )
            );

            Parent root = loader.load();

            Stage stage = (Stage) txtCedula
                    .getScene()
                    .getWindow();

            stage.setScene(
                    new Scene(root, 690, 420)
            );

            stage.centerOnScreen();

        } catch (IOException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se pudo regresar a la pantalla de pagos."
            );
        }
    }

    /**
     * Muestra una alerta informativa, de advertencia
     * o de error al usuario.
     */
    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}