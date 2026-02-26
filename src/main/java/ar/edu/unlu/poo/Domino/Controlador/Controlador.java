package ar.edu.unlu.poo.Domino.Controlador;

import ar.edu.unlu.poo.Domino.Modelo.*;
import ar.edu.unlu.poo.Domino.Vista.IVista;
import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Controlador implements IControladorRemoto {
    private IPartida partida;
    private IVista vista;
    private String jugadorLocal;

    public Controlador(IVista vista){
        this.vista = vista;
        this.vista.setControlador(this);
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException {
        this.partida = (IPartida) modeloRemoto;
    }

    public void conectar(String nombre){
        try{
            this.jugadorLocal = nombre;
            boolean exito = partida.conectarJugador(nombre);
            if(exito){
                vista.mostrarMensaje("Conectado! Esperando...");
            } else{
                this.jugadorLocal = null;
                vista.mostrarMensaje("No se pudo conectar.");
            }
        } catch (RemoteException e) {
            this.jugadorLocal = null;
            //vista.mostrarMensaje("Error de conexión: " + e.getMessage());
            vista.mostrarMensaje(limpiarMensajeError(e));
        }
    }

    public void intentarJugarFicha(int indice, Lado lado){
        try{
            if (!partida.getJugadorActual().getNombre().equals(this.jugadorLocal)) {
                vista.mostrarMensaje("No es tu turno. Por favor, espera.");
                return;
            }

            if(indice < 0 || indice >= partida.getManoJugador(jugadorLocal).size()){
                vista.mostrarMensaje("Índice inválido.");
                vista.activarControles(partida.getMesa().estaVacia());
                return;
            }

            Ficha f = partida.getManoJugador(jugadorLocal).get(indice);
            partida.realizarJugada(f, lado);
            vista.desactivarControles();
        } catch (RemoteException e) {
            //vista.mostrarMensaje(e.getMessage());
            vista.mostrarMensaje(limpiarMensajeError(e));
            try {
                vista.activarControles(partida.getMesa().estaVacia());
            } catch (RemoteException ex) {
                vista.mostrarMensaje("Error de red al reactivar controles.");
            }
        }
    }

    public void intentarPasar(){
        try{
            partida.pasarTurno();
            vista.desactivarControles();
        } catch (RemoteException e) {
            //vista.mostrarMensaje(e.getMessage());
            vista.mostrarMensaje(limpiarMensajeError(e));
            try {
                vista.activarControles(partida.getMesa().estaVacia());
            } catch (RemoteException ex) {
                vista.mostrarMensaje("Error de red al reactivar controles.");
            }
        }
    }

    public void confirmarFinRonda(){
        try{
            partida.iniciarNuevaRonda();
        } catch (RemoteException e) {
            vista.mostrarMensaje("Error al iniciar ronda.");
        }
    }

    private void gestionarFinRonda(){
        try {
            Jugador ganadorRonda = partida.obtenerGanadorRonda();
            int puntosSumados = partida.calcularPuntosGanadorRonda();

            vista.mostrarFinRonda(ganadorRonda.getNombre(), puntosSumados);

            vista.mostrarPuntajesTotales(partida.getJugadores());

            if(ganadorRonda.getNombre().equals(this.jugadorLocal)){
                vista.mostrarMensaje(">>> GANASTE LA RONDA! <<<");
                vista.solicitarContinuarRonda();
            } else{
                vista.mostrarMensaje("Esperando a que " + ganadorRonda.getNombre() + " inicie la siguiente ronda...");
            }

        } catch (RemoteException e) {
            vista.mostrarMensaje("Error al mostrar el fin de la ronda.");
            e.printStackTrace();
        }
    }

    public List<Integer> obtenerIndicesJugables() {
        try {
            return partida.getIndicesJugables(this.jugadorLocal);
        } catch (RemoteException e) {
            vista.mostrarMensaje("Error al obtener fichas jugables: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void solicitarNuevaPartida(int nuevoLimite) {
        try {
            partida.reiniciarPartida(nuevoLimite);
        } catch (RemoteException e) {
            vista.mostrarMensaje("Error al reiniciar la partida: " + e.getMessage());
        }
    }

    private String limpiarMensajeError(RemoteException e) {
        Throwable causa = e.getCause();
        while (causa != null && causa.getCause() != null) {
            causa = causa.getCause();
        }
        if (causa != null && causa.getMessage() != null) {
            return causa.getMessage();
        }

        String msg = e.getMessage();
        if (msg != null && msg.contains(":")) {
            String[] partes = msg.split(":");
            return partes[partes.length - 1].trim();
        }
        return msg != null ? msg : "Error de comunicación con el servidor.";
    }

    public void cerrarMesa() {
        try {
            partida.cerrarMesa();
        } catch (RemoteException e) {
            vista.mostrarMensaje("Error al cerrar la mesa.");
        }
    }

    @Override
    public void actualizar(IObservableRemoto modelo, Object eventoObjeto) throws RemoteException{
        Eventos evento = (Eventos) eventoObjeto;

        switch (evento){
            case JUGADOR_CONECTADO:
                List<String> nombres = new ArrayList<>();
                for(Jugador j: partida.getJugadores()){
                    nombres.add(j.getNombre());
                }
                vista.mostrarLobby(nombres, partida.getCantidadJugadoresRequeridos());
                break;
            case INICIO_PARTIDA:
            case CAMBIO_DE_TURNO:
                if (this.jugadorLocal == null) break;

                if(!partida.rondaTerminada()){
                    String actual = partida.getJugadorActual().getNombre();

                    boolean mesaVacia = partida.getMesa().estaVacia();
                    List<Ficha> fichasEnMesa = partida.getMesa().getFichasEnMesa();

                    vista.mostrarJuego(actual, fichasEnMesa, partida.getManoJugador(jugadorLocal), partida.getPozo().estaVacia());

                    if(actual.equals(jugadorLocal)){
                        vista.mostrarMensaje(">>> ES TU TURNO <<<");
                        vista.activarControles(mesaVacia);
                    } else{
                        vista.mostrarMensaje("Turno de " + actual);
                        vista.desactivarControles();
                    }
                }
                break;
            case FIN_DE_RONDA:
                if (this.jugadorLocal == null) break;

                gestionarFinRonda();
                break;
            case FIN_DE_PARTIDA:
                if (this.jugadorLocal == null) break;

                try {
                    Jugador ganador = partida.obtenerGanadorRonda();
                    int puntos = partida.calcularPuntosGanadorRonda();
                    vista.mostrarFinRonda(ganador.getNombre(), puntos);
                    vista.mostrarPuntajesTotales(partida.getJugadores());

                    List<EntradaRanking> top5 = partida.obtenerRanking();
                    vista.mostrarRanking(top5);

                    boolean esGanadorLocal = ganador.getNombre().equals(this.jugadorLocal);
                    vista.mostrarGanadorJuego(ganador.getNombre(), esGanadorLocal);

                } catch(RemoteException e){
                    vista.mostrarMensaje("Error al recuperar el ranking remoto.");
                }
                break;
            case JUGADOR_ROBO_FICHA:
                if (this.jugadorLocal == null) break;

                vista.mostrarMensaje(">>> El jugador ROBÓ una ficha del pozo. <<<");
                break;
            case JUGADOR_PASO_TURNO:
                if (this.jugadorLocal == null) break;

                vista.mostrarMensaje(">>> El jugador PASÓ su turno (Pozo vacío). <<<");
                break;
            case MESA_CERRADA:
                if (this.jugadorLocal == null) break;

                vista.mostrarCierreJuego();
                break;
            default:
                vista.mostrarMensaje("Evento desconocido");
                break;
        }
    }
}
