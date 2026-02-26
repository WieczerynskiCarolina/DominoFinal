package ar.edu.unlu.poo.Domino.Modelo;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Partida extends ObservableRemoto implements IObservableRemoto, IPartida {
    private ArrayList<Jugador> jugadores;
    private Pozo pozo;
    private Mesa mesa;
    private Jugador jugadorActual;
    private boolean juegoTerminado;
    private int pasesConsecutivos = 0;
    private int puntajeLimite;
    private int rondaNum = 0;
    private boolean rondaTerminada;
    private GestorRanking gestorRanking = new GestorRanking();
    private int cantidadJugadoresRequeridos;
    private boolean juegoIniciado;
    //private List<Observador> observadores;

    public Partida(int cantJugadores, int puntajeLim){
        this.jugadores = new ArrayList<>();
        this.cantidadJugadoresRequeridos = cantJugadores;
        this.puntajeLimite = puntajeLim;
        this.juegoTerminado = false;
        this.rondaTerminada = false;
        this.juegoIniciado = false;
    }

    @Override
    public Jugador getJugadorActual() throws RemoteException {
        return jugadorActual;
    }

    @Override
    public int getCantidadJugadoresRequeridos() throws RemoteException{
        return cantidadJugadoresRequeridos;
    }

    @Override
    public ArrayList<Ficha> getManoJugador(String nombre) throws RemoteException{
        for(Jugador j: jugadores){
            if(j.getNombre().equals(nombre)){
                return j.getMano();
            }
        }
        return null;
    }

    @Override
    public boolean conectarJugador(String nombre) throws RemoteException{
        if(juegoIniciado){
            throw new RemoteException("El juego ya comenzó, no puedes unirte");
        }
        if(jugadores.size() >= cantidadJugadoresRequeridos){
            throw new RemoteException("La sala está llena");
        }

        for(Jugador j: jugadores){
            if(j.getNombre().equalsIgnoreCase(nombre)){
                return false;
            }
        }

        jugadores.add(new Jugador(nombre));

        notificarObservadores(Eventos.JUGADOR_CONECTADO);

        if(jugadores.size() == cantidadJugadoresRequeridos){
            iniciarPartida();
        }

        return true;
    }

    @Override
    public void iniciarPartida() throws RemoteException{
        this.juegoTerminado = false;
        this.rondaNum = 0;
        iniciarNuevaRonda();
    }

    @Override
    public List<Integer> getIndicesJugables(String nombreJugador) throws RemoteException {
        List<Integer> indices = new ArrayList<>();
        Jugador jugadorBuscado = null;

        for (Jugador j : jugadores) {
            if (j.getNombre().equals(nombreJugador)) {
                jugadorBuscado = j;
                break;
            }
        }

        if (jugadorBuscado == null) return indices;

        int[] extremos = mesa.estaVacia() ? null : mesa.getExtremos();
        List<Ficha> mano = jugadorBuscado.getMano();

        for (int i = 0; i < mano.size(); i++) {
            if (mesa.estaVacia() || mano.get(i).puedeJugarEn(extremos)) {
                indices.add(i);
            }
        }
        return indices;
    }

    @Override
    public Jugador obtenerGanadorRonda() throws RemoteException{
        for(Jugador j: jugadores){
            if(j.tieneManoVacia()){
                return j;
            }
        }

        Jugador ganador = null;

        int min = Integer.MAX_VALUE;

        for(Jugador j: jugadores){
            int puntos = j.sumarPuntosDeMano();
            if(puntos < min){
                min = puntos;
                ganador = j;
            }
        }
        return ganador;
    }

    private void calcularPuntosGanadorRondaYAsignar() throws RemoteException {
        Jugador ganador = obtenerGanadorRonda();
        if(ganador != null){
            int puntos = calcularPuntosGanadorRonda();
            ganador.sumarPuntaje(puntos);
        }
    }

    @Override
    public int calcularPuntosGanadorRonda() throws RemoteException{
        Jugador ganador = obtenerGanadorRonda();
        int total = 0;

        for(Jugador j: jugadores){
            if(j != ganador){
                total += j.sumarPuntosDeMano();
            }
        }

        return total;
    }

    @Override
    public boolean hayGanadorDelJuego() throws RemoteException{
        return juegoTerminado;
    }

    @Override
    public void iniciarNuevaRonda() throws RemoteException{
        if(juegoTerminado){
            return;
        }

        if(!rondaTerminada && rondaNum > 0){
            return;
        }

        this.rondaNum++;
        this.rondaTerminada = false;
        this.mesa = new Mesa();
        this.pozo = new Pozo();
        this.pozo.mezclar();
        this.pasesConsecutivos = 0;

        for(Jugador j: jugadores){
            j.vaciarMano();
        }

        repartirFichas();

        this.pasesConsecutivos = 0;
        this.juegoTerminado = false;

        determinarJugadorInicial();

        if(rondaNum == 1){
            notificarObservadores(Eventos.INICIO_PARTIDA);
        } else{
            notificarObservadores(Eventos.CAMBIO_DE_TURNO);
        }
    }

    private void verificarEstadoDelJuego(){
        if(jugadorActual.getPuntajeTotal() == puntajeLimite){
            juegoTerminado = true;
        }
    }

    private void verificarGanadorDelJuego(){
        for(Jugador j: jugadores){
            if(j.getPuntajeTotal() >= this.puntajeLimite){
                this.juegoTerminado = true;
                gestorRanking.registrarPuntaje(j.getNombre(), j.getPuntajeTotal());
                break;
            }
        }
    }

    @Override
    public List<EntradaRanking> obtenerRanking() throws RemoteException{
        return gestorRanking.getTop5();
    }


    @Override
    public void pasarTurno() throws RemoteException {
        int[] extremos = mesa.getExtremos();

        if (jugadorActual.puedeJugar(extremos)) {
            throw new RemoteException("No puedes pasar, tienes fichas jugables.");
        }

        if (!pozo.estaVacia()) {
            Ficha robada = pozo.robarFicha();
            jugadorActual.agregarFichaAMano(robada);
            notificarObservadores(Eventos.JUGADOR_ROBO_FICHA);

            if(!rondaTerminada && !juegoTerminado) {
                notificarObservadores(Eventos.CAMBIO_DE_TURNO);
            }
            return;
        }

        this.pasesConsecutivos++;

        notificarObservadores(Eventos.JUGADOR_PASO_TURNO);

        verificarEstadoRonda();

        if(juegoTerminado){
            notificarObservadores(Eventos.FIN_DE_PARTIDA);
        } else if(rondaTerminada){
            notificarObservadores(Eventos.FIN_DE_RONDA);
        } else{
            siguienteTurno();
            notificarObservadores(Eventos.CAMBIO_DE_TURNO);
        }
    }

    private boolean jugarFichasEnMesa(Ficha f, Lado lado){
        if(!this.jugadorActual.getMano().contains(f)){
            return false;
        }

        if(this.mesa.estaVacia()){
            this.mesa.agregarPrimeraFicha(f);
            return true;
        } else{
            if(lado == Lado.IZQUIERDA){
                return this.mesa.agregarFichaPorIzquierda(f);
            } else{
                return this.mesa.agregarFichaPorDerecha(f);
            }
        }
    }

    private void verificarEstadoRonda() throws RemoteException {
        boolean manoVacia = jugadorActual.tieneManoVacia();
        boolean juegoTrancado = (pasesConsecutivos >= jugadores.size());

        if(manoVacia || juegoTrancado){
            this.rondaTerminada = true;

            calcularPuntosGanadorRondaYAsignar();

            verificarGanadorDelJuego();
        }
    }

    @Override
    public void realizarJugada(Ficha f, Lado lado) throws RemoteException {
        boolean exito = jugarFichasEnMesa(f,lado);
        if(!exito){
            throw new RemoteException("La ficha no coincide con el extremo o no la tienes.");
        }

        jugadorActual.removerFichaDeMano(f);
        this.pasesConsecutivos = 0;
        verificarEstadoRonda();

        if(juegoTerminado){
            notificarObservadores(Eventos.FIN_DE_PARTIDA);
        } else if(rondaTerminada){
            notificarObservadores(Eventos.FIN_DE_RONDA);
        } else{
            siguienteTurno();
            notificarObservadores(Eventos.CAMBIO_DE_TURNO);
        }
    }


    private void determinarJugadorInicial(){
        Ficha mejorDoble = null;
        Ficha fichaMasAlta = null;
        Jugador jugadorQueInicia = null;

        // buscamos ficha doble más alta
        for(Jugador j: this.jugadores){
            for(Ficha f: j.getMano()){
                if(f.esDoble()){
                    if(mejorDoble == null || f.getSumaPuntos() > mejorDoble.getSumaPuntos()){
                        mejorDoble = f;
                        jugadorQueInicia = j;
                    }
                }
            }
        }

        // si no se encontró dobles, buscamos la ficha más alta
        if(jugadorQueInicia == null){
            for(Jugador j: this.jugadores){
                for(Ficha f: j.getMano()){
                    if(fichaMasAlta == null || f.getSumaPuntos() > fichaMasAlta.getSumaPuntos()){
                        fichaMasAlta = f;
                        jugadorQueInicia = j;
                    }
                }
            }
        }

        this.jugadorActual = jugadorQueInicia;

        System.out.println("PARTIDA INICIADA. JUGADOR INICIAL: " + this.jugadorActual.getNombre());
    }

    private void repartirFichas(){
        int fichasARepartir = 7;

        for(Jugador j: this.jugadores){
            for(int i = 0; i < fichasARepartir; i++){
                if(!this.pozo.estaVacia()){
                    Ficha fichaRepartida = this.pozo.robarFicha();
                    j.agregarFichaAMano(fichaRepartida);
                }
            }
        }
    }

    @Override
    public void reiniciarPartida(int nuevoLimite) throws RemoteException {
        this.puntajeLimite = nuevoLimite;

        for (Jugador j : jugadores) {
            j.setPuntajeTotal(0);
        }

        this.juegoTerminado = false;

        this.iniciarPartida();
    }

    @Override
    public void cerrarMesa() throws RemoteException{
        notificarObservadores(Eventos.MESA_CERRADA);
    }

    @Override
    public Mesa getMesa() throws RemoteException{
        return this.mesa;
    }

    @Override
    public Pozo getPozo() throws RemoteException{
        return this.pozo;
    }

    @Override
    public ArrayList<Jugador> getJugadores() throws RemoteException{
        return this.jugadores;
    }

    @Override
    public int getRondaNum() throws RemoteException{
        return rondaNum;
    }

    private void siguienteTurno(){
        int indiceActual = this.jugadores.indexOf(this.jugadorActual);

        int indiceSiguiente = (indiceActual + 1) % this.jugadores.size();

        this.jugadorActual = this.jugadores.get(indiceSiguiente);
    }

    @Override
    public boolean rondaTerminada() throws RemoteException{
        return rondaTerminada;
    }

    /*@Override
    public void agregarObservador(Observador observador){
        observadores.add(observador);
    }

    @Override
    public void quitarObservador(Observador observador){
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(Eventos evento, Object objeto){
        for(Observador observador: observadores){
            observador.actualizar(evento, objeto);
        }
    }*/
}