package cr.ac.una.est.controlmembresiasgimnasio.repository;

import cr.ac.una.est.controlmembresiasgimnasio.modelo.accesos.RegistroAcceso;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/** Contrato de persistencia del control de acceso; no depende de JDBC concreto. */
public interface AccesoRepository {
    /** @param cedula cédula exacta @return ID del socio o null @throws SQLException ante errores de lectura */
    Integer buscarIdSocioPorCedula(String cedula) throws SQLException;
    /** @param idSocio socio conocido @param cedula cédula digitada @param permitido resultado
     * @param motivo explicación @throws SQLException ante errores de escritura */
    void registrarIntento(Integer idSocio, String cedula, boolean permitido, String motivo) throws SQLException;
    /** @param idSocio socio que ingresó @throws SQLException ante errores de escritura */
    void registrarEntrada(int idSocio) throws SQLException;
    /** @param idSocio socio o null @param cedula cédula consultada @param vigente estado
     * @param estado motivo @throws SQLException ante errores de escritura */
    void registrarConsulta(Integer idSocio, String cedula, boolean vigente, String estado) throws SQLException;
    /** @param desde fecha inicial inclusiva o null @param limite máximo de filas
     * @return intentos permitidos y denegados @throws SQLException ante errores de lectura */
    List<RegistroAcceso> listar(LocalDateTime desde, int limite) throws SQLException;
    /** @param desde inicio inclusivo o null @param hasta fin exclusivo o null
     * @param socio cédula exacta o fragmento del nombre @param limite máximo de filas
     * @return intentos filtrados en MySQL @throws SQLException ante errores de lectura */
    List<RegistroAcceso> buscar(LocalDateTime desde, LocalDateTime hasta, String socio, int limite) throws SQLException;
    /** @param desde inicio inclusivo @param hasta fin exclusivo @param socio filtro
     * @param limite máximo @return consultas filtradas @throws SQLException ante errores de lectura */
    List<RegistroAcceso> buscarConsultas(LocalDateTime desde, LocalDateTime hasta, String socio, int limite) throws SQLException;
    /** @param desde fecha inicial inclusiva o null @param limite máximo de filas
     * @return consultas de membresías @throws SQLException ante errores de lectura */
    List<RegistroAcceso> listarConsultas(LocalDateTime desde, int limite) throws SQLException;
}
