package cr.ac.una.est.controlmembresiasgimnasio.service;

import main.java.cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Socio;
import java.util.ArrayList;
import java.time.LocalDate;

import static jdk.jfr.internal.util.Utils.isBefore;

public class ControlAcceso {

    private ArrayList<Socio> socios;

    public ControlAcceso(ArrayList<Socio> socios) {
        this.socios = socios;
    }

    /*Busca en la lista de socios por medio de su id, por medio del parametro idSocio lo compara
    y retorna al socio o nulo si no existe*/
    public Socio buscarSocio(int idSocio) {
        for (Socio socio : socios) {

            if (socio.getIdSocio() == idSocio) {
                return socio;
            }
        }
        return null;
    }

    /*Verifica si un socio tiene la membresia vigente por medio de su identificador y retorna
    si esta vigente vencida o no existe*/
    public boolean verificarAcceso( int idSocio){

        Socio socio = buscarSocio(idSocio);

        if (socio == null) {
            return false;
        }
        LocalDate hoy = LocalDate.now();

        return !socio.getExpiraMembrecia() isBefore(hoy);
    }

}
