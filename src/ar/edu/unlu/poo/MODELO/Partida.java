package ar.edu.unlu.poo.MODELO;

import java.io.Serializable;
import java.util.ArrayList;

public class Partida implements Serializable {
    private Tablero tablero;
    private Bolsa bolsa;
    private ArrayList<Jugador> jugadores;
    private String turnoActual, nombreHost;
    private ScrabbleGame ScrabbleGame;
    private int id;

    public Partida(Tablero tablero, Bolsa bolsa, String turnoActual, ArrayList<Jugador> jugadores, int ID, ScrabbleGame scrabbleGame, String nombreHost){
        this.tablero = tablero;
        this.bolsa = bolsa;
        this.turnoActual = turnoActual;
        this.jugadores = jugadores;
        this.id = ID;
        this.ScrabbleGame = scrabbleGame;
        this.nombreHost = nombreHost;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public Bolsa getBolsa() {
        return bolsa;
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    public String getTurnoActual() {
        return turnoActual;
    }

    public String getNombreHost(){ return nombreHost; }

    public ScrabbleGame getScrabbleGame() {
        return ScrabbleGame;
    }

    public int getId() {
        return id;
    }

    public boolean contieneJugador(String nombreJugador){
        for(Jugador jugador : jugadores){
            if(jugador.getNombre().equals(nombreJugador)){
                return true;
            }
        }
        return false;
    }
}
