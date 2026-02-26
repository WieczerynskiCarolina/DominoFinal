package ar.edu.unlu.poo.Domino.Modelo;

import java.io.Serializable;

public class Ficha implements Serializable {
    private static final long serialVersionUID = 1L;
    private int valorA;
    private int valorB;

    public Ficha(int a, int b){
        this.valorA = a;
        this.valorB = b;
    }

    public int getValorA() {
        return valorA;
    }

    public int getValorB() {
        return valorB;
    }

    public boolean esDoble(){
        return valorA == valorB;
    }

    public int getSumaPuntos(){
        return valorA + valorB;
    }

    public boolean puedeJugarEn(int[] extremos) {
        if (extremos == null || extremos.length != 2) return true;

        return this.getValorA() == extremos[0] || this.getValorA() == extremos[1] ||
                this.getValorB() == extremos[0] || this.getValorB() == extremos[1];
    }

    public boolean contieneValor(int valor){
        return valorA == valor || valorB == valor;
    }

    public int getOtroValor(int valor){
        return (valor == valorA)? valorB : valorA;
    }

    public Ficha crearInvertida(){
        return new Ficha(this.valorB, this.valorA);
    }

    @Override
    public String toString() {
        return "[" + this.valorA + "|" + this.valorB + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ficha ficha = (Ficha) o;

        boolean ladoNormal = (this.getValorA() == ficha.getValorA() && this.getValorB() == ficha.getValorB());
        boolean ladoInvertido = (this.getValorA() == ficha.getValorB() && this.getValorB() == ficha.getValorA());

        return ladoNormal || ladoInvertido;
    }

    @Override
    public int hashCode() {
        return getValorA() + getValorB();
    }
}
