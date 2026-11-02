package cr.ac.una.est.controlmembresiasgimnasio.interfaces;

// ============================================================
// IRenovable.java
// QUÉ HACE:      Contrato para planes que se pueden renovar (dice si es automática y cuánto
//                cuesta).
// CONECTADO A:   Lo implementan PlanMensual, PlanAnual y PlanVIP.
// ============================================================
/**
 * Contrato para elementos del sistema que pueden renovarse periódicamente.
 * No todas las clases del sistema comparten una jerarquía común, por eso
 * se usa una interfaz en lugar de herencia.
 */
public interface IRenovable {

    /**
     * Indica si el elemento tiene renovación automática.
     * @return true si se renueva automáticamente, false si no
     */
    boolean esRenovableAutomaticamente();

    /**
     * Calcula el costo de renovar el elemento.
     * @return costo de la renovación en colones
     */
    double costoRenovacion();
}