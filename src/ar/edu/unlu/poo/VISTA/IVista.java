package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.MODELO.Celda;
import ar.edu.unlu.poo.MODELO.Ficha;
import ar.edu.unlu.poo.MODELO.Jugador;

import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IVista {
    void mostrarMensaje(String s);

    void actualizarAtril(Jugador jugador) throws RemoteException;

    void mostrarTablero(Celda[][] celdas);

    void mostrarAtril() throws RemoteException;

    //void setJugadorLocal(Jugador jugador);
    void mostrarTurno() throws RemoteException;

    void iniciarVista() throws RemoteException;

    String getJugadorLocal();

    //void actualizarVista() throws RemoteException;

    void limpiar();
}
