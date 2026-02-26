package ar.edu.unlu.poo.Domino.Modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorRanking implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ARCHIVO_RANKING = "ranking.dat";
    private List<EntradaRanking> listaRanking;

    public GestorRanking(){
        this.listaRanking = cargarDesdeArchivo();
    }

    public void registrarPuntaje(String nombre, int puntos){
        listaRanking.add(new EntradaRanking(nombre, puntos));
        Collections.sort(listaRanking);

        if(listaRanking.size() > 5){
            listaRanking = new ArrayList<>(listaRanking.subList(0,5));
        }

        guardarEnArchivo();
    }

    public List<EntradaRanking> getTop5(){
        return new ArrayList<>(listaRanking);
    }

    public void guardarEnArchivo(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_RANKING))){
            oos.writeObject(listaRanking);
        } catch (IOException e) {
            System.err.println("Error al persistir el ranking: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<EntradaRanking> cargarDesdeArchivo(){
        File file = new File(ARCHIVO_RANKING);
        if(!file.exists()){
            return new ArrayList<>();
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO_RANKING))){
            return (List<EntradaRanking>) ois.readObject();
        } catch(Exception e){
            System.err.println("No se pudo cargar el ranking previo, iniciando uno nuevo.");
            return new ArrayList<>();
        }
    }
}
