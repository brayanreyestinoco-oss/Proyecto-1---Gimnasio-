package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.Pantalla;
import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuPrincipalController {

    @FXML
    private Parent raiz;

    @FXML
    private void abrirSocios() throws IOException {
        abrir("SociosView.fxml");
    }

    @FXML
    private void abrirPlanes() throws IOException {
        abrir("PlanesView.fxml");
    }

    @FXML
    private void abrirPagos() throws IOException {
        abrir("PagosView.fxml");
    }

    @FXML
    private void abrirFacturacion() throws IOException {
        abrir("FacturacionView.fxml");
    }

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

    @FXML
    private void abrirReportes() throws IOException {
        abrir("ReportesView.fxml");
    }

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

    private Stage obtenerStage() {
        return (Stage) raiz.getScene().getWindow();
    }
}
