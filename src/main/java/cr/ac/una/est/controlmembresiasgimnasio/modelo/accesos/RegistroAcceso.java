package cr.ac.una.est.controlmembresiasgimnasio.modelo.accesos;

import java.time.LocalDateTime;

// ============================================================
// RegistroAcceso.java
// QUÉ HACE:      Una fila del historial: fecha y hora, socio, cédula, si se permitió y el motivo.
// CONECTADO A:   La llena AccesoRepositoryJdbc y la muestran ControladorAcceso e
//                HistorialAccesosController.
// ============================================================
/**
 * Una fila del historial de entradas o de consultas.
 *
 * @param fechaHora fecha y hora del registro
 * @param nombre    nombre del socio (null si la cédula no está registrada)
 * @param cedula    cédula del socio o la que se consultó
 * @param permitido si se permitió la entrada / si la membresía está vigente
 * @param motivo    motivo de acceso o estado de consulta
 */
public record RegistroAcceso(
        LocalDateTime fechaHora,
        String nombre,
        String cedula,
        boolean permitido,
        String motivo) {
}