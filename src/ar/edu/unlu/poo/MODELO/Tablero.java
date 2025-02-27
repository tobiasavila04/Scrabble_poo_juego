package ar.edu.unlu.poo.MODELO;

import java.io.Serializable;
import java.util.ArrayList;

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
                celdas[i][j] = new Celda(posicion);
            }
        }
    }

    public boolean agregarFicha(Ficha ficha, PosicionCelda posicion){
        Celda celda = getCelda(posicion);
        if(celda.getEstado() == EstadoCelda.LIBRE){
            System.out.println("hahaj");
            celda.agregarFicha(ficha);
            return true;
        }
        return false;
    }

    private Ficha obtenerFichaPosicion(PosicionCelda posicion){
        Celda celda = celdas[posicion.getPosicionX()][posicion.getPosicionY()];
        return celda.getFicha();
    }

    public Celda[][] getCeldas(){
        return celdas;
    }


    public Celda getCelda(PosicionCelda posicion) {
        return celdas[posicion.getPosicionX()][posicion.getPosicionY()];
    }

    public void restaurarCelda(PosicionCelda posicion){
        Celda celda = getCelda(posicion);
        celda.sacarFicha();
    }

    public Direccion validarPosicion(ArrayList<PosicionCelda> posiciones) {
        if(posiciones.size() == 1){
            return Direccion.PUEDE_SER_AMBAS;
        }
        boolean vertical = true, horizontal = true;
        int BaseFila = posiciones.getFirst().getPosicionX();
        int BaseColumna = posiciones.getFirst().getPosicionY();

        for (PosicionCelda posicion : posiciones) {
            if (posicion.getPosicionX() != BaseFila) {
                horizontal = false;
            }
            if (posicion.getPosicionY() != BaseColumna) {
                vertical = false;
            }
        }
        if(vertical) return Direccion.VERTICAL;
        else if (horizontal) return Direccion.HORIZONTAL;
        else return Direccion.INVALIDA;
    }

    public ArrayList<PosicionCelda> obtenerPosiciones(ArrayList<PosicionCelda> posiciones, Direccion direccion) {
        ArrayList<PosicionCelda> posicionesPalabra = new ArrayList<>();
        int posFila = posiciones.getFirst().getPosicionX();
        int posColumna = posiciones.getFirst().getPosicionY();
        if(direccion == Direccion.VERTICAL) {
            while (posFila >= 0 && getCelda(new PosicionCelda(posFila-1, posColumna)).getEstado() == EstadoCelda.OCUPADA) {
                posFila--;
            }
            while (posFila < 15  && getCelda(new PosicionCelda(posFila, posColumna)).getEstado() == EstadoCelda.OCUPADA) {
                //Ficha ficha = getCelda(new PosicionCelda(posFila, posColumna)).getFicha();
                posicionesPalabra.add(new PosicionCelda(posFila, posColumna));
                posFila++;
            }
        } else if (direccion == Direccion.HORIZONTAL) {
            while (posColumna >= 0 && getCelda(new PosicionCelda(posFila, posColumna-1)).getEstado() == EstadoCelda.OCUPADA) {
                posColumna--;
            }
            while (posColumna < 15  && getCelda(new PosicionCelda(posFila, posColumna)).getEstado() == EstadoCelda.OCUPADA) {
               // Ficha ficha = getCelda(new PosicionCelda(posFila, posColumna)).getFicha();
                posicionesPalabra.add(new PosicionCelda(posFila, posColumna));
                posColumna++;
            }

        }
        return posicionesPalabra;
    }


    public boolean validarAdyacentes(ArrayList<PosicionCelda> posiciones, Direccion direccion){
        boolean tieneAdyacente = false;
        for(PosicionCelda posicion : posiciones) {
            if (direccion == Direccion.HORIZONTAL || direccion == Direccion.PUEDE_SER_AMBAS){
                int fila = posicion.getPosicionX();
                int columna = posicion.getPosicionY();
                StringBuilder palabra = new StringBuilder();
                while(fila > 0 && getCelda(new PosicionCelda(fila-1,columna)).getEstado() == EstadoCelda.OCUPADA){
                    fila--;
                }
                while(fila < 15 && getCelda(new PosicionCelda(fila,columna)).getEstado() == EstadoCelda.OCUPADA){
                    Ficha ficha = getCelda(new PosicionCelda(fila,columna)).getFicha();
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
                int fila = posicion.getPosicionX();
                int columna = posicion.getPosicionY();
                StringBuilder palabra = new StringBuilder();
                while (columna > 0 && getCelda(new PosicionCelda(fila, columna-1)).getEstado() == EstadoCelda.OCUPADA) {
                    columna--;
                }
                while (columna < 15 && getCelda(new PosicionCelda(fila, columna)).getEstado() == EstadoCelda.OCUPADA) {
                    Ficha ficha = getCelda(new PosicionCelda(fila, columna)).getFicha();
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
}


