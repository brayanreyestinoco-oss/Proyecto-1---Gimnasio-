package cr.ac.una.est.controlmembresiasgimnasio.service;

import cr.ac.una.est.controlmembresiasgimnasio.bd.ConexionBD;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Membresia;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.pagos.Pago;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.planes.Plan;
import cr.ac.una.est.controlmembresiasgimnasio.modelo.socios.Socio;
import cr.ac.una.est.controlmembresiasgimnasio.repository.MembresiaRepository;
import cr.ac.una.est.controlmembresiasgimnasio.repository.MembresiaRepositoryJdbc;
import cr.ac.una.est.controlmembresiasgimnasio.repository.PagoRepository;
import cr.ac.una.est.controlmembresiasgimnasio.repository.PagoRepositoryJdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Servicio encargado de administrar pagos y membresías.
 *
 * Utiliza los repositorios JDBC para guardar y consultar
 * los datos de la base de datos MySQL.
 */
public class PagoService {

    private final PagoRepository pagoRepository;
    private final MembresiaRepository membresiaRepository;

    /**
     * Inicializa los repositorios.
     */
    public PagoService() {
        this.pagoRepository = new PagoRepositoryJdbc();
        this.membresiaRepository = new MembresiaRepositoryJdbc();
    }

    /**
     * Consulta los pagos registrados en MySQL.
     */
    public ArrayList<Pago> getPagos() {
        try {
            return new ArrayList<>(pagoRepository.listar());
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar los pagos.", e);
        }
    }

    /**
     * Consulta las membresías registradas en MySQL.
     */
    public ArrayList<Membresia> getMembresias() {
        try {
            return new ArrayList<>(membresiaRepository.listar());
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar las membresías.", e);
        }
    }

    /**
     * Registra una nueva membresía.
     *
     * Se conserva el parámetro idMembresia para mantener
     * compatibilidad con los controladores existentes.
     *
     * MySQL genera el identificador definitivo.
     */
    public Membresia registrarMembresia(
            int idMembresia,
            Socio socio,
            Plan plan,
            LocalDate fechaInicio) {

        if (socio == null) {
            throw new IllegalArgumentException(
                    "El socio no puede ser nulo.");
        }

        if (plan == null) {
            throw new IllegalArgumentException(
                    "El plan no puede ser nulo.");
        }

        if (fechaInicio == null) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser nula.");
        }

        if (buscarMembresiaPorSocio(socio.getIdSocio()) != null) {
            throw new IllegalArgumentException(
                    "El socio ya tiene una membresía registrada.");
        }

        Membresia membresia = new Membresia(
                idMembresia, socio, plan, fechaInicio);

