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

    public ScrabbleControlador(){}

    public void setVista(IVista vista){
        this.vista = vista;
    }

    public void iniciarJuego() throws RemoteException {
       // juego.inicializarJuego();
        vista.mostrarTablero(juego.getTablero());
        vista.mostrarAtril();
    }

    public void agregarJugador(String nombre) throws RemoteException {
        jugadorID = nombre;
        Jugador jugador = new Jugador(nombre);
        juego.conectarJugador(jugador);
    }

    public Celda[][] obtenerCeldasTablero() throws RemoteException {
        return juego.getTablero();
    }

    public boolean realizarAccionSiEsTurno() throws RemoteException{
        Jugador turnoActual = juego.getJugadorActual();
        return turnoActual.getNombre().equals(vista.getJugadorLocal());
    }

    public boolean celdaValida(PosicionCelda posicion) throws RemoteException {
        return juego.esCeldaLibreYValida(posicion);

    }

    public void colocarFichaSeleccionadaEnTablero(Ficha fichaSeleccioanda, PosicionCelda posicion) throws RemoteException {
        if(realizarAccionSiEsTurno()) {
            if (juego.esCeldaLibreYValida(posicion)) {
                juego.colocarFichaEnCelda(fichaSeleccioanda, posicion);
               // actualizarVista();
            }
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
        Jugador jugador = juego.obtenerJugador(jugadorLocal);
        return juego.obtenerFichasAtril(jugador);
    }

    public void pasarTurno() throws RemoteException {
        juego.pasarTurno();
    }

    public void cambiarFichas(ArrayList<Ficha> fichasCambiar) throws RemoteException {
        if(fichasCambiar.isEmpty()){
            vista.mostrarMensaje("no se seleccionaron fichas! no hubo cambios!");
        }else {
            juego.cambiarFichas(fichasCambiar);
        }
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

   /* public void mostrarTurno() throws RemoteException {
        Jugador turnoActual = juego.getJugadorActual();
        String jugadorLocal = vista.getJugadorLocal();
        if(turnoActual.getNombre().equals(jugadorLocal)){
            vista.mostrarMensaje("es tu turno " + turnoActual.getNombre());
        }else{
            vista.mostrarMensaje("es el turno de " + turnoActual.getNombre());
        }
    }*/

    private void restaurarJuego() throws RemoteException {
        if(realizarAccionSiEsTurno()){
            juego.restaurarEstadoJuego();
        }
        vista.mostrarAtril();
        vista.limpiar();
        vista.mostrarTablero(juego.getTablero());
    }
    private void actualizarJugador() throws RemoteException {
        if(realizarAccionSiEsTurno()){
            int puntos = juego.puntosJugador();
            vista.mostrarMensaje("puntos obtenidos: " + puntos);

        }
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException {
        this.juego = (IScrabbleGame) modeloRemoto;
        //this.juego.agregarObservador(this);
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
               }
               case JUGADOR_CONECTADO -> vista.mostrarMensaje("esperando a que se conecte tu opnente..");
               case PASO_TURNO -> vista.mostrarTurno();
               case CAMBIOS_PARTIDA -> {
                   vista.mostrarTablero(juego.getTablero());
                   //vista.mostrarAtril();
               }
               case ERROR_CAMBIO -> {
                   vista.mostrarMensaje("no se ha podido cambiar las fichas, recuerde debe haber jugado por primera vez y maximo dos veces!");
                   vista.actualizarAtril(juego.getJugadorActual());
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
                   //actualizarJugador();
                   vista.limpiar();
               }
               case FIN_PARTIDA -> vista.mostrarMensaje("fin partida!");
           }
       }
    }
}
