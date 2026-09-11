module cr.ac.una.est.controlmembresiasgimnasio {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.fxmlkit;

    opens cr.ac.una.est.controlmembresiasgimnasio to javafx.fxml;
    opens cr.ac.una.est.controlmembresiasgimnasio.vista to javafx.fxml, javafx.base;
    exports cr.ac.una.est.controlmembresiasgimnasio;
    exports cr.ac.una.est.controlmembresiasgimnasio.vista;
    exports cr.ac.una.est.controlmembresiasgimnasio.service;
    opens cr.ac.una.est.controlmembresiasgimnasio.service to javafx.fxml;
}