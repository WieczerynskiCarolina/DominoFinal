package ar.edu.unlu.poo.Domino.Modelo;

import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface IPartida extends IObservableRemoto {
    Jugador getJugadorActual() throws RemoteException;

    void iniciarPartida() throws RemoteException;

    Jugador obtenerGanadorRonda() throws RemoteException;

    int calcularPuntosGanadorRonda() throws RemoteException;

    void iniciarNuevaRonda() throws RemoteException;

    void pasarTurno() throws RemoteException;

    void realizarJugada(Ficha f, Lado lado) throws RemoteException;

    boolean hayGanadorDelJuego() throws RemoteException;

    Mesa getMesa() throws RemoteException;

    Pozo getPozo() throws RemoteException;

    ArrayList<Jugador> getJugadores() throws RemoteException;

    int getRondaNum() throws RemoteException;

    boolean rondaTerminada() throws RemoteException;

    List<EntradaRanking> obtenerRanking() throws RemoteException;

    boolean conectarJugador(String nombre) throws RemoteException;
    int getCantidadJugadoresRequeridos() throws RemoteException;
    ArrayList<Ficha> getManoJugador(String nombre) throws RemoteException;
    List<Integer> getIndicesJugables(String nombreNombre) throws RemoteException;
    void reiniciarPartida(int nuevoLimite) throws RemoteException;
    void cerrarMesa() throws RemoteException;
}
