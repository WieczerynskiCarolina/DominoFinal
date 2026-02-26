package ar.edu.unlu.poo.Domino.Modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class Jugador implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private int puntaje;
    private ArrayList<Ficha> mano;

    public Jugador(String nombre){
        this.nombre = nombre;
        this.mano = new ArrayList<>();
        this.puntaje = 0;
    }

    public void agregarFichaAMano(Ficha ficha){
        this.mano.add(ficha);
    }

    public ArrayList<Ficha> getMano() {
        return new ArrayList<>(this.mano);
    }

    public String getNombre(){
        return nombre;
    }

    public int getPuntajeTotal() {
        return puntaje;
    }

    public void setPuntajeTotal(int puntaje){
        this.puntaje = puntaje;
    }

    public Boolean tieneManoVacia(){
        return mano.isEmpty();
    }

    public int sumarPuntosDeMano(){
        int puntos = 0;
        for(Ficha f: mano){
            puntos += f.getSumaPuntos();
        }
        return puntos;
    }

    public void sumarPuntaje(int puntos){
        this.puntaje += puntos;
    }

    public void vaciarMano() {
        this.mano = new ArrayList<>();
    }

    public void removerFichaDeMano(Ficha f){
        this.mano.remove(f);
    }

    public boolean puedeJugar(int[] extremosMesa){
        int extIzq = extremosMesa[0];
        int extDer = extremosMesa[1];

        if(extIzq == -1){
            return !this.mano.isEmpty();
        }

        for(Ficha f: this.mano){
            if(f.contieneValor(extIzq) || f.contieneValor(extDer)){
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.nombre).append(": ");
        sb.append(this.mano.toString());
        return sb.toString();
    }
}
