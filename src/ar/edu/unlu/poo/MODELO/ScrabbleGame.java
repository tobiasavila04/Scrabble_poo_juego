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
    private Diccionario diccionario;
    private String turnoActual;
    private Palabra palabra;
    private ArrayList<Jugador> jugadores, jugadoresRegistrados = null;
    private Map<Integer,Partida> partidasGuardadas;
    private int cantidadJugadores;
    public static ScrabbleGame instanica;
    private ArrayList<PosicionCelda> posicionesFichas;
    private Map<Jugador, Integer> turnosPasadosJugador, intercambiosPorJugador;
    private int id;


    public ScrabbleGame() throws IOException {
        this.jugadores = new ArrayList<>();
        this.posicionesFichas = new ArrayList<>();
        this.bolsa = new Bolsa();
        this.diccionario = new Diccionario();
        this.tablero = new Tablero(diccionario);
        this.palabra = new Palabra(tablero,diccionario);
        turnosPasadosJugador = new HashMap<>();
        intercambiosPorJugador = new HashMap<>();
        if (this.jugadoresRegistrados == null) {
            this.jugadoresRegistrados = new ArrayList<>();
        }else{
            this.jugadoresRegistrados = Serializador.cargarJugadorHistorico();
        }
        if (this.partidasGuardadas == null) {
            this.partidasGuardadas = new HashMap<>();
        }else{
            this.partidasGuardadas = Serializador.cargarPartidaGuardada();
        }

    }

    public static ScrabbleGame getInstancia() throws IOException {
        if(instanica == null){
            instanica = new ScrabbleGame();
        }
        return instanica;
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
        if(!jugadoresRegistrados.contains(jugador)){
            jugadores.add(jugador);
            jugadoresRegistrados.add(jugador);
            Serializador.guardarJugadores(jugadoresRegistrados);
        }
        if(jugadores.size() == cantidadJugadores){
            inicializarJuego();
            notificarObservadores(Evento.INICIAR_PARTIDA);
        }else{
            notificarObservadores(Evento.JUGADOR_CONECTADO);
        }
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
        return tablero.getCelda(posicion).getEstado() == EstadoCelda.LIBRE;
    }

    public void colocarFichaEnCelda(Ficha fichaSeleccioanda, PosicionCelda posicion) throws RemoteException{
      //  boolean agregado = tablero.agregarFicha(fichaSeleccioanda,posicion);
        if(tablero.agregarFicha(fichaSeleccioanda,posicion)) {
            getJugadorActual().getAtril().sacarFichaDelAtril(fichaSeleccioanda.getLetra());
            notificarObservadores(Evento.CAMBIOS_PARTIDA);
        }else{
            notificarObservadores(Evento.CELDA_OCUPADA);
        }
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
            }else if (!palabra.esPalabraValida(posPalabra)) {
                notificarObservadores(Evento.ERROR_DICCIONARIO);
                return;
            }else if (!tablero.validarAdyacentes(posPalabra, direccion) && !esPrimerMovimiento()) {
                notificarObservadores(Evento.ERROR_ADYACENTES);
                return;
            }
        }
        int puntos = palabra.calcularPuntos(posiciones,posPalabra, direccion);
        getJugadorActual().setPuntos(puntos);
        getJugadorActual().getAtril().rellenarAtril(bolsa);
        notificarObservadores(Evento.PALABRA_AGREGADA);
        siguienteTurno();
        if(getJugadorActual().getAtril().getFichasAtril().isEmpty() && bolsa.esVacia()){
            notificarObservadores(Evento.FIN_PARTIDA);
        }
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
        PosicionCelda centro = new PosicionCelda(7,7);
        return tablero.getCelda(centro).getEstado() == EstadoCelda.OCUPADA;
    }

    public int puntosJugador() throws RemoteException{
        return getJugadorActual().getPuntos();
    }

    public void setCantidadJugadores(int cantidadJugadores) throws RemoteException{
        this.cantidadJugadores = cantidadJugadores;
    }

    public boolean verificarNombreJugador(String nombre) throws RemoteException {
        if (jugadoresRegistrados == null) {
            return false;
        }
        for (Jugador jugador : jugadoresRegistrados) {
            if (jugador.getNombre().equalsIgnoreCase(nombre)) {
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

    public Ficha obtenerFichaDelAtril(char letraFicha) throws RemoteException{
        Ficha ficha = getJugadorActual().getAtril().obtenerFichaAtril(letraFicha);
        if(ficha != null){
            return ficha;
        }
        notificarObservadores(Evento.ERROR_FICHAS_ATRIL);
        return null;
    }

    public ArrayList<Ficha> obtenerFichasAtril(Jugador jugador) throws RemoteException{
        if(jugador != null){
           return jugador.getAtril().getFichasAtril();
        }
        return null;
    }

    public void pasarTurno() throws RemoteException{
        int turnosPasados = turnosPasadosJugador.getOrDefault(getJugadorActual(),0);
        turnosPasados++;
        turnosPasadosJugador.put(getJugadorActual(), turnosPasados);
        if(turnosPasados >= 2){
            notificarObservadores(Evento.FIN_PARTIDA);
        }else {
            siguienteTurno();
        }
    }

    private void siguienteTurno() throws RemoteException {
        int indiceActual = jugadores.indexOf(getJugadorActual());
        int siguiente = (indiceActual + 1) % jugadores.size();
        turnoActual = jugadores.get(siguiente).getNombre();
        notificarObservadores(Evento.PASO_TURNO);
    }

    public void cambiarFichas(ArrayList<Ficha> fichasCambio) throws RemoteException {
        Jugador jugador = getJugadorActual();
        int cambiosJugador = intercambiosPorJugador.getOrDefault(jugador,0);
        if(bolsa.cantidadFichas() < 7 || cambiosJugador >= 2 || jugador.getPuntos() <= 0 || jugador.getAtril().tieneFichasCambio(fichasCambio)) {
            notificarObservadores(Evento.ERROR_CAMBIO);
            return;
        }
        jugador.getAtril().cambiarFichas(fichasCambio,bolsa);
        intercambiosPorJugador.put(jugador, ++cambiosJugador);
        notificarObservadores(Evento.FICHAS_CAMBIADAS);
        siguienteTurno();
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
        Partida partida = new Partida(tablero, bolsa, turnoActual, jugadores);
        if (!partidasGuardadas.containsKey(id)) {
            partidasGuardadas.put(id,partida);
            Serializador.guardarPartida(partidasGuardadas);
        }
    }

    public boolean cargarPartida(int IDPartida, String nombreJugador) throws RemoteException{
        Map<Integer, Partida> partidaGuardadas = Serializador.cargarPartidaGuardada();
        if(IDPartida < 0 || IDPartida >=  partidaGuardadas.size()){
            return false;
        }
        Partida partidaCargada = partidaGuardadas.get(IDPartida);
        if(!partidaGuardadas.containsKey(IDPartida) && partidaCargada.contieneJugador(nombreJugador)) {
            tablero = partidaCargada.getTablero();
            bolsa = partidaCargada.getBolsa();
            turnoActual = partidaCargada.getTurnoActual();
            jugadores = partidaCargada.getJugadores();
            return true;
        }
        return false;
    }

    public List<Partida> getPartidasGuardadas() throws RemoteException{
        return new ArrayList<>(Serializador.cargarPartidaGuardada().values());
    }

    public List<Jugador> obtenerTop5Jugadores() throws RemoteException{
        jugadoresRegistrados = Serializador.cargarJugadorHistorico();
        return jugadoresRegistrados.stream().sorted(Comparator.comparingInt(Jugador::getPuntos).reversed()).limit(5).collect(Collectors.toList());
    }

    public Jugador obtenerGanador()throws RemoteException{
        Jugador jugadorGanador = null;
        int puntosAtril = 0, maxPuntos = 0;
        for(Jugador jugador : jugadores){
            if(jugador.getAtril().getFichasAtril().isEmpty()){
                for(Jugador oponente : jugadores){
                    if(oponente != jugador){
                        puntosAtril = oponente.getAtril().puntosRestantes();
                        jugador.setPuntos(puntosAtril);
                    }
                }
            }else{
                puntosAtril = jugador.getAtril().puntosRestantes();
                jugador.restarPuntos(puntosAtril);
            }

            if(jugador.getPuntos() > 0){
                maxPuntos = jugador.getPuntos();
                jugadorGanador = jugador;
            }
        }
        return jugadorGanador;
    }
}