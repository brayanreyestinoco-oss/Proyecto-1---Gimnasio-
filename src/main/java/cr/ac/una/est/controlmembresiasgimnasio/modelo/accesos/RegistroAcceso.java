package cr.ac.una.est.controlmembresiasgimnasio.modelo.accesos;

import java.time.LocalDateTime;

/**
 * Una fila del historial de entradas o de consultas.
 *
 * @param fechaHora fecha y hora del registro
 * @param nombre    nombre del socio (null si la cédula no está registrada)
 * @param cedula    cédula del socio o la que se consultó
 * @param permitido si se permitió la entrada / si la membresía está vigente
 * @param motivo    plan (entradas) o estado (consultas)
 */
public record RegistroAcceso(
        LocalDateTime fechaHora,
        String nombre,
        String cedula,
        boolean permitido,
        String motivo) {
}