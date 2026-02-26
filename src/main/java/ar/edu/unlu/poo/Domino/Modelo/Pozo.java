package ar.edu.unlu.poo.Domino.Modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Pozo implements Serializable {
    private static final long serialVersionUID = 1L;
    private LinkedList<Ficha> fichas;

    public Pozo(){
        this.fichas = new LinkedList<>();
        for(int i = 0; i <= 6; i++){
            for(int j = i; j <= 6; j++){
                this.fichas.add(new Ficha(i,j));
            }
        }
    }

    public void mezclar(){
        Collections.shuffle(this.fichas);
    }

    public Ficha robarFicha(){
        if(!estaVacia()){
            return this.fichas.removeFirst();
        }
        return null;
    }

    public boolean estaVacia(){
        return this.fichas.isEmpty();
    }
}
