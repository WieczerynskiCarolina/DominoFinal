package ar.edu.unlu.poo.Domino.Modelo;

import java.io.Serializable;

public class EntradaRanking implements Serializable, Comparable<EntradaRanking> {
    private static final long serialVersionIUD = 1L;
    private String nombre;
    private int puntaje;


    public EntradaRanking(String nombre, int puntaje){
        this.nombre = nombre;
        this.puntaje = puntaje;
    }

    public String getNombre(){
        return nombre;
    }

    public int getPuntaje(){
        return puntaje;
    }

    @Override
    public int compareTo(EntradaRanking otra){
        return Integer.compare(otra.puntaje, this.puntaje);
    }

    @Override
    public String toString(){
        return String.format("%-15s | %d pts", nombre, puntaje);
    }
}
