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

        // Crea el cargador para abrir la vista de socios.
        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "SociosView.fxml"
                        )
                );

        // Cambia la ventana actual por la pantalla de socios.
        cambiarVentana(loader);
    }

    /**
     * Abre la pantalla de gestión de planes.
     *
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void abrirPlanes() throws IOException {

        // Crea el cargador para abrir la vista de planes.
        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "PlanesView.fxml"
                        )
                );

        // Cambia la ventana actual por la pantalla de planes.
        cambiarVentana(loader);
    }

    /**
     * Abre la pantalla de gestión de pagos.
     *
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void abrirPagos() throws IOException {

        // Crea el cargador para abrir la vista de pagos.
        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "PagosView.fxml"
                        )
                );

        // Cambia la ventana actual por la pantalla de pagos.
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

        // Crea el cargador para abrir la vista de control de acceso.
        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "ControlDeAccesoView.fxml"
                        )
                );

        // Carga la estructura visual de la pantalla.
        Parent root =
                loader.load();

        // Obtiene el controlador asociado al archivo FXML.
        ControladorAcceso controlador =
                loader.getController();

        // Crea el servicio encargado de controlar los accesos.
        ControlAcceso controlAcceso =
                new ControlAcceso(
                        DatosAplicacion.getPagoService()
                );

        // Asigna el servicio de control de acceso al controlador.
        controlador.setControlAcceso(
                controlAcceso
        );

        // Obtiene la ventana principal actualmente abierta.
        Stage stage =
                obtenerStage();

        /*
         * Esta pantalla tiene más contenido vertical
         * que las demás (encabezado + información de la
         * membresía + resultado), así que necesita una
         * ventana más alta para que no se vea recortada.
         */
        // Crea una escena con el tamaño adecuado para la pantalla de acceso.
        Scene scene =
                new Scene(
                        root,
                        600,
                        520
                );

        // Establece la nueva escena en la ventana principal.
        stage.setScene(scene);

        // Centra la ventana en la pantalla.
        stage.centerOnScreen();
    }

    /**
     * Abre la pantalla de reportes.
     *
     * @throws IOException si ocurre un error al cargar el FXML
     */
    @FXML
    private void abrirReportes() throws IOException {

        // Crea el cargador para abrir la vista de reportes.
        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "ReportesView.fxml"
                        )
                );

        // Carga la estructura visual de la pantalla de reportes.
        Parent root =
                loader.load();

        // Obtiene la ventana principal actualmente abierta.
        Stage stage =
                obtenerStage();

        /*
         * La pantalla de Reportes tiene más columnas
         * que las demás, así que necesita una ventana
         * más ancha para que no se vea recortada.
         */
        // Crea una escena con el tamaño adecuado para la pantalla de reportes.
        Scene scene =
                new Scene(
                        root,
                        1100,
                        550
                );

        // Establece la escena de reportes en la ventana actual.
        stage.setScene(scene);

        // Centra la ventana en la pantalla.
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

        // Crea el cargador para abrir nuevamente la pantalla de inicio de sesión.
        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "LoginView.fxml"
                        )
                );

        // Cambia la ventana actual por la pantalla de inicio de sesión.
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

        // Carga la estructura visual del archivo FXML indicado.
        Parent root =
                loader.load();

        // Obtiene la ventana principal actualmente abierta.
        Stage stage =
                obtenerStage();

        // Crea una nueva escena con el tamaño estándar de las pantallas.
        Scene scene =
                new Scene(root, 600, 400);

        // Reemplaza la escena actual por la nueva escena.
        stage.setScene(scene);
    }

    /**
     * Obtiene la ventana principal actualmente abierta.
     *
     * @return ventana principal de la aplicación
     */
    private Stage obtenerStage() {

        // Busca entre las ventanas de JavaFX aquella que actualmente está visible.
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