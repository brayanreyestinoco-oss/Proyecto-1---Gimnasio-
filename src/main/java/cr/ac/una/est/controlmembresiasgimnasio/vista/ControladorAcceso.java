package cr.ac.una.est.controlmembresiasgimnasio.vista;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;

public class ControladorAcceso {

    @FXML
    private TextFIeld txtIdsocio;

    @FXML
    private Button btnVerificar;

    @FXML
    private label lblResultado;

    private ControladorAcceso controladorAcceso;
    /*Inicialisa los componentes necesarios para el controlador sin recibir nada ni retornar nada
}
     */

    /*Verifica si la suscripcion del socio esta activa por medio del id no retorna nada este
    resultado va directo a la interfas
     */
    @FXML
    private void verificarAcceso(){

        String textoId = txtIdsocio.getText();

        if (textoId == null || textoId.isBlank()){
            lblResultado.setText("Ingrese la identificacion del socio.");
            return;
        }
        try{
            int idSocio = Integer.parseInt(textoId);
            boolean accesoPermitido =
                    controladorAcceso.verificarAcceso(idSocio);
            if (accesoPermitido){
                lblResultado.setText("Acceso perimitido");
            }
            else {
                lblResultado.setText("Acceso denegado por morosidad");
            }
        }
        catch(NumberFormatException e){
            lblResultado.setText("Ingrese un numero de socio valido ");
        }





    }
}
