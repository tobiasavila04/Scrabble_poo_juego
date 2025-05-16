package ar.edu.unlu.poo.MODELO;

import java.io.Serializable;
import java.util.ArrayList;

public class Jugador implements Serializable {
    private String nombre;
    private Atril atril;
    private int puntos;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.atril = new Atril();
        this.puntos = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public void sumarPuntos(int puntos) {
        this.puntos += puntos;
    }
    public void restarPuntos(int puntos){
        this.puntos -= puntos;
    }

    public Atril getAtril() {
        return this.atril;
    }
}
