package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.DatosAplicacion;
import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuPrincipalController {

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

    @FXML
    private void abrirPlanes() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("PlanesView.fxml")
        );

        Parent root = loader.load();

        Stage stage = obtenerStage();

        Scene escenaPlanes = new Scene(root, 1100, 650);

        // Al salir de Planes y cambiar a otra pantalla, restaura
        // el tamaño original del menú principal.
        stage.sceneProperty().addListener((observable, escenaAnterior, escenaNueva) -> {
            if (escenaNueva != escenaPlanes) {
                stage.setWidth(600);
                stage.setHeight(400);
                stage.centerOnScreen();
            }
        });

        stage.setMaximized(false);
        stage.setScene(escenaPlanes);
        stage.setWidth(1100);
        stage.setHeight(650);
        stage.centerOnScreen();
    }


    @FXML
    private void abrirPagos() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "PagosView.fxml"
                        )
                );

        Parent root = loader.load();

        Stage stage = obtenerStage();

        Scene scene = new Scene(
                root,
                690,
                420
        );

        stage.setScene(scene);

        stage.centerOnScreen();
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

        ControlAcceso controlAcceso =
                new ControlAcceso(
                        DatosAplicacion.getPagoService()
                );

        controlador.setControlAcceso(
                controlAcceso
        );

        Stage stage = obtenerStage();

        Scene scene = new Scene(
                root,
                600,
                520
        );

        stage.setScene(scene);

        stage.centerOnScreen();
    }

    @FXML
    private void abrirReportes() throws IOException {

        FXMLLoader loader =
                new FXMLLoader(
                        HelloApplication.class.getResource(
                                "ReportesView.fxml"
                        )
                );

        Parent root = loader.load();

        Stage stage = obtenerStage();

        Scene scene = new Scene(
                root,
                1100,
                550
        );

        stage.setScene(scene);

        stage.centerOnScreen();
    }

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

    private void cambiarVentana(
            FXMLLoader loader) throws IOException {

        Parent root = loader.load();

        Stage stage = obtenerStage();

        Scene scene = new Scene(
                root,
                600,
                400
        );

        stage.setScene(scene);
    }

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