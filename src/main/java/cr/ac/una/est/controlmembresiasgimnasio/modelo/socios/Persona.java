package cr.ac.una.est.controlmembresiasgimnasio.modelo.socios;

// Clase base que representa los datos generales de una persona.
public class Persona {

    // Nombre de la persona.
    private String nombre;

    // Número de teléfono de la persona.
    private String telefono;

    // Correo electrónico de la persona.
    private String correo;

    // Constructor que inicializa los datos de la persona.
    public Persona(String nombre, String telefono, String correo) {

        // Guarda el nombre recibido.
        this.nombre = nombre;

        // Guarda el número de teléfono recibido.
        this.telefono = telefono;

        // Guarda el correo electrónico recibido.
        this.correo = correo;
    }

    // Obtiene el nombre de la persona.
    public String getNombre() {

        return nombre;
    }

    // Modifica el nombre de la persona.
    public void setNombre(String nombre) {

        this.nombre = nombre;
    }

    // Obtiene el número de teléfono de la persona.
    public String getTelefono() {

        return telefono;
    }

    // Modifica el número de teléfono de la persona.
    public void setTelefono(String telefono) {

        this.telefono = telefono;
    }

    // Obtiene el correo electrónico de la persona.
    public String getCorreo() {

        return correo;
    }

    // Modifica el correo electrónico de la persona.
    public void setCorreo(String correo) {

        this.correo = correo;
    }
}