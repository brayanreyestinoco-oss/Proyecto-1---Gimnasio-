package cr.ac.una.est.controlmembresiasgimnasio.modelo.socios;
import java.time.LocalDate;

// Clase que representa a un socio del gimnasio y hereda los datos básicos de Persona.
public class Socio extends Persona {

    // Identificador único del socio.
    private int idSocio;

    // Número de cédula del socio.
    private String cedula;

    // Nombre de la persona que debe ser contactada en caso de emergencia.
    private String contactoEmergencia;

    // Número de teléfono del contacto de emergencia.
    private String telefonoEmergencia;

    // Información relacionada con las condiciones médicas del socio.
    private String condicionesMedicas;

    // Fecha en la que expira la membresía del socio.
    private LocalDate expiraMembrecia;


    // Constructor que inicializa la información del socio.
    public Socio(String cedula, String nombre, String telefono, String correo,
                 String contactoEmergencia, String telefonoEmergencia,
                 String condicionesMedicas) {

        // Llama al constructor de Persona para inicializar los datos heredados.
        super(nombre, telefono, correo);

        // Inicializa el identificador del socio en cero.
        this.idSocio = 0;

        // Guarda la cédula del socio.
        this.cedula = cedula;

        // Guarda el contacto de emergencia.
        this.contactoEmergencia = contactoEmergencia;

        // Guarda el teléfono del contacto de emergencia.
        this.telefonoEmergencia = telefonoEmergencia;

        // Guarda las condiciones médicas del socio.
        this.condicionesMedicas = condicionesMedicas;

        // Inicialmente el socio no tiene una fecha de expiración de membresía.
        this.expiraMembrecia = null;
    }


    // Obtiene el identificador del socio.
    public int getIdSocio() {

        return idSocio;
    }


    // Modifica el identificador del socio.
    public void setIdSocio(int idSocio) {

        this.idSocio = idSocio;
    }

    // Obtiene la cédula del socio.
    public String getCedula() {

        return cedula;
    }

    // Modifica la cédula del socio.
    public void setCedula(String cedula) {

        this.cedula = cedula;
    }

    // Obtiene el nombre del contacto de emergencia.
    public String getContactoEmergencia() {

        return contactoEmergencia;
    }


    // Modifica el contacto de emergencia.
    public void setContactoEmergencia(String contactoEmergencia) {

        this.contactoEmergencia = contactoEmergencia;
    }


    // Obtiene el teléfono del contacto de emergencia.
    public String getTelefonoEmergencia() {

        return telefonoEmergencia;
    }

    // Modifica el teléfono del contacto de emergencia.
    public void setTelefonoEmergencia(String telefonoEmergencia) {

        this.telefonoEmergencia = telefonoEmergencia;
    }


    // Obtiene la información sobre las condiciones médicas.
    public String getCondicionesMedicas() {

        return condicionesMedicas;
    }


    // Modifica la información sobre las condiciones médicas.
    public void setCondicionesMedicas(String condicionesMedicas) {

        this.condicionesMedicas = condicionesMedicas;
    }


    // Obtiene la fecha de expiración de la membresía.
    public LocalDate getExpiraMembrecia() {

        return expiraMembrecia;
    }


    // Modifica la fecha de expiración de la membresía.
    public void setExpiraMembrecia(LocalDate expiraMembrecia) {

        this.expiraMembrecia = expiraMembrecia;
    }

    // Obtiene el nombre del socio utilizando el método heredado de Persona.
    public String getNombreSocio() {

        return getNombre();
    }
}