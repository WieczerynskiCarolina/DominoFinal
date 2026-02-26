package ar.edu.unlu.poo.Domino.Cliente;

import ar.edu.unlu.poo.Domino.Controlador.Controlador;
import ar.edu.unlu.poo.Domino.Vista.Ventana;
import ar.edu.unlu.poo.Domino.Vista.VistaConsola;
import ar.edu.unlu.poo.Domino.Vista.VistaGUI;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.Util;
import ar.edu.unlu.rmimvc.cliente.Cliente;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class AppCliente {
    public static void main(String[] args){
        ArrayList<String> ips = Util.getIpDisponibles();
        String ip = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione la IP en la que escuchará peticiones el cliente", "IP del cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                ips.toArray(),
                null
        );

        String port = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el puerto en el que escuchará peticiones el cliente", "Puerto del cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                9999
        );

        String ipServidor = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione la IP en el que corre el servidor", "IP del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null
        );

        String portServidor = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el puerto en el que corre el servidor", "Puerto del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                8888
        );

        Object[] opciones = {"Interfaz Gráfica", "Consola"};
        int eleccion = JOptionPane.showOptionDialog(
                null,
                "Seleccione la interfaz",
                "Modo de Vista",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        Cliente cliente = new Cliente(ip, Integer.parseInt(port), ipServidor, Integer.parseInt(portServidor));

        try{
            if(eleccion == 0){
                //Modo Gráfico
                Ventana ventana = new Ventana();
                Controlador controlador = new Controlador(ventana);

                cliente.iniciar(controlador);
                VistaGUI.setControlador(controlador);

                VistaGUI.lanzar(ventana, args);
            } else{
                //Modo Consola
                VistaConsola vistaConsola = new VistaConsola();
                Controlador controlador = new Controlador(vistaConsola);

                cliente.iniciar(controlador);
                vistaConsola.iniciar();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (RMIMVCException e) {
            e.printStackTrace();
        }
    }
}
