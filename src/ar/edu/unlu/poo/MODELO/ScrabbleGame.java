package ar.edu.unlu.poo.MODELO;

import ar.edu.unlu.poo.GeneratorID;
import ar.edu.unlu.poo.Serializador;
import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class ScrabbleGame extends ObservableRemoto implements IScrabbleGame, Serializable {
    private Tablero tablero;
    private Bolsa bolsa;
    private final Diccionario diccionario;
    private String turnoActual, nombreHost;
    private ArrayList<Jugador> jugadores, jugadoresRegistrados = null;
    private Map<Integer,Partida> partidasGuardadas;
    private int cantidadJugadores, id;
    public static ScrabbleGame instancia;
    private ArrayList<PosicionCelda> posicionesFichas;
    private Map<Jugador, Integer> turnosPasadosJugador, intercambiosPorJugador;
    private Ranking ranking;

    public ScrabbleGame() throws IOException {
        this.jugadores = new ArrayList<>();
        this.posicionesFichas = new ArrayList<>();
        this.bolsa = new Bolsa();
        this.diccionario = new Diccionario();
        this.tablero = new Tablero(diccionario);
        this.nombreHost = null;
        turnosPasadosJugador = new HashMap<>();
        intercambiosPorJugador = new HashMap<>();
        this.jugadoresRegistrados = Serializador.cargarJugadorHistorico();

        if((this.partidasGuardadas == null)){
            this.partidasGuardadas = new HashMap<>();
        }else {
            this.partidasGuardadas = Serializador.cargarPartidaGuardada();
        }
        this.ranking = Ranking.getInstance();
    }

    public static ScrabbleGame getInstancia() throws IOException {
        if(instancia == null){
            instancia = new ScrabbleGame();
        }
        return instancia;
    }

    public void inicializarJuego() throws RemoteException{
        id = GeneratorID.generarID();
        repartirFichas();
        inicializarPrimerTurno();
    }

    public void repartirFichas() throws RemoteException {
        for (Jugador jugador : jugadores) {
            jugador.getAtril().generarAtril(bolsa);
        }
    }

    public void conectarJugador(Jugador jugador) throws RemoteException {
        if (!jugadoresRegistrados.contains(jugador)) {
            jugadoresRegistrados.add(jugador);
            Serializador.guardarJugadores(jugadoresRegistrados);
            if (nombreHost == null) {
                nombreHost = jugador.getNombre();
            }
        }
        if (!jugadores.contains(jugador)) {
            jugadores.add(jugador);
        }

        if (jugadores.size() == cantidadJugadores) {
            inicializarJuego();
            notificarObservadores(Evento.INICIAR_PARTIDA);
        } else {
            notificarObservadores(Evento.JUGADOR_CONECTADO);
        }
    }

    public Jugador agregarJugador(String nombre) throws RemoteException{
        for (Jugador jugador : jugadores) {
            if (jugador.getNombre().equals(nombre)) {
                return jugador;
            }
        }
        return new Jugador(nombre);
    }


    public boolean partidaIniciada() throws RemoteException{
        return nombreHost != null;
    }

    public void inicializarPrimerTurno() throws RemoteException{
        int fichaMayorValor = -1;
        for (Jugador jugador : jugadores) {
            Ficha ficha = bolsa.sacarFichaDeLaBolsa();
            if (ficha.getPuntos() > fichaMayorValor) {
                fichaMayorValor = ficha.getPuntos();
                turnoActual = jugador.getNombre();
            }
            bolsa.agregarFicha(ficha);
        }
    }

    public boolean esCeldaLibreYValida(PosicionCelda posicion) throws RemoteException{
        return !tablero.esCeldaOcupada(posicion.getPosX(), posicion.getPosY());
    }

    public void colocarFichaEnCelda(Ficha fichaSeleccioanda, PosicionCelda posicion) throws RemoteException{
        if(tablero.agregarFicha(fichaSeleccioanda,posicion)) {
            getJugadorActual().getAtril().sacarFichaDelAtril(fichaSeleccioanda);
            notificarObservadores(Evento.CAMBIOS_PARTIDA);
        }else{
            notificarObservadores(Evento.CELDA_OCUPADA);
        }
    }

    public void colocarFichaComodinEnCelda(Ficha ficha, PosicionCelda posicion, char letraComodin) throws RemoteException {
        getJugadorActual().getAtril().sacarFichaDelAtril(ficha);
        ficha.asignarLetraComodin(letraComodin);
        if(tablero.agregarFicha(ficha, posicion)) {
            notificarObservadores(Evento.CAMBIOS_PARTIDA);
        } else {
            notificarObservadores(Evento.CELDA_OCUPADA);
        }
    }

    public boolean esComodin(Ficha fichaSeleccionada) throws RemoteException{
        return fichaSeleccionada.esComodin();
    }

    public ArrayList<Ficha> obtenerFichas() throws RemoteException{
        return bolsa.obtenerLetrasDisponibles();
    }

    public void formarYvalidarPalabra(ArrayList<PosicionCelda> posiciones) throws IOException {
        Direccion direccion = tablero.validarPosicion(posiciones);
        System.out.println("direccion: " + direccion);
        ArrayList<PosicionCelda> posPalabra = new ArrayList<>(posiciones);
        this.posicionesFichas = new ArrayList<>(posiciones);
        if(direccion == Direccion.INVALIDA){
            notificarObservadores(Evento.ERROR_POSICIONES);
            return;
        }else if(direccion == Direccion.PUEDE_SER_AMBAS){
            if(!tablero.validarAdyacentes(posiciones,direccion)){
                notificarObservadores(Evento.ERROR_ADYACENTES);
                return;
            }
        }else{
            posPalabra = tablero.obtenerPosiciones(posiciones, direccion);
            if (!validarPrimerMovimiento()) {
                notificarObservadores(Evento.ERROR_CENTRO);
                return;
            }else if (!tablero.validarPalabra(posPalabra)) {
                notificarObservadores(Evento.ERROR_DICCIONARIO);
                return;
            }else if (!tablero.validarAdyacentes(posPalabra, direccion) && !esPrimerMovimiento()) {
                notificarObservadores(Evento.ERROR_ADYACENTES);
                return;
            }
        }
        turnosPasadosJugador.put(getJugadorActual(), 0);
        int puntos = tablero.calcularPuntosPalabra(posiciones,posPalabra, direccion);
        getJugadorActual().sumarPuntos(puntos);
        getJugadorActual().getAtril().rellenarAtril(bolsa);
        notificarObservadores(Evento.PALABRA_AGREGADA);
        siguienteTurno();
        if(getJugadorActual().getAtril().getFichasAtril().isEmpty() && bolsa.esVacia()){
            obtenerGanador();
            notificarObservadores(Evento.FIN_PARTIDA);
        }
    }

    private boolean validarDireccion(Direccion direccion, ArrayList<PosicionCelda> posiciones) throws RemoteException {
        if (direccion == Direccion.INVALIDA) {
            notificarObservadores(Evento.ERROR_POSICIONES);
            return false;
        } else if (direccion == Direccion.PUEDE_SER_AMBAS) {
            if (!tablero.validarAdyacentes(posiciones, direccion)) {
                notificarObservadores(Evento.ERROR_ADYACENTES);
                return false;
            }
        }
        return true;
    }

    private boolean esPrimerMovimiento() {
        for (Jugador jugadores : jugadores) {
            if (jugadores.getPuntos() > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean validarPrimerMovimiento() {
        return tablero.esCeldaOcupada(7,7);
    }

    public int puntosJugador() throws RemoteException{
        return getJugadorActual().getPuntos();
    }

    public void setCantidadJugadores(int cantidadJugadores) throws RemoteException{
        this.cantidadJugadores = cantidadJugadores;
    }

    public boolean verificarNombreJugador(String nombre) throws RemoteException {
        for (Jugador jugador : jugadoresRegistrados) {
            if (jugador.getNombre().equals(nombre)) {
                return true;
            }

        }
        return false;
    }

    public Jugador getJugadorActual() throws RemoteException{
        if(turnoActual == null) return null;
        String nombreJugador = turnoActual;
        return obtenerJugador(nombreJugador);
    }

    public Jugador obtenerJugador(String nombreJugador) throws RemoteException{
        for(Jugador jugador : jugadores){
            if(jugador.getNombre().equals(nombreJugador)){
                return jugador;
            }
        }
        return null;
    }

    public Jugador obtenerJugadorPartidaYaIniciada(String nombreJugador) throws RemoteException{
        Map<Integer, Partida> partidaGuardadas = Serializador.cargarPartidaGuardada();
        Partida partidaCargada = partidaGuardadas.get(id);
        ArrayList<Jugador> jugadorPartidaGuardada = partidaCargada.getJugadores();
        for(Jugador jugador : jugadorPartidaGuardada){
            if(jugador.getNombre().equals(nombreJugador)){
                return jugador;
            }
        }
        return null;
    }

    public ArrayList<Jugador> obtenerJugadores() throws RemoteException{
        return jugadores;
    }

    public Ficha obtenerFichaDelAtril(char letraFicha) throws RemoteException{
        Ficha ficha = getJugadorActual().getAtril().obtenerFichaAtril(letraFicha);
        if(ficha != null){
            return ficha;
        }
        notificarObservadores(Evento.ERROR_FICHAS_ATRIL);
        return null;
    }

    public ArrayList<Ficha> obtenerFichasAtril(Jugador jugador) throws RemoteException{
        return jugador.getAtril().getFichasAtril();
    }

    public void pasarTurno() throws RemoteException{
        int turnosPasados = turnosPasadosJugador.getOrDefault(getJugadorActual(),0) + 1;
        turnosPasadosJugador.put(getJugadorActual(), turnosPasados);
        if(turnosPasados >= 2){
            obtenerGanador();
            notificarObservadores(Evento.FIN_PARTIDA);
        }else {
            siguienteTurno();
        }
    }

    public void siguienteTurno() throws RemoteException {
        int indiceActual = jugadores.indexOf(getJugadorActual());
        int siguiente = (indiceActual + 1) % jugadores.size();
        turnoActual = jugadores.get(siguiente).getNombre();
        notificarObservadores(Evento.PASO_TURNO);
    }

    public boolean cambiarFichas(ArrayList<Ficha> fichasCambio) throws RemoteException {
        Jugador jugador = getJugadorActual();
        int cambiosJugador = intercambiosPorJugador.getOrDefault(jugador,0);
        if(bolsa.cantidadFichas() < 7 || cambiosJugador >= 2 || jugador.getPuntos() <= 0) {
            notificarObservadores(Evento.ERROR_CAMBIO);
            return false;
        }
        jugador.getAtril().cambiarFichas(fichasCambio,bolsa);
        intercambiosPorJugador.put(jugador, ++cambiosJugador);
        notificarObservadores(Evento.FICHAS_CAMBIADAS);
        siguienteTurno();
        return true;
    }

    public Celda[][] getTablero()throws RemoteException{
        return tablero.getCeldas();
    }

    public void restaurarEstadoJuego() throws RemoteException {
        Jugador jugador = getJugadorActual();
        if(posicionesFichas != null) {
            for(PosicionCelda posicion : posicionesFichas){
                tablero.restaurarCelda(posicion);
            }
        }
        jugador.getAtril().restaurarAtril();
    }

    public void rellenarAtril() throws RemoteException{
        Jugador jugador = getJugadorActual();
        jugador.getAtril().rellenarAtril(bolsa);
    }

    public void guardarPartida() throws RemoteException {
        Partida partida = new Partida(tablero, bolsa, turnoActual, jugadores, id, this, nombreHost);
        if (!partidasGuardadas.containsKey(id)) {
            partidasGuardadas.put(id,partida);
            Serializador.guardarPartida(partidasGuardadas);
            Serializador.guardarJugadores(jugadoresRegistrados);
            notificarObservadores(Evento.PARTIDA_GUARDADA);
        }
    }

    public boolean cargarPartida(int IDPartida, String nombreJugador) throws RemoteException{
        Map<Integer, Partida> partidaGuardadas = Serializador.cargarPartidaGuardada();
        Partida partidaCargada = partidaGuardadas.get(IDPartida);
        if(partidaGuardadas.containsKey(IDPartida) && partidaCargada.contieneJugador(nombreJugador)) {
            tablero = partidaCargada.getTablero();
            bolsa = partidaCargada.getBolsa();
            this.jugadores = new ArrayList<>();
            this.id = IDPartida;
            turnoActual = partidaCargada.getTurnoActual();
            setCantidadJugadores(partidaCargada.getJugadores().size());
            return true;
        }
        return false;
    }

    public ArrayList<Partida> getPartidasGuardadas() throws RemoteException{
        return new ArrayList<>(Serializador.cargarPartidaGuardada().values());
    }

    public ArrayList<Jugador> obtenerTop5Jugadores() throws RemoteException{
       return ranking.obtenerTop5();
    }

    public Jugador obtenerJugadorGanador() throws RemoteException{
        Jugador jugadorGanador = null;
        int maxPuntos = Integer.MIN_VALUE;
        for (Jugador jugador : jugadores) {
            if (jugador.getPuntos() > maxPuntos) {
                maxPuntos = jugador.getPuntos();
                jugadorGanador = jugador;
            }
        }
        return jugadorGanador;
    }

    public void obtenerGanador()throws RemoteException {
        Serializador.guardarJugadores(jugadoresRegistrados);
        Jugador jugadorGanador = null;
        int puntosAtril = 0;
        for (Jugador jugador : jugadores) {
            if (jugador.getAtril().getFichasAtril().isEmpty()) {
                for (Jugador oponente : jugadores) {
                    if (oponente != jugador) {
                        puntosAtril = oponente.getAtril().puntosRestantes();
                        jugador.sumarPuntos(puntosAtril);
                    }
                }
            } else {
                puntosAtril = jugador.getAtril().puntosRestantes();
                jugador.restarPuntos(puntosAtril);
            }
        }
    }
}
