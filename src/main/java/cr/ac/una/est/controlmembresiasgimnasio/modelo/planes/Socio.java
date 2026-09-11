package cr.ac.una.est.controlmembresiasgimnasio.modelo.planes;

import java.time.LocalDate;


public class Socio {

    private int idSocio;
    private String nombreSocio;
    private LocalDate expiraMembresia;

    public Socio(int idSocio, String nombreSocio, LocalDate expiraMembrecia) {
        this.idSocio = idSocio;
        this.nombreSocio = nombreSocio;
        this.expiraMembresia = expiraMembrecia;
    }
    //Retorna el numero de identificacion del socio sin recibir parametros
    public int getIdSocio()
    {
        return idSocio;
    }
    //Retorna el nombre del socio sin recibir parametros
    public String getNombreSocio()
    {
        return nombreSocio;
    }
    //Retorna la Fecha de expiracion de la membrecia sin recibir parametros
    public LocalDate getExpiraMembresia() {

        return expiraMembresia;
    }
}

