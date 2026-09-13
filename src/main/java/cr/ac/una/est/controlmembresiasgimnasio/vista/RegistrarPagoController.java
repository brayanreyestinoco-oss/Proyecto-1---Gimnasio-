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

    @FXML
    private TextField txtCedula;

    @FXML
    private ComboBox<String> cmbPlan;

    @FXML
    private TextField txtMonto;

    @FXML
    private DatePicker dpFechaInicio;

    @FXML
    private ComboBox<String> cmbMetodoPago;

    @FXML
    private Label lblVencimiento;

    private SocioService socioService;

    private PlanService planService;

    private PagoService pagoService;

    /**
     * Inicializa el controlador y carga
     * los planes y métodos de pago.
     */
    @FXML
    public void initialize() {

        socioService =
                DatosAplicacion.getSocioService();

        planService =
                DatosAplicacion.getPlanService();

        pagoService =
                DatosAplicacion.getPagoService();

        cargarPlanes();

        cargarMetodosPago();

        dpFechaInicio.setValue(
                LocalDate.now()
        );

        /*
         * Cuando el usuario selecciona un plan,
         * mostramos automáticamente su precio.
         */
        cmbPlan.setOnAction(
                event -> actualizarDatosPlan()
        );

        /*
         * Si cambia la fecha también actualizamos
         * la fecha estimada de vencimiento.
         */
        dpFechaInicio.setOnAction(
                event -> actualizarDatosPlan()
        );
    }

    /**
     * Carga los planes disponibles
     * dentro del ComboBox.
     */
    private void cargarPlanes() {

        cmbPlan.getItems().clear();

        for (Plan plan :
                planService.getPlanesDisponibles()) {

            cmbPlan.getItems().add(
                    plan.getNombre()
            );
        }
    }

    /**
     * Carga los métodos de pago disponibles.
     */
    private void cargarMetodosPago() {

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

        String nombrePlan =
                cmbPlan.getValue();

        LocalDate fecha =
                dpFechaInicio.getValue();

        if (nombrePlan == null) {

            return;
        }

        Plan plan =
                planService.buscarPorNombre(
                        nombrePlan
                );

        if (plan == null) {

            return;
        }

        txtMonto.setText(
                String.valueOf(
                        plan.calcularPrecioFinal()
                )
        );

        if (fecha != null) {

            LocalDate vencimiento =
                    fecha.plusMonths(
                            plan.getDuracionEnMeses()
                    );

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

        if (!camposValidos()) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Datos incompletos",
                    "Debe completar todos los datos del pago."
            );

            return;
        }

        /*
         * Primero buscamos al socio utilizando
         * la cédula escrita en el formulario.
         */
        Socio socio =
                socioService.buscarPorCedula(
                        txtCedula
                                .getText()
                                .trim()
                );

        if (socio == null) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Socio no encontrado",
                    "No existe un socio registrado con esa cédula."
            );

            return;
        }

        /*
         * Obtenemos el plan seleccionado.
         */
        Plan plan =
                planService.buscarPorNombre(
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

        double monto;

        try {

            monto =
                    Double.parseDouble(
                            txtMonto
                                    .getText()
                                    .trim()
                    );

        } catch (NumberFormatException e) {

            mostrarMensaje(
                    Alert.AlertType.WARNING,
                    "Monto inválido",
                    "El monto debe ser un número válido."
            );

            return;
        }

        LocalDate fechaPago =
                dpFechaInicio.getValue();

        String metodoPago =
                cmbMetodoPago.getValue();

        try {

            /*
             * Buscamos si el socio ya posee
             * una membresía.
             */
            Membresia membresia =
                    pagoService
                            .buscarMembresiaPorSocio(
                                    socio.getIdSocio()
                            );

            /*
             * Si todavía no tiene membresía,
             * se crea una nueva.
             */
            if (membresia == null) {

                int idMembresia =
                        obtenerSiguienteIdMembresia();

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
                membresia.setPlan(
                        plan
                );
            }

            int idPago =
                    obtenerSiguienteIdPago();

            Pago pago =
                    pagoService.registrarPago(
                            idPago,
                            membresia,
                            monto,
                            fechaPago,
                            metodoPago
                    );

            lblVencimiento.setText(
                    membresia
                            .getFechaVencimiento()
                            .toString()
            );

            mostrarMensaje(
                    Alert.AlertType.INFORMATION,
                    "Pago registrado",
                    "El pago se registró correctamente.\n"
                            + "Número de pago: "
                            + pago.getIdPago()
                            + "\nVencimiento: "
                            + membresia.getFechaVencimiento()
            );

            limpiarFormulario();

        } catch (IllegalArgumentException e) {

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

        int mayorId = 0;

        for (Membresia membresia :
                pagoService.getMembresias()) {

            if (membresia.getIdMembresia()
                    > mayorId) {

                mayorId =
                        membresia.getIdMembresia();
            }
        }

        return mayorId + 1;
    }

    /**
     * Obtiene el siguiente identificador
     * disponible para un pago.
     *
     * @return siguiente identificador
     */
    private int obtenerSiguienteIdPago() {

        int mayorId = 0;

        for (Pago pago :
                pagoService.getPagos()) {

            if (pago.getIdPago()
                    > mayorId) {

                mayorId =
                        pago.getIdPago();
            }
        }

        return mayorId + 1;
    }

    /**
     * Verifica que los campos necesarios
     * estén completos.
     *
     * @return true si los datos son válidos
     */
    private boolean camposValidos() {

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

        limpiarFormulario();
    }

    /**
     * Limpia los campos del formulario.
     */
    private void limpiarFormulario() {

        txtCedula.clear();

        cmbPlan.getSelectionModel()
                .clearSelection();

        txtMonto.clear();

        dpFechaInicio.setValue(
                LocalDate.now()
        );

        cmbMetodoPago
                .getSelectionModel()
                .clearSelection();

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

            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "PagosView.fxml"
                                    )
                    );

            Parent root =
                    loader.load();

            Stage stage =
                    (Stage)
                            txtCedula
                                    .getScene()
                                    .getWindow();

            stage.setScene(
                    new Scene(
                            root,
                            600,
                            400
                    )
            );

        } catch (IOException e) {

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

        Alert alerta =
                new Alert(tipo);

        alerta.setTitle(
                titulo
        );

        alerta.setHeaderText(
                null
        );

        alerta.setContentText(
                mensaje
        );

        alerta.showAndWait();
    }
}