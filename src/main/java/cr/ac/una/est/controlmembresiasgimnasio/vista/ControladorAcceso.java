package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ControladorAcceso {

    @FXML
    private TextField txtIdsocio;

    @FXML
    private Button btnVerificar;

    @FXML
    private Label lblResultado;

    private ControlAcceso controlAcceso;

    /*
     * Inicializa los componentes necesarios para el controlador.
     * No recibe parámetros.
     * No retorna ningún valor.
     */
    @FXML
    public void initialize() {
        lblResultado.setText("");
    }

    /*
     * Recibe el objeto ControlAcceso que contiene la lógica
     * y la información necesaria para verificar el acceso.
     *
     * @param controlAcceso objeto encargado de verificar el acceso.
     * No retorna ningún valor.
     */
    public void setControlAcceso(ControlAcceso controlAcceso) {
        this.controlAcceso = controlAcceso;
    }

    /*
     * Verifica si la suscripción del socio está activa por medio del ID.
     * No recibe parámetros directamente.
     * No retorna ningún valor porque el resultado se muestra en la interfaz.
     */
    @FXML
    private void verificarAcceso() {

        String textoId = txtIdsocio.getText();

        // Verifica que el usuario haya escrito un número de socio
        if (textoId == null || textoId.isBlank()) {

            lblResultado.setText(
                    "Ingrese la identificación del socio."
            );

            return;
        }

        // Verifica que ControlAcceso haya sido cargado desde el sistema principal
        if (controlAcceso == null) {

            lblResultado.setText(
                    "No se ha cargado la información de los socios."
            );

            return;
        }

        try {

            // Convierte el texto ingresado a un número entero
            int idSocio = Integer.parseInt(textoId);

            // Envía el ID a la clase ControlAcceso
            boolean accesoPermitido =
                    controlAcceso.verificarAcceso(idSocio);

            // Muestra el resultado en la interfaz
            if (accesoPermitido) {

                lblResultado.setText(
                        "Acceso permitido"
                );

            } else {

                lblResultado.setText(
                        "Acceso denegado por morosidad"
                );
            }

        } catch (NumberFormatException e) {

            lblResultado.setText(
                    "Ingrese un número de socio válido."
            );
        }
    }
}
