module cr.ac.una.est.controlmembresiasgimnasio {
    requires javafx.controls;
    requires javafx.fxml;

    opens cr.ac.una.est.controlmembresiasgimnasio to javafx.fxml;
    opens cr.ac.una.est.controlmembresiasgimnasio.vista to javafx.fxml, javafx.base;
    exports cr.ac.una.est.controlmembresiasgimnasio;
}