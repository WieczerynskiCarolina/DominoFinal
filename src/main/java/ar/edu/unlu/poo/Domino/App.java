package ar.edu.unlu.poo.Domino;

import ar.edu.unlu.poo.Domino.Controlador.Controlador;
import ar.edu.unlu.poo.Domino.Vista.VistaConsola;

public class App {
    public static void main(String[] args){
        VistaConsola vista = new VistaConsola();

        Controlador controlador = new Controlador(vista);

        vista.iniciar();
    }
}
