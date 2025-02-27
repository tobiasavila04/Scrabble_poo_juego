package ar.edu.unlu.poo.MODELO;

import java.io.Serializable;

public class PosicionCelda implements Serializable {
    private int posicionX;
    private int posicionY;

    public PosicionCelda(int posX, int posY){
        this.posicionX = posX;
        this.posicionY = posY;
    }

    public int getPosicionX() {
        return posicionX;
    }

    public int getPosicionY() {
        return posicionY;
    }
}
