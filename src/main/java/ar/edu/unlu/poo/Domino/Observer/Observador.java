package ar.edu.unlu.poo.Domino.Observer;

import ar.edu.unlu.poo.Domino.Modelo.Eventos;

public interface Observador {
    void actualizar(Eventos evento, Object objeto);
}
