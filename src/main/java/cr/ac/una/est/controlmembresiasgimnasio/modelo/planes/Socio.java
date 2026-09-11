package main.java.cr.ac.una.est.controlmembresiasgimnasio.modelo.planes;

import java.time.LocalDate;


public class Socio {

    private int idSocio;
    private String nombreSocio;
    private LocalDate expiraMembrecia;

    public Socio(int idSocio, String nombreSocio, LocalDate expiraMembrecia) {
        this.idSocio = idSocio;
        this.nombreSocio = nombreSocio;
        this.expiraMembrecia = expiraMembrecia;
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
    public LocalDate getExpiraMembrecia() {

        return expiraMembrecia;
    }
}

