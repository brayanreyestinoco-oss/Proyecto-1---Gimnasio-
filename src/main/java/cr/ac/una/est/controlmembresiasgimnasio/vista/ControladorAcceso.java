package cr.ac.una.est.controlmembresiasgimnasio.vista;

import cr.ac.una.est.controlmembresiasgimnasio.HelloApplication;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.service.ControlAcceso;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador encargado de manejar
 * la simulación de acceso al gimnasio.
 */
public class ControladorAcceso {

    // Campo donde se ingresa el número de cédula del socio.
    @FXML
    private TextField txtCedula;

    // Etiqueta donde se muestra el nombre del socio.
    @FXML
    private Label lblNombre;

    // Etiqueta donde se muestra el nombre del plan.
    @FXML
    private Label lblPlan;

    // Etiqueta donde se muestra la fecha de vencimiento de la membresía.
    @FXML
    private Label lblVencimiento;

    // Etiqueta donde se muestra el resultado de la verificación.
    @FXML
    private Label lblResultado;

    // Servicio encargado de controlar y verificar los accesos.
    private ControlAcceso controlAcceso;

    /**
     * Inicializa la pantalla de acceso.
     */
    @FXML
    public void initialize() {

        // Limpia los datos mostrados inicialmente en la pantalla.
        limpiarInformacion();
    }

    /**
     * Asigna el servicio utilizado
     * para verificar el acceso.
     *
     * @param controlAcceso servicio de control de acceso
     */
    public void setControlAcceso(
            ControlAcceso controlAcceso) {

        // Guarda el servicio recibido para utilizarlo posteriormente.
        this.controlAcceso =
                controlAcceso;
    }

    /**
     * Verifica si el socio ingresado
     * posee una membresía vigente.
     */
    @FXML
    private void verificarAcceso() {

        // Obtiene el texto ingresado y elimina espacios al inicio y al final.
        String textoNumero =
                txtCedula
                        .getText()
                        .trim();

        // Comprueba si el campo de cédula está vacío.
        if (textoNumero.isBlank()) {

            // Limpia la información anterior mostrada en pantalla.
            limpiarInformacion();

            // Indica al usuario que debe ingresar el número de cédula.
            lblResultado.setText(
                    "Ingrese el numero de cedula del socio."
            );

            return;
        }

        // Variable que almacenará el identificador numérico del socio.
        int idSocio;

        try {

            // Convierte el texto ingresado a un número entero.
            idSocio =
                    Integer.parseInt(
                            textoNumero
                    );

        } catch (NumberFormatException e) {

            // Limpia la información si el dato ingresado no es válido.
            limpiarInformacion();

            // Muestra un mensaje indicando que debe ingresar un número válido.
            lblResultado.setText(
                    "Ingrese un número cedula de socio válido."
            );

            return;
        }

        // Verifica que el servicio de control de acceso haya sido asignado.
        if (controlAcceso == null) {

            // Limpia la información mostrada.
            limpiarInformacion();

            // Informa que los datos necesarios todavía no están disponibles.
            lblResultado.setText(
                    "Los datos del sistema no han sido cargados."
            );

            return;
        }

        /*
         * Buscamos la membresía asociada
         * al número de socio.
         */
        Membresia membresia =
                controlAcceso.buscarMembresia(
                        idSocio
                );

        /*
         * Si no existe membresía,
         * el socio no puede ingresar.
         */
        if (membresia == null) {

            // Limpia los datos de la membresía anterior.
            limpiarInformacion();

            // Indica que el acceso fue rechazado porque no existe una membresía.
            lblResultado.setText(
                    "ACCESO DENEGADO - SIN MEMBRESÍA"
            );

            return;
        }

        // Muestra en pantalla la información de la membresía encontrada.
        mostrarInformacionMembresia(
                membresia
        );

        // Solicita al servicio que determine si el socio puede ingresar.
        boolean accesoPermitido =
                controlAcceso.verificarAcceso(
                        idSocio
                );

        // Comprueba si el acceso fue autorizado.
        if (accesoPermitido) {

            // Muestra el mensaje de acceso permitido.
            lblResultado.setText(
                    "ACCESO PERMITIDO"
            );

        } else {

            // Muestra el mensaje cuando el acceso es rechazado por morosidad.
            lblResultado.setText(
                    "ACCESO DENEGADO POR MOROSIDAD"
            );
        }
    }

    /**
     * Muestra los datos de la membresía
     * encontrada.
     *
     * @param membresia membresía del socio
     */
    private void mostrarInformacionMembresia(
            Membresia membresia) {

        // Obtiene y muestra el nombre del socio asociado a la membresía.
        lblNombre.setText(
                membresia
                        .getSocio()
                        .getNombre()
        );

        // Obtiene y muestra el nombre del plan contratado.
        lblPlan.setText(
                membresia
                        .getPlan()
                        .getNombre()
        );

        // Comprueba si la membresía todavía no tiene una fecha de vencimiento.
        if (membresia.getFechaVencimiento()
                == null) {

            // Indica que todavía no existe un pago registrado.
            lblVencimiento.setText(
                    "Sin pago registrado"
            );

        } else {

            // Muestra la fecha de vencimiento de la membresía.
            lblVencimiento.setText(
                    membresia
                            .getFechaVencimiento()
                            .toString()
            );
        }
    }

    /**
     * Limpia la información mostrada
     * en pantalla.
     */
    private void limpiarInformacion() {

        // Coloca un guion en el campo del nombre.
        lblNombre.setText(
                "-"
        );

        // Coloca un guion en el campo del plan.
        lblPlan.setText(
                "-"
        );

        // Coloca un guion en el campo de vencimiento.
        lblVencimiento.setText(
                "-"
        );

        // Restablece el mensaje inicial de la pantalla.
        lblResultado.setText(
                "Ingrese el número de cedula del socio"
        );
    }

    /**
     * Regresa al menú principal.
     */
    @FXML
    private void volverMenu() {

        try {

            // Carga el archivo FXML correspondiente al menú principal.
            FXMLLoader loader =
                    new FXMLLoader(
                            HelloApplication.class
                                    .getResource(
                                            "MenuPrincipalView.fxml"
                                    )
                    );

            // Carga la estructura visual del menú principal.
            Parent root =
                    loader.load();

            // Obtiene la ventana actual utilizando el campo de cédula.
            Stage stage =
                    (Stage)
                            txtCedula
                                    .getScene()
                                    .getWindow();

            // Cambia la escena actual por la del menú principal.
            stage.setScene(
                    new Scene(
                            root,
                            600,
                            400
                    )
            );

            // Centra nuevamente la ventana en la pantalla.
            stage.centerOnScreen();

        } catch (IOException e) {

            // Muestra un mensaje si ocurre un error al cargar el menú.
            lblResultado.setText(
                    "No se pudo regresar al menú principal."
            );
        }
    }
}