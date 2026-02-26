module com.example.dominotpf2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires LibreriaRMIMVC;
    requires java.rmi;
    requires java.desktop;


    opens com.example.dominotpf2 to javafx.fxml;
    exports ar.edu.unlu.poo.Domino.Modelo;
    exports ar.edu.unlu.poo.Domino.Controlador;

    opens ar.edu.unlu.poo.Domino.Modelo to java.rmi;
    opens ar.edu.unlu.poo.Domino.Controlador to java.rmi;

    exports ar.edu.unlu.poo.Domino.Vista to javafx.graphics;

    opens ar.edu.unlu.poo.Domino.Vista to javafx.fxml;
}