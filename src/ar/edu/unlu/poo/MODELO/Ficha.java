package ar.edu.unlu.poo.MODELO;

import java.io.Serializable;
import java.util.Objects;

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

    public boolean esComodin(){
        return letra == '_';
    }

    public int getPuntos(){
        return puntos;
    }

    public void asignarLetraComodin(char letra) {
        if (esComodin()) {
            this.letra = letra;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ficha ficha)) return false;
        return letra == ficha.letra && puntos == ficha.puntos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(letra, puntos);
    }

}
