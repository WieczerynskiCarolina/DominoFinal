package ar.edu.unlu.poo.Domino.Vista;

import ar.edu.unlu.poo.Domino.Controlador.Controlador;
import ar.edu.unlu.poo.Domino.Modelo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VistaConsola implements IVista{
    private Scanner scanner = new Scanner(System.in);
    private Controlador controlador;

    @Override
    public void setControlador(Controlador controlador){
        this.controlador = controlador;
    }

    public void mostrarBienvenida(){
        System.out.println("=== DOMINO ===");
    }

    @Override
    public void iniciar(){
        mostrarBienvenida();
        String nombre = pedirNombreJugador();
        controlador.conectar(nombre);
    }

    @Override
    public void mostrarLobby(List<String> nombres, int totalRequeridos){
        System.out.println("/n--- SALA DE ESPERA ---");
        System.out.println("Jugadores conectados (" + nombres.size() + "/" + totalRequeridos + "): ");
        for(String n: nombres){
            System.out.println(" - " + n);
        }
        System.out.println("Esperando a que se llene la sala...");
    }

    public void mostrarMenu(){
        System.out.println("\n¿Qué hacer?");
        System.out.println("1. Jugar");
        System.out.println("2. Pasar");
        System.out.println("Seleccione una opción: ");
    }

    @Override
    public void activarControles(boolean mesaVacia){
        new Thread(() -> {
            boolean jugadaExitosa = false;
            while(!jugadaExitosa){
                mostrarMenu();
                int opcion = obtenerOpcion();

                if(opcion == 1){
                    int idx = pedirIndiceFicha() -1;

                    Lado lado = Lado.DERECHA;

                    if(!mesaVacia){
                        int ladoInt = pedirLado();
                        lado = (ladoInt == 1) ? Lado.IZQUIERDA : Lado.DERECHA;
                    }

                    controlador.intentarJugarFicha(idx, lado);
                    jugadaExitosa = true;
                } else if(opcion == 2){
                    controlador.intentarPasar();
                    jugadaExitosa = true;
                }
            }
        }).start();
    }

    @Override
    public void desactivarControles(){

    }

    @Override
    public void solicitarContinuarRonda(){
        new Thread(() -> {
            System.out.println("Presiona Enter para comenzar la siguiente ronda...");
            esperarEnter();
            controlador.confirmarFinRonda();
        }).start();
    }

    public synchronized int obtenerOpcion(){
        while (!scanner.hasNextInt()) {
            mostrarMensaje("Error: Debe ingresar un NÚMERO (1 o 2).");
            scanner.next();
            mostrarMenu();
        }

        int opcion = scanner.nextInt();
        scanner.nextLine();
        return opcion;
    }


    @Override
    public synchronized String pedirNombreJugador(){
        System.out.println("Ingrese el nombre del Jugador: ");
        return scanner.nextLine();
    }

    public void mostrarMensaje(String mensaje){
        System.out.println(mensaje);
    }


    @Override
    public void mostrarJuego(String jugadorActual, List<Ficha> fichasMesa, ArrayList<Ficha> mano, boolean pozoVacio) {
        System.out.println("\n\n==================================================");
        System.out.println("Turno de: " + jugadorActual);
        System.out.println("--------------------------------------------------");

        if (fichasMesa == null || fichasMesa.isEmpty()) {
            System.out.println("MESA (Vacía)");
            System.out.println("     Fichas jugadas: (Vacía)");
        } else {
            Ficha primeraFicha = fichasMesa.get(0);
            Ficha ultimaFicha = fichasMesa.get(fichasMesa.size() - 1);

            int extremoIzq = primeraFicha.getValorA();
            int extremoDer = ultimaFicha.getValorB();

            System.out.println("MESA (Extremos: [" + extremoIzq + "] ... [" + extremoDer + "])");

            StringBuilder sbMesa = new StringBuilder();
            sbMesa.append("     Fichas jugadas: ");
            for (Ficha f : fichasMesa) {
                sbMesa.append(f.toString()).append(" ");
            }
            System.out.println(sbMesa.toString());
        }
        System.out.println("--------------------------------------------------");

        String estadoPozo = pozoVacio ? "VACÍO" : "Fichas disponibles";
        System.out.println("Pozo: " + estadoPozo);

        StringBuilder sbMano = new StringBuilder();
        sbMano.append("Tu mano: ");

        if (mano == null || mano.isEmpty()) {
            sbMano.append("(Sin fichas)");
        } else {
            for (int i = 0; i < mano.size(); i++) {
                sbMano.append(i + 1).append(". ");
                sbMano.append(mano.get(i).toString());
                sbMano.append("  ");
            }
        }
        System.out.println(sbMano.toString().trim());
        System.out.println("==================================================");
    }


    @Override
    public void mostrarFinRonda(String ganador, int puntosGanados){
        System.out.println("\n==================================================");
        System.out.println("¡RONDA TERMINADA!");
        if (ganador != null) {
            System.out.println("Ganador de la ronda: " + ganador);
            System.out.println("Puntos sumados: " + puntosGanados);
        } else {
            System.out.println("¡Juego Cerrado! Nadie pudo mover. Ganó el de menos puntos en mano.");
        }
        System.out.println("==================================================");
    }



    @Override
    public void mostrarPuntajesTotales(List<Jugador> jugadores){
        System.out.println("\n--- Tabla de Posiciones ---");
        for(Jugador j : jugadores){
            System.out.println("  " + j.getNombre() + ": " + j.getPuntajeTotal() + " puntos");
        }
        System.out.println("---------------------------");
    }

    @Override
    public void mostrarGanadorJuego(String nombreGanador, boolean soyElGanador) {
        System.out.println("\n=============================================");
        System.out.println("             ¡FIN DEL JUEGO!");
        System.out.println("    Campeón absoluto: " + nombreGanador.toUpperCase());
        System.out.println("=============================================\n");

        if (soyElGanador) {
            // Uso un hilo (Thread) para no bloquear al servidor
            new Thread(() -> {
                System.out.println("¡Felicidades, ganaste la partida!");
                System.out.println("¿Qué deseas hacer ahora?");
                System.out.println("1. Jugar una NUEVA PARTIDA");
                System.out.println("2. Salir del juego");
                System.out.print("Elige una opción (1 o 2): ");

                java.util.Scanner scanner = new java.util.Scanner(System.in);
                String opcion = scanner.nextLine();

                if (opcion.equals("1")) {
                    System.out.print("Ingresa el nuevo puntaje límite (Ej. 100): ");
                    try {
                        int nuevoLimite = Integer.parseInt(scanner.nextLine());
                        System.out.println("Preparando mesa...");
                        controlador.solicitarNuevaPartida(nuevoLimite);
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada inválida. Se usará el límite 100.");
                        controlador.solicitarNuevaPartida(100);
                    }
                } else {
                    System.out.println("Cerrando la mesa para todos...");
                    controlador.cerrarMesa();
                    System.exit(0);
                }
            }).start();

        } else {
            System.out.println("Esperando a que " + nombreGanador + " decida si habrá nueva partida...");
        }
    }

    @Override
    public void mostrarCierreJuego() {
        System.out.println("\n=============================================");
        System.out.println("   El ganador ha decidido retirarse.");
        System.out.println("   La mesa se ha cerrado. ¡Gracias por jugar!");
        System.out.println("=============================================");
        System.exit(0);
    }


    public synchronized int pedirIndiceFicha() {
        System.out.println("Elige el número de la ficha: ");
        while(!scanner.hasNextInt()){
            mostrarMensaje("Error: Debe ingresar un número.");
            scanner.next();
            System.out.println("Elige el número de la ficha: ");
        }

        int idx = scanner.nextInt();
        scanner.nextLine();
        return idx;
    }

    public synchronized int pedirLado() {
        int opcionLado = 0;

        do{
            System.out.println("Elige el lado (1: Izquierda, 2: Derecha): ");

            while(!scanner.hasNextInt()){
                mostrarMensaje("Error: Debe ingresar un número");
                scanner.next();
                System.out.println("Elige el lado (1: Izquierda, 2: Derecha): ");
            }

            opcionLado = scanner.nextInt();
            scanner.nextLine();

            if(opcionLado != 1 && opcionLado != 2){
                mostrarMensaje("Error: Opción no válida. Ingrese 1 o 2.");
            }
        } while(opcionLado != 1 && opcionLado != 2);

        return opcionLado;
    }

    public synchronized void esperarEnter() {
        scanner.nextLine();
    }


    @Override
    public void mostrarRanking(List<EntradaRanking> ranking) {
        System.out.println("\n*********************************");
        System.out.println("  TOP 5 HISTÓRICO ");
        System.out.println("*********************************");
        if (ranking.isEmpty()) {
            System.out.println("¡Aún no hay récords registrados!");
        } else {
            int pos = 1;
            for (EntradaRanking entrada : ranking) {
                System.out.println(pos + ". " + entrada.toString());
                pos++;
            }
        }
        System.out.println("*********************************\n");
    }
}
