package ar.edu.unlu.poo.Domino.Modelo;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Mesa implements Serializable {
    private static final long serialVersionUID = 1L;
    private LinkedList<Ficha> fichasEnMesa;

    public Mesa(){
        this.fichasEnMesa  = new LinkedList<>();
    }

    public boolean agregarFichaPorIzquierda(Ficha f){
        if(estaVacia()){
            return false;
        }

        int valorAConectar = getExtremoIzquierdo();

        if(f.getValorB() == valorAConectar){
            fichasEnMesa.addFirst(f);
            return true;
        } else if(f.getValorA() == valorAConectar){
            fichasEnMesa.addFirst(f.crearInvertida());
            return true;
        }
        return false;
    }

    public boolean agregarFichaPorDerecha(Ficha f){
        if(estaVacia()){
            return false;
        }

        int valorAConectar = getExtremoDerecho();

        if(f.getValorA() == valorAConectar){
            fichasEnMesa.addLast(f);
            return true;
        } else if(f.getValorB() == valorAConectar){
            fichasEnMesa.addLast(f.crearInvertida());
            return true;
        }
        return false;
    }

    public int[] getExtremos(){
        return new int[]{getExtremoIzquierdo(), getExtremoDerecho()};
    }

    public List<Ficha> getFichasEnMesa(){
        return Collections.unmodifiableList(this.fichasEnMesa);
    }

    public int getExtremoIzquierdo(){
        if(fichasEnMesa.isEmpty()){
            return -1;
        }
        return fichasEnMesa.getFirst().getValorA();
    }

    public int getExtremoDerecho() {
        if(fichasEnMesa.isEmpty()){
            return -1;
        }
        return fichasEnMesa.getLast().getValorB();
    }

    public void agregarPrimeraFicha(Ficha f){
        if(estaVacia()){
            this.fichasEnMesa.add(f);
        }
    }

    public boolean estaVacia(){
        return this.fichasEnMesa.isEmpty();
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        if(estaVacia()){
            sb.append("MESA (Vacía)\n");
        } else{
            sb.append("MESA (Extremos: ");
            sb.append("[").append(getExtremoIzquierdo()).append("] ... ");
            sb.append("[").append(getExtremoDerecho()).append("])\n");
        }

        sb.append("     Fichas jugadas: ");

        if(estaVacia()){
            sb.append("(Vacía)");
        } else{
            for(Ficha f: this.fichasEnMesa){
                sb.append(f.toString()).append(" ");
            }
        }

        return sb.toString();
    }
}
