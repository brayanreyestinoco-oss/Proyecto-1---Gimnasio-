package cr.ac.una.est.controlmembresiasgimnasio.interfaces;

public interface Gestionable<T> {


    void registrar(T objeto);


    T buscar(int id);

    boolean modificar(T objeto);

    boolean eliminar(int id);
}
