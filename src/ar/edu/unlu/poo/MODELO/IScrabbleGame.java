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

    boolean partidaIniciada() throws RemoteException;

    Jugador obtenerJugadorPartidaYaIniciada(String nombreJugador) throws RemoteException;

    void inicializarPrimerTurno() throws RemoteException;

    Celda[][] getTablero() throws RemoteException;

    boolean esCeldaLibreYValida(PosicionCelda posicion) throws RemoteException;

    void colocarFichaEnCelda(Ficha fichaSeleccioanda, PosicionCelda posicion) throws RemoteException;

    boolean verificarNombreJugador(String nombre) throws RemoteException;

    Jugador getJugadorActual() throws RemoteException;

    Jugador obtenerJugador(String nombreJugador) throws RemoteException;

    ArrayList<Jugador> obtenerJugadores() throws RemoteException;

    Ficha obtenerFichaDelAtril(char letraFicha) throws RemoteException;

    void colocarFichaComodinEnCelda(Ficha ficha, PosicionCelda posicion, char letraComodin) throws RemoteException;
    void pasarTurno() throws RemoteException;

    void siguienteTurno() throws RemoteException;

    boolean cambiarFichas(ArrayList<Ficha> fichasCambiar) throws RemoteException;

    void formarYvalidarPalabra(ArrayList<PosicionCelda> posiciones) throws IOException;

    ArrayList<Ficha> obtenerFichasAtril(Jugador jugador) throws RemoteException;

    void restaurarEstadoJuego() throws RemoteException;

    void rellenarAtril() throws RemoteException;

    int puntosJugador() throws RemoteException;

    void guardarPartida() throws RemoteException;

    boolean cargarPartida(int IDPartida, String nombreJugador) throws RemoteException;

    ArrayList<Partida> getPartidasGuardadas() throws RemoteException;

    ArrayList<Jugador> obtenerTop5Jugadores() throws RemoteException;

    Jugador obtenerJugadorGanador()throws RemoteException;

    void obtenerGanador()throws RemoteException;

    boolean esComodin(Ficha ficha) throws RemoteException;

    ArrayList<Ficha> obtenerFichas() throws RemoteException;

    Jugador agregarJugador(String nombre) throws RemoteException;
}
