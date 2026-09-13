package cr.ac.una.est.controlmembresiasgimnasio.modelo.socios;
import java.time.LocalDate;

public class Socio extends Persona {

    private int idSocio;
    private String cedula;
    private String contactoEmergencia;
    private String telefonoEmergencia;
    private String condicionesMedicas;
    private LocalDate expiraMembrecia;


    public Socio(String cedula, String nombre, String telefono, String correo,
                 String contactoEmergencia, String telefonoEmergencia,
                 String condicionesMedicas) {

        super(nombre, telefono, correo);

        this.idSocio = 0;
        this.cedula = cedula;
        this.contactoEmergencia = contactoEmergencia;
        this.telefonoEmergencia = telefonoEmergencia;
        this.condicionesMedicas = condicionesMedicas;
        this.expiraMembrecia = null;
    }


    public int getIdSocio() {
        return idSocio;
    }


    public void setIdSocio(int idSocio) {
        this.idSocio = idSocio;
    }
    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getContactoEmergencia() {
        return contactoEmergencia;
    }


    public void setContactoEmergencia(String contactoEmergencia) {
        this.contactoEmergencia = contactoEmergencia;
    }


    public String getTelefonoEmergencia() {
        return telefonoEmergencia;
    }

    public void setTelefonoEmergencia(String telefonoEmergencia) {
        this.telefonoEmergencia = telefonoEmergencia;
    }


    public String getCondicionesMedicas() {
        return condicionesMedicas;
    }


    public void setCondicionesMedicas(String condicionesMedicas) {
        this.condicionesMedicas = condicionesMedicas;
    }


    public LocalDate getExpiraMembrecia() {
        return expiraMembrecia;
    }


    public void setExpiraMembrecia(LocalDate expiraMembrecia) {
        this.expiraMembrecia = expiraMembrecia;
    }

    public String getNombreSocio() {
        return getNombre();
    }
}