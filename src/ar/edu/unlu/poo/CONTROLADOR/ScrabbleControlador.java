package ar.edu.unlu.poo.CONTROLADOR;

import ar.edu.unlu.poo.MODELO.*;
import ar.edu.unlu.poo.VISTA.IVista;
import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ScrabbleControlador implements IControladorRemoto {
    IVista vista;
    IScrabbleGame juego;
    private String jugadorID;

    public ScrabbleControlador()  {
    }
    
    public ScrabbleControlador(ScrabbleGame modelo){
        this.juego = modelo;
    }

    public void setVista(IVista vista){
        this.vista = vista;
    }

    public void iniciarJuego() throws RemoteException {
        vista.mostrarTablero(juego.getTablero());
        vista.mostrarAtril();
        vista.mostrarPuntajes(juego.obtenerJugadores());
    }

    public void agregarJugador(String nombre) throws RemoteException {
        jugadorID = nombre;
        Jugador jugador = juego.agregarJugador(nombre);
        juego.conectarJugador(jugador);
    }

    public boolean perteneceJugadorAPartida(String nombre) throws RemoteException {
        return juego.obtenerJugadorPartidaYaIniciada(nombre) != null;
    }

    public boolean partidaYaIniciada() throws RemoteException {
        return juego.partidaIniciada();
    }

    public boolean realizarAccionSiEsTurno() throws RemoteException{
        Jugador turnoActual = juego.getJugadorActual();
        return turnoActual.getNombre().equals(vista.getJugadorLocal());
    }

    public boolean celdaValida(PosicionCelda posicion) throws RemoteException {
        return juego.esCeldaLibreYValida(posicion);
    }

    public void colocarFichaSeleccionadaEnTablero(Ficha fichaSeleccionada, PosicionCelda posicion) throws RemoteException {
        if(realizarAccionSiEsTurno()) {
            if (juego.esCeldaLibreYValida(posicion)) {
                if(juego.esComodin(fichaSeleccionada)){
                    vista.pedirFicha(posicion);
                }else {
                    juego.colocarFichaEnCelda(fichaSeleccionada, posicion);
                }
            }
        }
    }

    public void colocarFichaComodinEnTablero(Ficha fichaSeleccionada, char letraComodin, PosicionCelda posicion) throws RemoteException {
        if (juego.esCeldaLibreYValida(posicion)) {
            juego.colocarFichaComodinEnCelda(fichaSeleccionada, posicion, letraComodin);
        }
    }

    public boolean existeNombre(String nombre) throws RemoteException {
        return juego.verificarNombreJugador(nombre);
    }

    public String obtenerTurnoActual() throws RemoteException {
        return juego.getJugadorActual().getNombre();
    }

    public Ficha obtenerFicha(char letraFicha) throws RemoteException {
        return juego.obtenerFichaDelAtril(letraFicha);
    }
    public ArrayList<Ficha> obtenerFichasAtril(String jugadorLocal) throws RemoteException {
        return juego.obtenerFichasAtril(juego.obtenerJugador(jugadorLocal));
    }

    public ArrayList<Ficha> obtenerFichas() throws RemoteException {
        return juego.obtenerFichas();
    }

    public void pasarTurno() throws RemoteException {
        juego.pasarTurno();
    }

    public boolean cambiarFichas(ArrayList<Ficha> fichasCambiar) throws RemoteException {
        return juego.cambiarFichas(fichasCambiar);
    }

    public void enviarPalabra(ArrayList<PosicionCelda> posiciones) throws IOException {
        juego.formarYvalidarPalabra(posiciones);
    }

    public ArrayList<Ficha> obtenerAtrilJugador() throws RemoteException {
        Jugador jugador = juego.getJugadorActual();
        return jugador.getAtril().getFichasAtril();
    }

    public void guardarPartida() throws RemoteException {
        juego.guardarPartida();
    }

    public void cargarPartida(String nombre, int ID) throws RemoteException {
        juego.cargarPartida(ID,nombre);
    }

    public ArrayList<Jugador> obtenerRanking() throws RemoteException {
        return juego.obtenerTop5Jugadores();
    }

    private void restaurarJuego() throws RemoteException {
        if(realizarAccionSiEsTurno()){
            juego.restaurarEstadoJuego();
            vista.mostrarAtril();
        }
        vista.limpiar();
        vista.mostrarTablero(juego.getTablero());
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException {
        this.juego = (IScrabbleGame) modeloRemoto;
    }

    @Override
    public void actualizar(IObservableRemoto instanciaModelo, Object cambio) throws RemoteException{
       if(cambio instanceof Evento){
           switch (((Evento)cambio)){
               case INICIAR_PARTIDA ->{
                   iniciarJuego();
                   vista.mostrarMensaje("la partida ha comenzado!");
                   vista.mostrarTurno();
               }
               case FICHAS_CAMBIADAS -> {
                   vista.mostrarAtril();
                   vista.mostrarMensaje("fichas cambiadas!");
                   vista.limpiar();
               }
               case JUGADOR_CONECTADO -> vista.mostrarMensaje("esperando a que se conecte tu opnente..");
               case PASO_TURNO -> vista.mostrarTurno();
               case CAMBIOS_PARTIDA -> {
                   vista.mostrarAtril();
                   vista.mostrarTablero(juego.getTablero());
               }
               case ERROR_CAMBIO -> {
                   vista.mostrarMensaje("no se ha podido cambiar las fichas, recuerde debe haber jugado por primera vez y maximo dos veces!");
                   vista.limpiar();
               }
               case ERROR_POSICIONES -> {
                   vista.mostrarMensaje("las posiciones de las fichas no son consecutivas!");
                   restaurarJuego();
               }
               case ERROR_DICCIONARIO, ERROR_ADYACENTES ->{
                   vista.mostrarMensaje("palabra invalida!");
                   restaurarJuego();
               }
               case ERROR_CENTRO -> {
                   vista.mostrarMensaje("la primera palabra se debe colocar en el centro!");
                   restaurarJuego();
               }
               case ERROR_FICHAS_ATRIL -> {
                   vista.mostrarMensaje("error cambio, las fichas deben estar en el atril!");
                   vista.limpiar();
               }
               case PALABRA_AGREGADA -> {
                   vista.mostrarMensaje("palabra exitosa!");
                   vista.mostrarAtril();
                   vista.mostrarPuntajes(juego.obtenerJugadores());
                   vista.limpiar();
               }
               case PARTIDA_GUARDADA -> {
                   vista.mostrarMensaje("ya no se puede seguir jugando esta partida, la partida ha sido guardada");
                   vista.deshabilitarInteraccion();
               }
               case FIN_PARTIDA -> {
                   vista.mostrarMensaje("fin partida!");
                   Jugador ganador = juego.obtenerJugadorGanador();
                   vista.mostrarFinDePartida(ganador, juego.obtenerJugadores());
               }
           }
       }
    }
}