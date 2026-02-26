package ar.edu.unlu.poo.Domino.Observer;

import ar.edu.unlu.poo.Domino.Modelo.Eventos;

public interface Observable {
    void agregarObservador(Observador observador);
    void quitarObservador(Observador observador);
    void notificarObservadores(Eventos evento, Object objeto);
}
