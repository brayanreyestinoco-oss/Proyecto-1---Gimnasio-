package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanAnual;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanMensual;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.PlanVIP;
import cr.ac.una.est.controlmembresiasgimnasio.repository.PlanRepository;
import cr.ac.una.est.controlmembresiasgimnasio.repository.PlanRepositoryJdbc;
import cr.ac.una.est.controlmembresiasgimnasio.repository.RepositorioException;

import java.sql.SQLException;
import java.util.ArrayList;

// ============================================================
// PlanService.java
// QUÉ HACE:      Reglas de los planes: crear, modificar, eliminar y buscar, y traduce los errores
//                de la base a mensajes claros.
// CONECTADO A:   PlanRepositoryJdbc y los modelos de plan. Lo usan PlanesController y
//                RegistrarPagoController.
// ============================================================
/**
 * Servicio con la lógica de negocio de los planes de membresía.
 * Los planes se guardan en MySQL a través de un PlanRepository.
 *
 * Errores:
 *  - IllegalArgumentException: datos inválidos o nombre repetido.
 *  - IllegalStateException: el plan no se puede eliminar porque está en uso.
 *  - RepositorioException: cualquier otro error de base de datos.
 */
public class PlanService {

    // Códigos de error de MySQL que se traducen a mensajes para el usuario.
    private static final int ERROR_DUPLICADO = 1062;
    private static final int ERROR_REGISTRO_REFERENCIADO = 1451;

    private final PlanRepository repository;

    /** Crea el servicio usando la implementación JDBC. */
    public PlanService() {
        this(new PlanRepositoryJdbc());
    }

    /** Crea el servicio con un repositorio dado (útil para pruebas). */
    public PlanService(PlanRepository repository) {
        this.repository = repository;
    }

    /** Obtiene todos los planes guardados en la base de datos. */
    public ArrayList<Plan> getPlanesDisponibles() {
        try {
            return new ArrayList<>(repository.listar());
        } catch (SQLException e) {
            throw new RepositorioException(
                    "No se pudieron consultar los planes.", e);
        }
    }

    /** Busca un plan por su nombre exacto; null si no existe. */
    public Plan buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        try {
            return repository.buscarPorNombre(nombre.trim());
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo buscar el plan.", e);
        }
    }

    /** Busca un plan por su identificador; null si no existe. */
    public Plan buscarPorId(int idPlan) {
        try {
            return repository.buscarPorId(idPlan);
        } catch (SQLException e) {
            throw new RepositorioException("No se pudo buscar el plan.", e);
        }
    }

    /**
     * Crea y guarda un plan nuevo.
     *
     * @param tipo MENSUAL, ANUAL o VIP
     * @param nombre nombre único del plan
     * @param precioBase precio base mayor que cero
     * @param ajuste descuento en % (ANUAL, 0 a 100) o recargo en colones (VIP);
     *               se ignora en MENSUAL
     * @param beneficios descripción de los beneficios
     * @return el plan guardado, ya con su id
     */
    public Plan crear(String tipo, String nombre, double precioBase,
                      double ajuste, String beneficios) {
        Plan plan = construir(0, tipo, nombre, precioBase, ajuste, beneficios);
        try {
            repository.guardar(plan);
            return plan;
        } catch (SQLException e) {
            throw traducir(e, "No se pudo guardar el plan.");
        }
    }

    /** Modifica un plan existente. El tipo no cambia. */
    public Plan modificar(int idPlan, String nombre, double precioBase,
                          double ajuste, String beneficios) {
        Plan existente = buscarPorId(idPlan);
        if (existente == null) {
            throw new IllegalArgumentException("El plan ya no existe.");
        }

        Plan actualizado = construir(idPlan, existente.getTipo(),
                nombre, precioBase, ajuste, beneficios);
        try {
            repository.actualizar(actualizado);
            return actualizado;
        } catch (SQLException e) {
            throw traducir(e, "No se pudo actualizar el plan.");
        }
    }

    /** Elimina un plan. Falla si hay membresías que lo usan. */
    public void eliminar(int idPlan) {
        try {
            if (!repository.eliminar(idPlan)) {
                throw new IllegalArgumentException("El plan ya no existe.");
            }
        } catch (SQLException e) {
            throw traducir(e, "No se pudo eliminar el plan.");
        }
    }

    // Valida los datos y crea la subclase de Plan que corresponde al tipo.
    private Plan construir(int idPlan, String tipo, String nombre,
                           double precioBase, double ajuste, String beneficios) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del plan es obligatorio.");
        }
        if (nombre.trim().length() > 50) {
            throw new IllegalArgumentException(
                    "El nombre del plan no puede pasar de 50 caracteres.");
        }
        if (precioBase <= 0) {
            throw new IllegalArgumentException(
                    "El precio base debe ser mayor que cero.");
        }
        if (beneficios == null || beneficios.isBlank()) {
            throw new IllegalArgumentException("Los beneficios son obligatorios.");
        }
        if (beneficios.trim().length() > 255) {
            throw new IllegalArgumentException(
                    "Los beneficios no pueden pasar de 255 caracteres.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Debe elegir el tipo de plan.");
        }

        String n = nombre.trim();
        String b = beneficios.trim();

        return switch (tipo) {
            case PlanMensual.TIPO -> new PlanMensual(idPlan, n, precioBase, b);
            case PlanAnual.TIPO -> {
                if (ajuste < 0 || ajuste > 100) {
                    throw new IllegalArgumentException(
                            "El descuento debe estar entre 0 y 100.");
                }
                yield new PlanAnual(idPlan, n, precioBase, ajuste, b);
            }
            case PlanVIP.TIPO -> {
                if (ajuste < 0) {
                    throw new IllegalArgumentException(
                            "El recargo no puede ser negativo.");
                }
                yield new PlanVIP(idPlan, n, precioBase, ajuste, b);
            }
            default -> throw new IllegalArgumentException(
                    "Tipo de plan no válido: " + tipo);
        };
    }

    // Convierte errores conocidos de MySQL en mensajes claros.
    private RuntimeException traducir(SQLException e, String mensajeGenerico) {
        if (e.getErrorCode() == ERROR_DUPLICADO) {
            return new IllegalArgumentException(
                    "Ya existe un plan con ese nombre.");
        }
        if (e.getErrorCode() == ERROR_REGISTRO_REFERENCIADO) {
            return new IllegalStateException(
                    "No se puede eliminar el plan porque hay membresías que lo usan.");
        }
        return new RepositorioException(mensajeGenerico, e);
    }
}