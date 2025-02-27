package ar.edu.unlu.poo.MODELO;

import java.io.Serializable;

public class Ficha implements Serializable {
    private char letra;
    private int puntos;

    public Ficha(char letra, int puntos){
        this.letra = letra;
        this.puntos = puntos;
    }



    public char getLetra(){
        return letra;
    }

    public int getPuntos(){
        return puntos;
    }
}
