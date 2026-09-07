package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanAnual;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanMensual;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanVIP;

import java.util.ArrayList;

/**
 * Servicio encargado de la lógica de negocio relacionada con los planes
 * de membresía. Mantiene los planes disponibles en memoria.
 */
public class PlanService {

    private ArrayList<Plan> planesDisponibles;

    /**
     * Crea el servicio e inicializa los 3 planes fijos del gimnasio
     * (Mensual, Anual, VIP).
     */
    public PlanService() {
        this.planesDisponibles = new ArrayList<>();
        planesDisponibles.add(new PlanMensual());
        planesDisponibles.add(new PlanAnual());
        planesDisponibles.add(new PlanVIP());
    }

    /**
     * Obtiene la lista completa de planes disponibles en el gimnasio.
     * @return lista de planes
     */
    public ArrayList<Plan> getPlanesDisponibles() {
        return planesDisponibles;
    }

    /**
     * Busca un plan por su nombre exacto.
     * @param nombre nombre del plan a buscar (ej. "Mensual")
     * @return el plan encontrado, o null si no existe ninguno con ese nombre
     */
    public Plan buscarPorNombre(String nombre) {
        for (Plan plan : planesDisponibles) {
            if (plan.getNombre().equalsIgnoreCase(nombre)) {
                return plan;
            }
        }
        return null;
    }
}
