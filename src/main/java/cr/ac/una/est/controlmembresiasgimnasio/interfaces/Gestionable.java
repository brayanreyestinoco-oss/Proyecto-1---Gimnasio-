package cr.ac.una.est.controlmembresiasgimnasio.interfaces;

// Interfaz genérica para definir operaciones básicas de gestión.
public interface Gestionable<T> {

    // Registra un objeto de tipo T.
    void registrar(T objeto);

    // Busca y devuelve un objeto de tipo T utilizando su ID.
    T buscar(int id);

    // Modifica un objeto existente y devuelve true si la operación fue exitosa.
    boolean modificar(T objeto);

    // Elimina un objeto utilizando su ID y devuelve true si la operación fue exitosa.
    boolean eliminar(int id);
}