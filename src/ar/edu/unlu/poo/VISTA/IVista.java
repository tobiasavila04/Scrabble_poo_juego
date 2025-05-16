package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.MODELO.Celda;
import ar.edu.unlu.poo.MODELO.Ficha;
import ar.edu.unlu.poo.MODELO.Jugador;
import ar.edu.unlu.poo.MODELO.PosicionCelda;

import java.io.PipedOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IVista {
    void mostrarMensaje(String s);

    void mostrarTablero(Celda[][] celdas);

    void mostrarAtril() throws RemoteException;

    void mostrarTurno() throws RemoteException;

    void iniciarVista(String nombre) throws RemoteException;

    String getJugadorLocal();

    void limpiar() throws RemoteException;

    void pedirFicha(PosicionCelda posicion) throws RemoteException;

    void mostrarPuntajes(ArrayList<Jugador> jugador);

    void mostrarFinDePartida(Jugador ganador, ArrayList<Jugador> jugadores);

    void deshabilitarInteraccion();
}
