package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.Pantalla;
import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// ============================================================
// MenuPrincipalController.java
// QUÉ HACE:      Menú principal: cada tarjeta abre una pantalla; también cierra la sesión.
// CONECTADO A:   MenuPrincipalView.fxml. Abre Socios, Planes, Pagos, Facturacion, Control de
//                Acceso, Historial, Reportes y Login.
// ============================================================
public class MenuPrincipalController {

    @FXML
    private Parent raiz;

    @FXML
    private Label lblFecha;

    /** Muestra la fecha de hoy en el encabezado, ej. "Lunes 2 de noviembre de 2026". */
    @FXML
    public void initialize() {

        String fecha = LocalDate.now().format(
                DateTimeFormatter.ofPattern(
                        "EEEE d 'de' MMMM 'de' yyyy",
                        Locale.forLanguageTag("es-CR")));

        lblFecha.setText(
                Character.toUpperCase(fecha.charAt(0)) + fecha.substring(1));
    }

    // Abre la pantalla de Socios.
    @FXML
    private void abrirSocios() throws IOException {
        abrir("SociosView.fxml");
    }

    // Abre la pantalla de Planes.
    @FXML
    private void abrirPlanes() throws IOException {
        abrir("PlanesView.fxml");
    }

    // Abre la pantalla de Pagos.
    @FXML
    private void abrirPagos() throws IOException {
        abrir("PagosView.fxml");
    }

    // Abre la pantalla de Facturacion.
    @FXML
    private void abrirFacturacion() throws IOException {
        abrir("FacturacionView.fxml");
    }

    // Abre Control de Acceso y le entrega el servicio de acceso.
    @FXML
    private void abrirAcceso() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "ControlDeAccesoView.fxml"
                        )
                );

        Parent root = loader.load();

        ControladorAcceso controlador =
                loader.getController();

        controlador.setControlAcceso(
                new ControlAcceso(
                        DatosAplicacion.getPagoService()
                )
        );

        Pantalla.cambiar(obtenerStage(), root);
    }

    // Abre el Historial de Accesos.
    @FXML
    private void abrirHistorialAccesos() throws IOException {
        abrir("HistorialAccesosView.fxml");
    }

    // Abre los Reportes.
    @FXML
    private void abrirReportes() throws IOException {
        abrir("ReportesView.fxml");
    }

    // Vuelve al Login.
    @FXML
    private void cerrarSesion() throws IOException {
        abrir("LoginView.fxml");
    }

    /**
     * Carga la vista indicada y la muestra en la ventana actual,
     * que permanece maximizada.
     */
    private void abrir(String vista) throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(vista)
                );

        Parent root = loader.load();

        Pantalla.cambiar(obtenerStage(), root);
    }

    // Obtiene la ventana actual.
    private Stage obtenerStage() {
        return (Stage) raiz.getScene().getWindow();
    }
}