        try (Connection conexion = ConexionBD.obtenerConexion()) {

            membresiaRepository.guardar(membresia, conexion);
            return membresia;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo registrar la membresía.", e);
        }
    }

    /**
     * Registra un pago y renueva la membresía.
     *
     * Ambas modificaciones se realizan dentro
     * de una misma transacción JDBC.
     */
    public Pago registrarPago(
            int idPago,
            Membresia membresia,
            double monto,
            LocalDate fechaPago,
            String metodoPago) {

        validarDatosPago(
                membresia, monto, fechaPago, metodoPago);

        if (membresia.getIdMembresia() <= 0) {
            throw new IllegalArgumentException(
                    "La membresía debe estar registrada en MySQL.");
        }

        // Guardamos el estado original para restaurarlo
        // si la transacción falla.
        LocalDate inicioAnterior = membresia.getFechaInicio();
        LocalDate vencimientoAnterior =
                membresia.getFechaVencimiento();
        boolean activaAnterior = membresia.isActiva();

        try (Connection conexion = ConexionBD.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {
                // Verificar que el socio y el plan de la membresía
                // coincidan con registros existentes en MySQL.
                validarReferenciasEnBD(
                        conexion,
                        membresia.getSocio(),
                        membresia.getPlan()
                );
                // Actualizar las fechas en el objeto Java.
                actualizarVencimiento(membresia, fechaPago);

                // Actualizar la membresía en MySQL.
                boolean actualizada =
                        membresiaRepository.actualizar(
                                membresia, conexion);

                if (!actualizada) {
                    throw new SQLException(
                            "No se encontró la membresía para actualizar.");
                }

                // Crear el objeto Pago.
                Pago pago = new Pago(
                        idPago,
                        membresia,
                        monto,
                        fechaPago,
                        metodoPago
                );

                // Guardar el pago utilizando la misma conexión.
                pagoRepository.guardar(pago, conexion);

                // Confirmar las dos operaciones.
                conexion.commit();

                return pago;

            } catch (SQLException | RuntimeException e) {

                // Indica si MySQL pudo revertir la transacción.
                boolean rollbackExitoso = false;

                try {

                    // Intentar deshacer los cambios realizados.
                    conexion.rollback();

                    // La operación rollback terminó sin errores.
                    rollbackExitoso = true;

                } catch (SQLException errorRollback) {

                    // Guardar también el error ocurrido al revertir.
                    e.addSuppressed(errorRollback);
                }

                // Restaurar el objeto Java si la reversión
                // se completó correctamente.
                if (rollbackExitoso) {

                    membresia.setFechaInicio(inicioAnterior);

                    membresia.setFechaVencimiento(
                            vencimientoAnterior
                    );

                    membresia.setActiva(activaAnterior);
                }

                // Informar del fallo sin asegurar una reversión
                // cuando no fue posible comprobarla.
                throw new IllegalStateException(
                        rollbackExitoso
                                ? "No se pudo registrar el pago. "
                                + "Se ejecutó la reversión de la transacción."
                                : "No se pudo registrar el pago "
                                + "y no fue posible confirmar la reversión.",
                        e
                );
            }

        } catch (SQLException e) {

            membresia.setFechaInicio(inicioAnterior);
            membresia.setFechaVencimiento(vencimientoAnterior);
            membresia.setActiva(activaAnterior);

            throw new IllegalStateException(
                    "Error de conexión al registrar el pago.", e);
        }
    }

    /**
     * Crea una membresía nueva y registra su primer pago.
     *
     * Las dos operaciones se ejecutan dentro de una única
     * transacción JDBC para garantizar que ambas se guarden
     * correctamente o que ninguna quede registrada.
     *
     * MySQL genera automáticamente los identificadores.
     *
     * @param socio socio que realiza el pago
     * @param plan plan seleccionado
     * @param monto monto del primer pago
     * @param fechaPago fecha del pago
     * @param metodoPago método de pago utilizado
     * @return pago registrado con su ID generado por MySQL
     */
    public Pago registrarPrimerPago(
            Socio socio,
            Plan plan,
            double monto,
            LocalDate fechaPago,
            String metodoPago) {

        // Verificamos que exista un socio válido.
        if (socio == null || socio.getIdSocio() <= 0) {
            throw new IllegalArgumentException(
                    "El socio debe estar registrado en MySQL.");
        }

        // Verificamos que el plan exista en la base de datos.
        if (plan == null || plan.getIdPlan() <= 0) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un plan registrado.");
        }

        // Creamos la membresía inicialmente sin identificador.
        // MySQL generará el ID cuando se inserte.
        Membresia membresia = new Membresia(
                0,
                socio,
                plan,
                fechaPago
        );

        // Reutilizamos las validaciones del servicio.
        validarDatosPago(
                membresia,
                monto,
                fechaPago,
                metodoPago
        );

        try (Connection conexion = ConexionBD.obtenerConexion()) {

            // Desactivamos la confirmación automática.
            conexion.setAutoCommit(false);

            try {

                // Comprobar que el socio y el plan existen
                // y que sus identificadores coinciden con MySQL.
                validarReferenciasEnBD(
                        conexion,
                        socio,
                        plan
                );

                // 1. Guardar la nueva membresía.
                membresiaRepository.guardar(
                        membresia,
                        conexion
                );
                // 2. Calcular su primer vencimiento.
                actualizarVencimiento(
                        membresia,
                        fechaPago
                );

                // 3. Actualizar las fechas y activar la membresía.
                boolean actualizada =
                        membresiaRepository.actualizar(
                                membresia,
                                conexion
                        );

                if (!actualizada) {
                    throw new SQLException(
                            "No se pudo activar la membresía.");
                }

                // 4. Crear el pago asociado a esa membresía.
                Pago pago = new Pago(
                        0,
                        membresia,
                        monto,
                        fechaPago,
                        metodoPago
                );

                // 5. Registrar el pago usando la misma conexión.
                pagoRepository.guardar(
                        pago,
                        conexion
                );

                // 6. Confirmar todas las operaciones juntas.
                conexion.commit();

                // Devolver el pago ya registrado.
                return pago;

            } catch (SQLException | RuntimeException e) {

                // Intentamos deshacer las operaciones realizadas en MySQL.
                boolean rollbackExitoso = false;

                try {
                    conexion.rollback();
                    rollbackExitoso = true;

                } catch (SQLException errorRollback) {

                    // Conservamos información de ambos errores.
                    e.addSuppressed(errorRollback);
                }

                // Restauramos los datos del objeto Java únicamente
                // cuando MySQL confirmó que la reversión fue exitosa.
                if (rollbackExitoso) {

                    // La membresía no quedó registrada.
                    membresia.setIdMembresia(0);

                    // Restauramos su estado inicial.
                    membresia.setFechaInicio(fechaPago);
                    membresia.setFechaVencimiento(null);
                    membresia.setActiva(false);
                }

                throw new IllegalStateException(
                        rollbackExitoso
                                ? "No se pudo registrar el primer pago. "
                                + "La transacción fue revertida."
                                : "No se pudo registrar el primer pago "
                                + "y no fue posible confirmar la reversión.",
                        e
                );
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error de conexión al registrar el primer pago.",
                    e
            );
        }
    }

    /**
     * Busca la membresía de un socio.
     */
    public Membresia buscarMembresiaPorSocio(int idSocio) {
        try {
            return membresiaRepository.buscarPorSocio(idSocio);
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar la membresía del socio.", e);
        }
    }

    /**
     * Busca una membresía mediante su identificador.
     */
    public Membresia buscarMembresia(int idMembresia) {
        try {
            return membresiaRepository.buscarPorId(idMembresia);
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar la membresía.", e);
        }
    }

    /**
     * Renueva una membresía después de realizar un pago.
     *
     * Si todavía está vigente, suma el período desde
     * el vencimiento actual.
     *
     * Si está vencida, comienza desde la fecha del pago.
     */
    private void actualizarVencimiento(
            Membresia membresia,
            LocalDate fechaPago) {

        LocalDate fechaBase;

        if (membresia.isActiva()
                && membresia.getFechaVencimiento() != null
                && !membresia.getFechaVencimiento()
                .isBefore(fechaPago)) {

            fechaBase = membresia.getFechaVencimiento();

        } else {

            fechaBase = fechaPago;
            membresia.setFechaInicio(fechaPago);
        }

        LocalDate nuevoVencimiento =
                fechaBase.plusMonths(
                        membresia.getPlan().getDuracionEnMeses());

        membresia.setFechaVencimiento(nuevoVencimiento);
        membresia.setActiva(true);
    }

    /**
     * Valida los datos de un pago antes de procesarlo.
     */
    private void validarDatosPago(
            Membresia membresia,
            double monto,
            LocalDate fechaPago,
            String metodoPago) {

        if (membresia == null) {
            throw new IllegalArgumentException(
                    "La membresía no puede ser nula.");
        }

        if (!Double.isFinite(monto) || monto <= 0) {
            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero.");
        }

        if (fechaPago == null) {
            throw new IllegalArgumentException(
                    "La fecha del pago no puede ser nula.");
        }

        if (metodoPago == null || metodoPago.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe indicar el método de pago.");
        }

        if (metodoPago.length() > 30) {
            throw new IllegalArgumentException(
                    "El método de pago no puede superar 30 caracteres.");
        }

        if (membresia.getPlan() == null) {
            throw new IllegalArgumentException(
                    "La membresía debe tener un plan.");
        }
    }

    /**
     * Verifica si una membresía está vigente hoy.
     */
    public boolean membresiaEstaVigente(Membresia membresia) {
        return membresia != null
                && membresia.estaVigente(LocalDate.now());
    }

    /**
     * Comprueba si un socio tiene acceso vigente.
     */
    public boolean socioTieneAcceso(int idSocio) {
        Membresia membresia = buscarMembresiaPorSocio(idSocio);

        return membresiaEstaVigente(membresia);
    }

    /**
     * Busca una membresía por la cédula del socio.
     *
     * Conserva el método original utilizado
     * por RegistrarPagoController.
     */
    public Membresia buscarMembresiaPorCedula(String cedula) {

        if (cedula == null || cedula.isBlank()) {
            return null;
        }

        for (Membresia membresia : getMembresias()) {

            if (membresia.getSocio().getCedula()
                    .equalsIgnoreCase(cedula.trim())) {

                return membresia;
            }
        }

        return null;
    }

    /**
     * Verifica que el socio y el plan realmente existan
     * en MySQL antes de registrar una membresía.
     *
     * La validación utiliza la misma conexión JDBC
     * que la transacción del primer pago.
     *
     * @param conexion conexión activa de MySQL
     * @param socio socio que realizará el pago
     * @param plan plan seleccionado
     * @throws SQLException si los datos no coinciden
     */
    private void validarReferenciasEnBD(
            Connection conexion,
            Socio socio,
            Plan plan) throws SQLException {

        // 1. Buscar al socio utilizando su ID y su cédula.
        String sqlSocio =
                "SELECT id_socio "
                        + "FROM socio "
                        + "WHERE id_socio = ? AND cedula = ?";

        try (PreparedStatement ps =
                     conexion.prepareStatement(sqlSocio)) {

            // Asignar el ID recibido desde Java.
            ps.setInt(1, socio.getIdSocio());

            // Asignar la cédula del socio.
            ps.setString(2, socio.getCedula());

            // Ejecutar la consulta.
            try (ResultSet rs = ps.executeQuery()) {

                // Si no existe coincidencia, detener el proceso.
                if (!rs.next()) {

                    throw new SQLException(
                            "El socio no coincide con un registro "
                                    + "de MySQL. Verifique su ID y cédula."
                    );
                }
            }
        }

        // 2. Comprobar que el plan seleccionado exista.
        String sqlPlan =
                "SELECT id_plan "
                        + "FROM plan "
                        + "WHERE id_plan = ? AND nombre = ?";

        try (PreparedStatement ps =
                     conexion.prepareStatement(sqlPlan)) {

            // Asignar el identificador del plan.
            ps.setInt(1, plan.getIdPlan());

            // Asignar el nombre del plan.
            ps.setString(2, plan.getNombre());

            // Ejecutar la consulta.
            try (ResultSet rs = ps.executeQuery()) {

                // Si el plan no coincide, detener el registro.
                if (!rs.next()) {

                    throw new SQLException(
                            "El plan seleccionado no coincide "
                                    + "con un registro de MySQL."
                    );
                }
            }
        }
    }
}