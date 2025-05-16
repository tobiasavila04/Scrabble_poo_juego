package ar.edu.unlu.poo;

import ar.edu.unlu.poo.MODELO.Jugador;
import ar.edu.unlu.poo.MODELO.Partida;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Serializador {
    private static final String ARCHIVO_GUARDAR_JUGADORES = "guardarJugadores.dat";
    private static final String ARCHIVO_GUARDAR_PARTIDAS = "guardarPartidas.dat";
    private static final ArrayList<Jugador> jugadores = new ArrayList<>();

    public static void guardarPartida(Map<Integer, Partida> partidas) {
        guardarObjeto(ARCHIVO_GUARDAR_PARTIDAS,partidas);
    }

    public static void guardarJugadores(ArrayList<Jugador> jugador) {
        guardarObjeto(ARCHIVO_GUARDAR_JUGADORES, jugador);
    }


   public static Map<Integer,Partida> cargarPartidaGuardada() {
       Map<Integer,Partida> partidas = cargarObjeto(ARCHIVO_GUARDAR_PARTIDAS);
        if(partidas == null){
            partidas = new HashMap<>();
        }
        return partidas;
    }

    public static ArrayList<Jugador> cargarJugadorHistorico() {
        ArrayList<Jugador> jugadores = cargarObjeto(ARCHIVO_GUARDAR_JUGADORES);
        if (jugadores == null) {
            jugadores = new ArrayList<>();
        }
        return jugadores;
    }

    public static void guardarObjeto(String archivo, Object objeto) {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo))) {
            salida.writeObject(objeto);
        } catch (IOException e) {
            System.err.println("Error al guardar en el archivo: " + archivo);
            e.printStackTrace();
        }
    }

    public static <T> T cargarObjeto(String archivo) {
        File file = new File(archivo);
        if (file.exists()) {
            try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {
                return (T) entrada.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar desde el archivo: " + archivo);
                e.printStackTrace();
            }
        }
        return null;
    }

}
