package ar.edu.unlu.poo.Domino.Servidor;

import ar.edu.unlu.poo.Domino.Modelo.IPartida;
import ar.edu.unlu.poo.Domino.Modelo.Partida;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.Util;
import ar.edu.unlu.rmimvc.servidor.Servidor;

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class AppServidor {
    public static void main(String[] args){
        ArrayList<String> ips = Util.getIpDisponibles();
        String ip = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione la IP en la que escuchará peticiones el servidor", "IP del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                ips.toArray(),
                null
        );

        String port = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el puerto en el que escuchará peticiones el servidor", "Puerto del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                8888
        );

        //Pedir config
        String cantStr = JOptionPane.showInputDialog("Cantidad de Jugadores (2-4): ");
        int cantidad = (cantStr != null && !cantStr.isEmpty()) ? Integer.parseInt(cantStr) : 2;

        String ptosStr = JOptionPane.showInputDialog("Puntaje Límite (Ej. 100): ");
        int limite = (ptosStr != null && !ptosStr.isEmpty()) ? Integer.parseInt(ptosStr) : 100;

        IPartida modelo = new Partida(cantidad, limite);
        Servidor servidor = new Servidor(ip, Integer.parseInt(port));

        try{
            servidor.iniciar(modelo);
        } catch (RemoteException e){
            e.printStackTrace();
        } catch (RMIMVCException e) {
            e.printStackTrace();
        }
    }
}
