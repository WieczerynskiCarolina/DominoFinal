package ar.edu.unlu.poo.Domino.Vista;

import ar.edu.unlu.poo.Domino.Controlador.Controlador;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class VistaGUI extends Application{
    static Ventana manejadorVista;
    public static Controlador controlador;

    public static void setControlador(Controlador unControlador){
        controlador = unControlador;
    }

    @Override
    public void start(Stage stage) throws IOException{
        java.net.URL fxmlUrl = VistaGUI.class.getResource("/com/example/dominotpf2/main-view.fxml");

        if (fxmlUrl == null) {
            System.out.println("¡ERROR! No se encontró el archivo FXML en esa ruta.");
            System.exit(1);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        fxmlLoader.setController(VistaGUI.manejadorVista);

        Scene scene = new Scene(fxmlLoader.load());

        stage.setResizable(false);

        scene.setOnKeyPressed(event -> {
            if(event.getCode() == javafx.scene.input.KeyCode.F11){
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        stage.setTitle("POO Dominó");
        stage.setScene(scene);
        stage.show();

        manejadorVista.iniciar();
    }

    public VistaGUI(){

    }

    public static void lanzar(Ventana manejadorVista, String[] args){
        VistaGUI.manejadorVista = manejadorVista;
        launch(args);
    }

    @Override
    public void stop() throws Exception{
        super.stop();
        if(controlador != null){
            try{
                UnicastRemoteObject.unexportObject(controlador,true);
                System.out.println("Controlador des-exportado del registro RMI.");
            } catch (RemoteException e){
                System.err.println("Error al des-exportar el controlador: " + e.getMessage());
            }
        }
        System.exit(0);
    }
}
