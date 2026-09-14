package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        /*
         * Esta pantalla tiene más contenido vertical
         * que las demás (encabezado + información de la
         * membresía + resultado), así que necesita una
         * ventana más alta para que no se vea recortada.
         */
        Scene scene =
                new Scene(
                        root,
                        600,
                        520
                );

        stage.setScene(scene);

        stage.centerOnScreen();
    }

    /**
     * Abre la pantalla de reportes.
     *
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void abrirReportes() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "ReportesView.fxml"
                        )
                );

        Parent root =
                loader.load();

        Stage stage =
                obtenerStage();

        /*
         * La pantalla de Reportes tiene más columnas
         * que las demás, así que necesita una ventana
         * más ancha para que no se vea recortada.
         */
        Scene scene =
                new Scene(
                        root,
                        850,
                        460
                );

        stage.setScene(scene);

        stage.centerOnScreen();
    }

    /**
     * Cierra la sesión actual y regresa
     * a la pantalla de inicio de sesión.
     *
     * @throws IOException si ocurre un error al cargar el login
     */
    @FXML
    private void cerrarSesion() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "LoginView.fxml"
                        )
                );

        cambiarVentana(loader);
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