package ar.edu.unlu.poo.Domino.Vista;

import ar.edu.unlu.poo.Domino.Controlador.Controlador;
import ar.edu.unlu.poo.Domino.Modelo.EntradaRanking;
import ar.edu.unlu.poo.Domino.Modelo.Ficha;
import ar.edu.unlu.poo.Domino.Modelo.Jugador;

import java.util.ArrayList;
import java.util.List;

public interface IVista {
    /*void setControlador(Controlador controlador);

    //métodos de inicio
    void mostrarBienvenida();
    void mostrarMensaje(String mensaje);

    //métodos de input
    String pedirNombreJugador();
    int pedirCantidadJugadores(); //Solo si hay admin
    int pedirPuntajeLimite(); //igual que el anterior

    //métodos de juego
    void mostrarLobby(List<String> nombreJugadores, int maxJugadores);
    void mostrarEstadoJuego(String jugadorActual, String mesa, List<Ficha> mano, boolean pozoVacio);
    void mostrarFinRonda(String ganador, int puntos);
    void mostrarGanadorJuego(String ganador);
    void mostrarPuntajesTotales(List<Jugador> jugadores); //final de ronda
    void mostrarMenu();
    void obtenerOpcion();
    int pedirIndiceFicha();
    int pedirLado();
    void esperarEnter();
    void mostrarRanking(List<EntradaRanking> ranking);

     */

    void setControlador(Controlador controlador);

    void iniciar(); // Arranca la UI
    void mostrarMensaje(String mensaje);

    // Métodos de Pantallas
    void mostrarLobby(List<String> nombres, int totalRequeridos);
    //void mostrarJuego(String jugadorActual, String mesa, ArrayList<Ficha> mano, boolean pozoVacio);
    void mostrarJuego(String jugadorActual, List<Ficha> fichasMesa, ArrayList<Ficha> mano, boolean pozoVacio);
    void mostrarFinRonda(String ganador, int puntos);
    void mostrarPuntajesTotales(List<Jugador> jugadores);
    void mostrarRanking(List<EntradaRanking> ranking); // Asumiendo que tenés el import
    void mostrarGanadorJuego(String nombreGanador, boolean soyElGanador);
    void mostrarCierreJuego();
    void solicitarContinuarRonda();

    // MÉTODOS CLAVE PARA EL FLUJO ÚNICO
    void activarControles(boolean mesaVacia);
    void desactivarControles();

    String pedirNombreJugador();
}