package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.Pantalla;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.accesos.RegistroAcceso;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;
import cr.ac.una.est.controlmembresiasgimnasio.service.FormatoFactura;
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
import java.time.temporal.ChronoUnit;
import java.util.List;

// ============================================================
// ControladorAcceso.java
// QUÉ HACE:      Pantalla de Control de Acceso: el usuario escribe la cédula y el programa dice si
//                puede entrar (verde) o no (rojo).
// CONECTADO A:   ControlDeAccesoView.fxml (diseño), ControlAcceso (decide y guarda) e
//                HistorialAccesoService (últimos intentos). Se abre desde el menú.
// ============================================================
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
    private Label lblDetalle;

    private static final String AYUDA =
            "Escriba la cédula y elija: Consultar membresía (solo ver el estado) "
                    + "o Registrar entrada (marca el ingreso del socio).";

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
    private boolean procesando;

    // Se ejecuta al abrir la pantalla: arma la tabla de últimos intentos.
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
                        ? "#4ADE80" : "#F87171") + ";");
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
        tarea.setOnFailed(e -> tablaRecientes.setPlaceholder(
                new Label("No se pudieron cargar los accesos recientes. Revise MySQL.")));

        Thread hilo = new Thread(tarea, "cargar-accesos-recientes");
        hilo.setDaemon(true);
        hilo.start();
    }

    // El menú le entrega el ControlAcceso que debe usar.
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

    // Llama a ControlAcceso en segundo plano y muestra el resultado.
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

        if (procesando) return;
        if (cedula.length() > 20) {
            lblResultado.setText("La cédula no puede superar los 20 caracteres.");
            return;
        }
        procesando = true;
        txtCedula.setDisable(true);
        lblResultado.setText("Consultando y guardando en MySQL...");
        lblDetalle.setText("");
        pintarResultado(null);
        Task<ResultadoAcceso> tarea = new Task<>() {
            @Override protected ResultadoAcceso call() {
                return esEntrada ? controlAcceso.registrarEntrada(cedula)
                        : controlAcceso.consultarMembresia(cedula);
            }
        };
        tarea.setOnSucceeded(e -> {
            procesando = false;
            txtCedula.setDisable(false);
            mostrarResultado(tarea.getValue(), esEntrada);
        });
        tarea.setOnFailed(e -> {
            procesando = false;
            txtCedula.setDisable(false);
            limpiarInformacion();
            lblResultado.setText("No se pudo verificar el acceso");
            lblDetalle.setText("Revise la conexión a MySQL e intente de nuevo.");
            pintarResultado(false);
        });
        Thread hilo = new Thread(tarea, "verificar-acceso");
        hilo.setDaemon(true);
        hilo.start();
    }

    /** Presenta la verificación desde el hilo de JavaFX.
     * @param resultado verificación @param esEntrada indica intento o consulta */
    private void mostrarResultado(ResultadoAcceso resultado, boolean esEntrada) {
        if (resultado.membresia() == null) {
            limpiarInformacion();
        } else {
            mostrarInformacionMembresia(resultado.membresia());
        }

        LocalDate vence = resultado.membresia() == null
                ? null : resultado.membresia().getFechaVencimiento();
        String plazo = describirPlazo(vence);
        String motivo = resultado.motivo();
        String titulo;
        String detalle;

        if (esEntrada) {

            if (resultado.permitido()) {
                titulo = "ACCESO PERMITIDO";
                detalle = "Entrada registrada. " + detalleMotivo(motivo, plazo);
            } else {
                titulo = "ACCESO DENEGADO";
                detalle = detalleMotivo(motivo, plazo)
                        + ("Membresía vencida".equals(motivo)
                        ? " Renueve el pago para permitir el ingreso." : "");
            }

        } else {
            titulo = resultado.permitido()
                    ? "MEMBRESÍA VIGENTE" : motivo.toUpperCase();
            detalle = "Solo consulta: no se registró la entrada. "
                    + detalleMotivo(motivo, plazo);
        }

        if (!resultado.registrado()) {
            detalle += " (No se pudo guardar en la base de datos.)";
        }

        lblResultado.setText(titulo);
        lblDetalle.setText(detalle);
        pintarResultado(resultado.permitido());

        txtCedula.clear();
        txtCedula.requestFocus();

        if (esEntrada) {
            cargarRecientes();
        }
    }

    /**
     * Colorea el panel de resultado: verde si el acceso está permitido (o la
     * membresía vigente), rojo si no, y neutro cuando aún no hay resultado.
     *
     * @param permitido true = verde, false = rojo, null = neutro
     */
    private void pintarResultado(Boolean permitido) {

        String fondo = "#161B22";
        String borde = "#2A313C";
        String texto = "#E5E7EB";

        if (permitido != null && permitido) {
            fondo = "rgba(74,222,128,0.12)";
            borde = "#4ADE80";
            texto = "#4ADE80";
        } else if (permitido != null) {
            fondo = "rgba(248,113,113,0.12)";
            borde = "#F87171";
            texto = "#F87171";
        }

        lblResultado.setStyle("-fx-background-color: " + fondo + ";"
                + " -fx-background-radius: 12;"
                + " -fx-border-color: " + borde + ";"
                + " -fx-border-radius: 12; -fx-border-width: 1.5;"
                + " -fx-text-fill: " + texto + ";"
                + " -fx-font-size: 28px; -fx-font-weight: bold;"
                + " -fx-padding: 16;");
    }

    /** Explica el motivo del resultado en una frase concreta. */
    private static String detalleMotivo(String motivo, String plazo) {
        return switch (motivo) {
            case "Membresía vigente", "Membresía vencida" -> "La membresía " + plazo + ".";
            case "Membresía inactiva" -> "La membresía del socio está inactiva.";
            case "Sin membresía" -> "El socio está registrado, pero no tiene membresía.";
            case "Cédula no registrada" -> "No existe ningún socio con esa cédula.";
            default -> motivo + ".";
        };
    }

    /** Describe el vencimiento respecto a hoy, ej. "vence el 30/11/2026 (quedan 12 días)". */
    private static String describirPlazo(LocalDate vence) {
        if (vence == null) {
            return "no tiene fecha de vencimiento";
        }

        String fecha = FormatoFactura.fecha(vence);
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), vence);

        if (dias > 1) return "vence el " + fecha + " (quedan " + dias + " días)";
        if (dias == 1) return "vence mañana (" + fecha + ")";
        if (dias == 0) return "vence hoy (" + fecha + ")";
        if (dias == -1) return "venció ayer (" + fecha + ")";
        return "venció el " + fecha + " (hace " + (-dias) + " días)";
    }

    // Muestra socio, plan y vencimiento.
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
                    FormatoFactura.fecha(membresia.getFechaVencimiento())
            );

        } else {

            lblVencimiento.setText(
                    "Sin pago registrado"
            );
        }
    }

    // Deja los datos del socio en blanco.
    private void limpiarInformacion() {

        lblNombre.setText("-");
        lblPlan.setText("-");
        lblVencimiento.setText("-");

        lblResultado.setText(
                "Ingrese el número de cédula del socio"
        );
        lblDetalle.setText(AYUDA);
        pintarResultado(null);
    }

    // Regresa al menú principal.
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