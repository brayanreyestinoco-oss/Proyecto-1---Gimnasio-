package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;

// ============================================================
// ResultadoAcceso.java
// QUÉ HACE:      Resultado de revisar un acceso: si el socio existe, si puede entrar, el motivo y
//                si quedó guardado.
// CONECTADO A:   Lo crea ControlAcceso y lo muestra ControladorAcceso.
// ============================================================
/**
 * Resultado de consultar una membresía o de intentar una entrada.
 *
 * @param idSocio     id del socio (null si la cédula no está registrada)
 * @param membresia   membresía encontrada (null si no hay)
 * @param socioExiste si la cédula pertenece a un socio registrado
 * @param permitido   si la membresía está vigente (entrada permitida)
 * @param motivo      razón del resultado (se guarda en MySQL)
 * @param registrado  si el resultado quedó guardado en MySQL
 */
public record ResultadoAcceso(
        Integer idSocio,
        Membresia membresia,
        boolean socioExiste,
        boolean permitido,
        String motivo,
        boolean registrado) {
}