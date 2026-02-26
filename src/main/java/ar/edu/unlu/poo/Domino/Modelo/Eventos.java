package ar.edu.unlu.poo.Domino.Modelo;

import java.io.Serializable;

public enum Eventos implements Serializable {
    JUGADOR_CONECTADO,
    INICIO_PARTIDA,
    JUGADOR_ROBO_FICHA,
    JUGADOR_PASO_TURNO,
    CAMBIO_DE_TURNO,
    FIN_DE_RONDA,
    FIN_DE_PARTIDA,
    MESA_CERRADA
}
