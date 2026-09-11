package modelo.socios;

public class Socio extends Persona {

    private int idSocio;
    private String contactoEmergencia;
    private String telefonoEmergencia;
    private String condicionesMedicas;


    public Socio(int idSocio, String nombre, String telefono, String correo,
                 String contactoEmergencia, String telefonoEmergencia,
                 String condicionesMedicas) {

        super(nombre, telefono, correo);

        this.idSocio = idSocio;
        this.contactoEmergencia = contactoEmergencia;
        this.telefonoEmergencia = telefonoEmergencia;
        this.condicionesMedicas = condicionesMedicas;
    }


    public int getIdSocio() {
        return idSocio;
    }


    public void setIdSocio(int idSocio) {
        this.idSocio = idSocio;
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
}