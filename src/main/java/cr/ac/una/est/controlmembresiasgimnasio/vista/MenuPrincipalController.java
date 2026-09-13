package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador encargado de administrar la navegación
 * desde el menú principal de la aplicación.
 */
public class MenuPrincipalController {

    /**
     * Abre la pantalla de gestión de socios.
     *
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void abrirSocios() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "SociosView.fxml"
                        )
                );

        cambiarVentana(loader);
    }

    /**
     * Abre la pantalla de gestión de planes.
     *
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void abrirPlanes() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "PlanesView.fxml"
                        )
                );

        cambiarVentana(loader);
    }

    /**
     * Abre la pantalla de gestión de pagos.
     *
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void abrirPagos() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "PagosView.fxml"
                        )
                );

        cambiarVentana(loader);
    }

    /**
     * Abre la pantalla utilizada para simular
     * el acceso de un socio al gimnasio.
     *
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void abrirAcceso() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "ControlDeAccesoView.fxml"
                        )
                );

        Parent root =
                loader.load();

        ControladorAcceso controlador =
                loader.getController();

        ControlAcceso controlAcceso =
                new ControlAcceso(
                        DatosAplicacion.getPagoService()
                );

        controlador.setControlAcceso(
                controlAcceso
        );

        Stage stage =
                obtenerStage();

        Scene scene =
                new Scene(
                        root,
                        600,
                        400
                );

        stage.setScene(scene);
    }

    /**
     * Muestra un aviso de que la sección de reportes
     * todavía no ha sido implementada.
     */
    @FXML
    private void abrirReportes() {

        Alert alerta =
                new Alert(
                        Alert.AlertType.INFORMATION,
                        "La sección de Reportes aún no está disponible."
                );

        alerta.setHeaderText(null);

        alerta.showAndWait();
    }

    /**
     * Cierra la aplicación.
     */
    @FXML
    private void salir() {

        Platform.exit();
    }

    /**
     * Carga una nueva pantalla en la ventana actual.
     *
     * @param loader cargador del archivo FXML
     * @throws IOException si ocurre un error al cargar la pantalla
     */
    private void cambiarVentana(
            FXMLLoader loader) throws IOException {

        Parent root =
                loader.load();

        Stage stage =
                obtenerStage();

        Scene scene =
                new Scene(root, 600, 400);

        stage.setScene(scene);
    }

    /**
     * Obtiene la ventana principal actualmente abierta.
     *
     * @return ventana principal de la aplicación
     */
    private Stage obtenerStage() {

        return (Stage)
                javafx.stage.Window
                        .getWindows()
                        .stream()
                        .filter(
                                javafx.stage.Window::isShowing
                        )
                        .findFirst()
                        .orElse(null);
    }
}