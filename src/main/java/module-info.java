module cr.ac.una.est.controlmembresiasgimnasio {

    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.fxmlkit;

    /*
     * Permite que JavaFX cargue los controladores
     * y archivos FXML.
     */
    requires java.sql;
    // Permite utilizar JDBC y las clases de java.sql.

    /*
     * El driver de MySQL (mysql-connector-j) no se declara con requires:
     * java.sql lo descubre solo mediante ServiceLoader al estar en el
     * module path. Si al ejecutar aparece "No suitable driver found",
     * agregar: requires mysql.connector.j;
     */

    opens cr.ac.una.est.controlmembresiasgimnasio
            to javafx.fxml;

    opens cr.ac.una.est.controlmembresiasgimnasio.vista
            to javafx.fxml, javafx.base;

    /*
     * Permite que las TableView accedan
     * a las propiedades de los socios.
     */
    opens cr.ac.una.est.controlmembresiasgimnasio.modelo.socios
            to javafx.base;

    /*
     * Exportamos los paquetes utilizados
     * por la aplicación.
     */
    exports cr.ac.una.est.controlmembresiasgimnasio;

    exports cr.ac.una.est.controlmembresiasgimnasio.vista;

    exports cr.ac.una.est.controlmembresiasgimnasio.service;

    /*
     * Permite que JavaFX pueda acceder
     * al paquete de servicios si es necesario.
     */
    opens cr.ac.una.est.controlmembresiasgimnasio.service
            to javafx.fxml;
}