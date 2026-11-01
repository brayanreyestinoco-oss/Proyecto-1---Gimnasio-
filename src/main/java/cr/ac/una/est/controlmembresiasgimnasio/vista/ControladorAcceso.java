package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.Pantalla;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.accesos.RegistroAcceso;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;
import cr.ac.una.est.controlmembresiasgimnasio.service.HistorialAccesoService;
import cr.ac.una.est.controlmembresiasgimnasio.service.ResultadoAcceso;

import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ControladorAcceso {

    @FXML
    private TextField txtCedula;

    @FXML
    private Label lblNombre;

    @FXML
    private Label lblPlan;

    @FXML
    private Label lblVencimiento;

    @FXML
    private Label lblResultado;

    @FXML
    private TableView<RegistroAcceso> tablaRecientes;

    @FXML
    private TableColumn<RegistroAcceso, String> colHora;

    @FXML
    private TableColumn<RegistroAcceso, String> colSocioReciente;

    @FXML
    private TableColumn<RegistroAcceso, String> colCedulaReciente;

    @FXML
    private TableColumn<RegistroAcceso, String> colResultadoReciente;

    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    private final HistorialAccesoService historialService =
            new HistorialAccesoService();

    private ControlAcceso controlAcceso;

    @FXML
    public void initialize() {
        limpiarInformacion();

        colHora.setCellValueFactory(c -> new SimpleStringProperty(
                FORMATO_HORA.format(c.getValue().fechaHora())));
        colSocioReciente.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().nombre() == null ? "-" : c.getValue().nombre()));
        colCedulaReciente.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().cedula()));
        colResultadoReciente.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().permitido() ? "PERMITIDO" : "DENEGADO"));

        colResultadoReciente.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String texto, boolean vacio) {
                super.updateItem(texto, vacio);

                if (vacio || texto == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(texto);
                setStyle("-fx-font-weight: bold; -fx-text-fill: "
                        + ("PERMITIDO".equals(texto)
                        ? "#15803D" : "#DC2626") + ";");
            }
        });

        cargarRecientes();
    }

    /** Muestra las últimas entradas de hoy, leídas de acceso_historial. */
    private void cargarRecientes() {

        Task<List<RegistroAcceso>> tarea = new Task<>() {
            @Override
            protected List<RegistroAcceso> call() {
                return historialService.listar(
                        LocalDate.now().atStartOfDay(), 8);
            }
        };

        tarea.setOnSucceeded(e ->
                tablaRecientes.getItems().setAll(tarea.getValue()));

        Thread hilo = new Thread(tarea, "cargar-accesos-recientes");
        hilo.setDaemon(true);
        hilo.start();
    }

    public void setControlAcceso(ControlAcceso controlAcceso) {
        this.controlAcceso = controlAcceso;
    }

    /** Registra una ENTRADA (se guarda en acceso_historial). */
    @FXML
    private void verificarAcceso() {
        procesar(true);
    }

    /** Consulta la membresía sin registrar entrada (se guarda en consulta_membresia). */
    @FXML
    private void consultarMembresia() {
        procesar(false);
    }

    private void procesar(boolean esEntrada) {

        String cedula = txtCedula.getText().trim();

        if (cedula.isEmpty()) {
            limpiarInformacion();

            lblResultado.setText(
                    "Ingrese el número de cédula del socio."
            );

            return;
        }

        if (controlAcceso == null) {
            limpiarInformacion();

            lblResultado.setText(
                    "Error: el servicio de acceso no está inicializado."
            );

            return;
        }

        ResultadoAcceso resultado;

        try {
            resultado = esEntrada
                    ? controlAcceso.registrarEntrada(cedula)
                    : controlAcceso.consultarMembresia(cedula);

        } catch (IllegalStateException e) {
            limpiarInformacion();

            lblResultado.setText(
                    "Error al consultar la base de datos."
            );

            return;
        }

        if (resultado.membresia() == null) {
            limpiarInformacion();
        } else {
            mostrarInformacionMembresia(resultado.membresia());
        }

        String texto;

        if (esEntrada) {

            if (resultado.permitido()) {
                texto = "ACCESO PERMITIDO";
            } else {
                texto = "ACCESO DENEGADO - "
                        + resultado.motivo().toUpperCase();
            }

        } else {
            texto = "CONSULTA: " + resultado.motivo().toUpperCase();
        }

        if (!resultado.registrado()) {
            texto += "\n(No se pudo guardar en la base de datos)";
        }

        lblResultado.setText(texto);

        txtCedula.clear();
        txtCedula.requestFocus();

        if (esEntrada) {
            cargarRecientes();
        }
    }

    private void mostrarInformacionMembresia(Membresia membresia) {

        if (membresia.getSocio() != null) {

            lblNombre.setText(
                    membresia.getSocio().getNombre()
            );

        } else {

            lblNombre.setText("-");
        }

        if (membresia.getPlan() != null) {

            lblPlan.setText(
                    membresia.getPlan().getNombre()
            );

        } else {

            lblPlan.setText("-");
        }

        if (membresia.getFechaVencimiento() != null) {

            lblVencimiento.setText(
                    membresia.getFechaVencimiento().toString()
            );

        } else {

            lblVencimiento.setText(
                    "Sin pago registrado"
            );
        }
    }

    private void limpiarInformacion() {

        lblNombre.setText("-");
        lblPlan.setText("-");
        lblVencimiento.setText("-");

        lblResultado.setText(
                "Ingrese el número de cédula del socio"
        );
    }

    @FXML
    private void volverMenu() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class.getResource(
                                    "MenuPrincipalView.fxml"
                            )
                    );

            Parent root = loader.load();

            Stage stage =
                    (Stage) txtCedula.getScene().getWindow();

            Pantalla.cambiar(stage, root);
        } catch (IOException e) {

            lblResultado.setText(
                    "Error al regresar al menú principal."
            );
        }
    }
}