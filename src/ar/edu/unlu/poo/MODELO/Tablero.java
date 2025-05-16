package ar.edu.unlu.poo.MODELO;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tablero implements Serializable {
    private Celda[][] celdas;
    private Diccionario diccionario;

    public Tablero(Diccionario diccionario) {
        this.diccionario = diccionario;
        celdas = new Celda[15][15];
        inicializarTablero();
    }

    public void inicializarTablero(){
        for(int i = 0; i < celdas.length; i++){
            for(int j = 0; j <  celdas.length; j++){
                PosicionCelda posicion = new PosicionCelda(i,j);
                BonificacionTablero bonificacion = obtenerBonificacion(posicion);
                celdas[i][j] = new Celda(posicion, bonificacion);
            }
        }
    }

    public BonificacionTablero obtenerBonificacion(PosicionCelda posicion){
        int n = 15;
        int minFila = Math.min(posicion.getPosX(), n - 1 - posicion.getPosX());
        int minColumna = Math.min(posicion.getPosY(), n - 1 - posicion.getPosY());
        if(minFila == 7 && minColumna == 7) return BonificacionTablero.CENTRO;
        if (minFila == 0 && minColumna == 0 || (minFila == 7 && minColumna == 0)){ //|| minColumna == 7))) {
            return BonificacionTablero.TRIPLE_PALABRA;

        }
        if ((minFila == 1 && minColumna == 5) || (minFila == 5 && minColumna == 1) || (minFila == 5 && minColumna == 5)) {
            return BonificacionTablero.TRIPLE_LETRA;
        }
        if ((minFila == 0 && (minColumna == 3 || minColumna == 11)) || (minFila == 2 && (minColumna == 6 || minColumna == 8)) ||
                (minFila == 3 && (minColumna == 0 || minColumna == 7)) || (minFila == 7 && minColumna == 3) || (minFila == 6 && (minColumna == 2 || minColumna == 6))) {
            return BonificacionTablero.DOBLE_LETRA;
        }
        if (minFila == minColumna || minFila + minColumna == 14) {
            return BonificacionTablero.DOBLE_PALABRA;
        }
        return BonificacionTablero.NORMAL;
    }

    public boolean agregarFicha(Ficha ficha, PosicionCelda posicion){
        Celda celda = getCelda(posicion);
        if(celda.getEstado() == EstadoCelda.LIBRE){
            celda.agregarFicha(ficha);
            return true;
        }
        return false;
    }

    public Celda[][] getCeldas(){
        return celdas;
    }


    public Celda getCelda(PosicionCelda posicion) {
        return celdas[posicion.getPosX()][posicion.getPosY()];
    }

    public Celda getCelda(int fila,int columna) {
        return celdas[fila][columna];
    }

    public void restaurarCelda(PosicionCelda posicion){
        Celda celda = getCelda(posicion);
        celda.sacarFicha();
    }

    public boolean esCeldaOcupada(int fila, int columna){
        return getCelda(fila, columna).getEstado() == EstadoCelda.OCUPADA;

    }

    public Direccion validarPosicion(ArrayList<PosicionCelda> posiciones) {
        if(posiciones.size() == 1){
            return Direccion.PUEDE_SER_AMBAS;
        }
        boolean vertical = true, horizontal = true;
        int BaseFila = posiciones.getFirst().getPosX();
        int BaseColumna = posiciones.getFirst().getPosY();

        for (PosicionCelda posicion : posiciones) {
            if (posicion.getPosX() != BaseFila) {
                horizontal = false;
            }
            if (posicion.getPosY() != BaseColumna) {
                vertical = false;
            }
        }
        if(vertical) return Direccion.VERTICAL;
        else if (horizontal) return Direccion.HORIZONTAL;
        else return Direccion.INVALIDA;
    }

    public ArrayList<PosicionCelda> obtenerPosiciones(ArrayList<PosicionCelda> posiciones, Direccion direccion) {
        ArrayList<PosicionCelda> posicionesPalabra = new ArrayList<>();
        int posFila = posiciones.getFirst().getPosX();
        int posColumna = posiciones.getFirst().getPosY();
        if(direccion == Direccion.VERTICAL) {
            PosicionCelda posicionInicio = obtenerInicio(posFila, posColumna, -1, 0);
            int fila = posicionInicio.getPosX();
            int columna = posicionInicio.getPosY();
            while (fila < 15  && esCeldaOcupada(fila, columna)) {
                posicionesPalabra.add(new PosicionCelda(fila++, columna));
            }
        } else if (direccion == Direccion.HORIZONTAL) {
            PosicionCelda posicionInicio = obtenerInicio(posFila, posColumna, 0, -1);
            int fila = posicionInicio.getPosX();
            int columna = posicionInicio.getPosY();
            while (columna < 15  && esCeldaOcupada(fila,columna)) {
                posicionesPalabra.add(new PosicionCelda(fila, columna++));
            }

        }
        return posicionesPalabra;
    }


    public boolean validarAdyacentes(ArrayList<PosicionCelda> posiciones, Direccion direccion){
        boolean tieneAdyacente = false;
        for(PosicionCelda posicion : posiciones) {
            if (direccion == Direccion.HORIZONTAL || direccion == Direccion.PUEDE_SER_AMBAS){
                int fila = posicion.getPosX();
                int columna = posicion.getPosY();
                StringBuilder palabra = new StringBuilder();
                while(fila > 0 && getCelda(fila-1,columna).getEstado() == EstadoCelda.OCUPADA){
                    fila--;
                }
                while(fila < 15 && getCelda(fila,columna).getEstado() == EstadoCelda.OCUPADA){
                    Ficha ficha = getCelda(fila,columna).getFicha();
                    palabra.append(ficha.getLetra());
                    fila++;
                }
                System.out.println("palab h " + palabra);
                if(palabra.length() > 1){
                    tieneAdyacente = true;
                    if(!diccionario.esPalabraValida(palabra.toString())){
                        return false;
                    }
                }
            }
            if(direccion == Direccion.VERTICAL || direccion == Direccion.PUEDE_SER_AMBAS) {
                int fila = posicion.getPosX();
                int columna = posicion.getPosY();
                StringBuilder palabra = new StringBuilder();
                while (columna > 0 && getCelda(fila, columna-1).getEstado() == EstadoCelda.OCUPADA) {
                    columna--;
                }
                while (columna < 15 && getCelda(fila, columna).getEstado() == EstadoCelda.OCUPADA) {
                    Ficha ficha = getCelda(fila, columna).getFicha();
                    palabra.append(ficha.getLetra());
                    columna++;
                }
                System.out.println("paab v " + palabra);
                if(palabra.length() > 1){
                    tieneAdyacente = true;
                    if(!diccionario.esPalabraValida(palabra.toString())) {
                        return false;
                    }
                }

            }
        }
        return tieneAdyacente;
    }

    private PosicionCelda obtenerInicio(int fila, int columna, int vertical, int horizontal) {
        while (fila + vertical >= 0  && fila + vertical < 15 && columna + horizontal >= 0 && columna + horizontal < 15 && esCeldaOcupada(fila + vertical, columna + horizontal)) {
            fila += vertical;
            columna += horizontal;
        }
        return new PosicionCelda(fila,columna);
    }

    public boolean validarPalabra(ArrayList<PosicionCelda> posicionesPalabra) throws IOException {
        Palabra palabra = new Palabra(this, diccionario);
        return palabra.esPalabraValida(posicionesPalabra);
    }

    public int calcularPuntosPalabra(ArrayList<PosicionCelda> posFichasColocadas, ArrayList<PosicionCelda> posPalabraCompleta, Direccion direccion) throws IOException {
        Palabra palabra = new Palabra(this, diccionario);
        return palabra.calcularPuntos(posFichasColocadas, posPalabraCompleta,direccion);
    }

}


