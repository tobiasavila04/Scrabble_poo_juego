package ar.edu.unlu.poo.MODELO;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Palabra implements Serializable {
    private Tablero tablero;
    private Diccionario diccionario;

    public Palabra(Tablero tablero, Diccionario diccionario) throws IOException {
        this.tablero = tablero;
        this.diccionario = diccionario;
    }


    public boolean esPalabraValida(ArrayList<PosicionCelda> posPalabra) {
        StringBuilder palabra = new StringBuilder();
        for(PosicionCelda posicion : posPalabra) {
            Ficha ficha = tablero.getCelda(posicion).getFicha();
            palabra.append(ficha.getLetra());
        }
        System.out.println("palabra:: " + palabra.toString());
        return diccionario.esPalabraValida(palabra.toString());
    }

    public int calcularPuntos(ArrayList<PosicionCelda> posFichasColocadas,ArrayList<PosicionCelda> posPalabraCompleta, Direccion direccion){
        int puntos = 0, puntosFicha = 0, multiplicadorPalabra = 1;
        for(PosicionCelda pos : posPalabraCompleta){
            Celda celda = tablero.getCelda(pos);
            Ficha ficha = celda.getFicha();
            puntosFicha = ficha.getPuntos() * celda.getBonificacionLetra();
            puntos += puntosFicha;
            multiplicadorPalabra *= celda.getBonificacionPalabra();
        }
        int puntosAdyacentes = calcularPuntosAdyacentes(posPalabraCompleta, direccion);
        int puntosTotal = (puntos * multiplicadorPalabra) + puntosAdyacentes;
        if(posFichasColocadas.size() == 7) puntosTotal += 50;
        return puntosTotal;
    }

    private int calcularPuntosAdyacentes(ArrayList<PosicionCelda> posiciones, Direccion direccion) {
        int puntos = 0;
        for(PosicionCelda posicion : posiciones){
            int fila = posicion.getPosX();
            int columna = posicion.getPosY();
            if(direccion == Direccion.HORIZONTAL || direccion == Direccion.PUEDE_SER_AMBAS){
                int puntosPalabra = 0;
                int multiplicador = 1;
                StringBuilder palabra = new StringBuilder();
                while(fila > 0 && tablero.getCelda(fila-1, columna).getEstado() == EstadoCelda.OCUPADA){
                    fila--;
                }
                while(fila < 15 && tablero.getCelda(fila,columna).getEstado() == EstadoCelda.OCUPADA){
                    Celda celda = tablero.getCelda(new PosicionCelda(fila,columna));
                    Ficha ficha = celda.getFicha();
                    palabra.append(ficha.getLetra());
                    puntosPalabra += ficha.getPuntos() * celda.getBonificacionLetra();
                    multiplicador *= celda.getBonificacionPalabra();
                    fila++;
                }
                if(palabra.length() > 1){
                    puntos += puntosPalabra * multiplicador;
                }
            }
            if(direccion == Direccion.VERTICAL || direccion == Direccion.PUEDE_SER_AMBAS){
                int puntosPalabra = 0;
                int multiplicador = 1;
                StringBuilder palabra = new StringBuilder();
                while(columna > 0 && tablero.getCelda(new PosicionCelda(fila,columna-1)).getEstado() == EstadoCelda.OCUPADA){
                    columna--;
                }
                while(columna < 15 && tablero.getCelda(fila,columna).getEstado() == EstadoCelda.OCUPADA){
                    Celda celda = tablero.getCelda(fila,columna);
                    Ficha ficha = celda.getFicha();
                    palabra.append(ficha.getLetra());
                    puntosPalabra += ficha.getPuntos() * celda.getBonificacionLetra();
                    multiplicador *=  celda.getBonificacionPalabra();
                    columna++;
                }
                if(palabra.length() > 1){
                    puntos += puntosPalabra * multiplicador;
                }
            }
        }
        return puntos;
    }
}
