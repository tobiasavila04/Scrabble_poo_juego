package ar.edu.unlu.poo.MODELO;

import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface IScrabbleGame extends IObservableRemoto {
    void inicializarJuego() throws RemoteException;

    void repartirFichas() throws RemoteException;

    void conectarJugador(Jugador jugador) throws RemoteException;

    void inicializarPrimerTurno() throws RemoteException;

    Celda[][] getTablero() throws RemoteException;

    boolean esCeldaLibreYValida(PosicionCelda posicion) throws RemoteException;

    void colocarFichaEnCelda(Ficha fichaSeleccioanda, PosicionCelda posicion) throws RemoteException;

    boolean verificarNombreJugador(String nombre) throws RemoteException;

    Jugador getJugadorActual() throws RemoteException;

    Jugador obtenerJugador(String nombreJugador) throws RemoteException;

    Ficha obtenerFichaDelAtril(char letraFicha) throws RemoteException;

    void pasarTurno() throws RemoteException;

    void cambiarFichas(ArrayList<Ficha> fichasCambiar) throws RemoteException;

    void formarYvalidarPalabra(ArrayList<PosicionCelda> posiciones) throws IOException;

    ArrayList<Ficha> obtenerFichasAtril(Jugador jugador) throws RemoteException;

    void restaurarEstadoJuego() throws RemoteException;

    void rellenarAtril() throws RemoteException;

    int puntosJugador() throws RemoteException;

    void guardarPartida() throws RemoteException;

    boolean cargarPartida(int IDPartida, String nombreJugador) throws RemoteException;

    List<Partida> getPartidasGuardadas() throws RemoteException;

    List<Jugador> obtenerTop5Jugadores() throws RemoteException;

    Jugador obtenerGanador()throws RemoteException;


}
