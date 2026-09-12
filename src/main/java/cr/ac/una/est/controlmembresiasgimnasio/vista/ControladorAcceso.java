package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ControladorAcceso {

    @FXML
    private TextField txtNumeroSocio;

    @FXML
    private Label lblResultado;

    private ControlAcceso controlAcceso;

    /**
     * Inicializa los componentes de la pantalla de acceso.
     * No recibe parámetros.
     * No retorna ningún valor.
     */
    @FXML
    public void initialize() {

        lblResultado.setText(
                "Ingrese el número del socio"
        );
    }

    /**
     * Recibe el servicio que contiene las membresías y pagos
     * registrados en el sistema.
     *
     * @param controlAcceso servicio encargado de verificar el acceso
     */
    public void setControlAcceso(ControlAcceso controlAcceso) {

        this.controlAcceso = controlAcceso;
    }

    /**
     * Verifica si el socio ingresado puede acceder al gimnasio.
     * Obtiene el identificador desde el campo de texto y consulta
     * el servicio ControlAcceso.
     * No retorna ningún valor.
     */
    @FXML
    private void verificarAcceso() {

        String textoNumero = txtNumeroSocio.getText();

        if (textoNumero == null || textoNumero.isBlank()) {

            lblResultado.setText(
                    "Ingrese un número de socio."
            );

            return;
        }

        try {

            int idSocio =
                    Integer.parseInt(textoNumero);

            if (controlAcceso == null) {

                lblResultado.setText(
                        "Los datos del sistema no han sido cargados."
                );

                return;
            }

            boolean accesoPermitido =
                    controlAcceso.verificarAcceso(idSocio);

            if (accesoPermitido) {

                lblResultado.setText(
                        "ACCESO PERMITIDO"
                );

            } else {

                lblResultado.setText(
                        "ACCESO DENEGADO POR MOROSIDAD"
                );
            }

        } catch (NumberFormatException e) {

            lblResultado.setText(
                    "Ingrese un número de socio válido."
            );
        }
    }
}
