package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.MODELO.BonificacionTablero;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GestorImagenes extends JPanel{
    private static final Map<Character, ImageIcon> cacheFichas = new HashMap<>();
    private static final Map<Character, ImageIcon> cacheFichasComodin = new HashMap<>();
    private static final Map<BonificacionTablero, ImageIcon> cacheBonificaciones = new HashMap<>();

    private static final Map<String, ImageIcon> imagenes = new HashMap<>();
    // Carga la imagen de una ficha según la letra
    public ImageIcon cargarImagenFicha(char letra) {
        if (cacheFichas.containsKey(letra)) {
            return cacheFichas.get(letra); // Devuelve desde la caché si ya está cargada
        }
        String ruta = "src/ar/edu/unlu/poo/RESOURCES/LETRAS/" + letra + ".jpg"; // Ajusta la carpeta donde guardas las imágenes
        try {
            Image imagen = ImageIO.read(new File(ruta));
            BufferedImage bufferedImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bufferedImage.createGraphics();

            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRoundRect(3, 3, 45, 45, 8, 8);
            g2.drawImage(imagen, 0, 0, 45, 45, null);
            g2.dispose();
            ImageIcon imageIcon = new ImageIcon(imagen.getScaledInstance(45, 45, Image.SCALE_SMOOTH));
            cacheFichas.put(letra, imageIcon);
            return imageIcon;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ImageIcon obtenerImagenCeldaEspecial(BonificacionTablero bonificacion) {
        if (cacheBonificaciones.containsKey(bonificacion)) {
            return cacheBonificaciones.get(bonificacion);
        }
        String ruta = "src/ar/edu/unlu/poo/RESOURCES/BONIFICACIONES/" + bonificacion.toString() + ".png";
        try {
            Image imagen = ImageIO.read(new File(ruta));
            ImageIcon imageIcon = new ImageIcon(imagen.getScaledInstance(65, 65, Image.SCALE_SMOOTH));
            cacheBonificaciones.put(bonificacion, imageIcon);
            return imageIcon;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ImageIcon cargarImagenFichaComodin(char letra) {
        if (cacheFichasComodin.containsKey(letra)) {
            return cacheFichasComodin.get(letra);
        }
        String ruta = "src/ar/edu/unlu/poo/RESOURCES/LETRASCOMODIN/" + letra + ".jpeg"; // Ajusta la carpeta donde guardas las imágenes
        try {
            Image imagen = ImageIO.read(new File(ruta));
            BufferedImage bufferedImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bufferedImage.createGraphics();

            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRoundRect(3, 3, 45, 45, 8, 8);
            g2.drawImage(imagen, 0, 0, 45, 45, null);
            g2.dispose();
            ImageIcon imageIcon = new ImageIcon(imagen.getScaledInstance(45, 45, Image.SCALE_SMOOTH));
            cacheFichasComodin.put(letra, imageIcon);
            return imageIcon;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ImageIcon crearImagenComodinConLetra(char letra) {
        int ancho = 45;
        int alto = 45;

        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagen.createGraphics();

        // Fondo blanco
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, ancho, alto);

        // Borde gris
        g2.setColor(Color.GRAY);
        g2.drawRect(0, 0, ancho - 1, alto - 1);

        // Letra centrada
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.setColor(Color.BLACK);
        FontMetrics fm = g2.getFontMetrics();
        int x = (ancho - fm.charWidth(letra)) / 2;
        int y = (alto + fm.getAscent()) / 2 - 4;
        g2.drawString(String.valueOf(letra), x, y);

        g2.dispose();
        return new ImageIcon(imagen);
    }

    /*
    @startuml
class ScrabbleGame {
    - tablero : Tablero
    - bolsa : Bolsa
    - diccionario : Diccionario
    - turnoActual : String
    - nombreHost : String
    - jugadores : ArrayList<Jugador>
    - jugadoresRegistrados : ArrayList<Jugador>
    - partidasGuardadas : Map<Integer, Partida>
    - cantidadJugadores : int
    - id : int
    - posicionesFichas : ArrayList<PosicionCelda>
    - turnosPasadosJugador : Map<Jugador, Integer>
    - intercambiosPorJugador : Map<Jugador, Integer>
    - ranking : Ranking
    - instancia : static ScrabbleGame

    + ScrabbleGame()
    + getInstancia() : ScrabbleGame
    + inicializarJuego()
    + repartirFichas()
    + conectarJugador(jugador : Jugador)
    + agregarJugador(nombre : String) : Jugador
    + partidaIniciada() : boolean
    + inicializarPrimerTurno()
    + esCeldaLibreYValida(posicion : PosicionCelda) : boolean
    + colocarFichaEnCelda(ficha : Ficha, posicion : PosicionCelda)
    + colocarFichaComodinEnCelda(ficha : Ficha, posicion : PosicionCelda, letraComodin : char)
    + esComodin(ficha : Ficha) : boolean
    + obtenerFichas() : ArrayList<Ficha>
    + formarYvalidarPalabra(posiciones : ArrayList<PosicionCelda>)
    + puntosJugador() : int
    + setCantidadJugadores(cantidad : int)
    + verificarNombreJugador(nombre : String) : boolean
    + getJugadorActual() : Jugador
    + obtenerJugador(nombre : String) : Jugador
    + obtenerJugadorPartidaYaIniciada(nombre : String) : Jugador
    + obtenerJugadores() : ArrayList<Jugador>
    + obtenerFichaDelAtril(letra : char) : Ficha
    + obtenerFichasAtril(jugador : Jugador) : ArrayList<Ficha>
    + pasarTurno()
    + siguienteTurno()
    + cambiarFichas(fichasCambio : ArrayList<Ficha>) : boolean
    + getTablero() : Celda[][]
    + restaurarEstadoJuego()
    + rellenarAtril()
    + guardarPartida()
    + cargarPartida(IDPartida : int, nombreJugador : String) : boolean
    + getPartidasGuardadas() : ArrayList<Partida>
    + obtenerTop5Jugadores() : ArrayList<Jugador>
    + obtenerGanador() : Jugador
}

 class Bolsa {
    - fichas: ArrayList<Ficha>
    + Bolsa()
    + obtenerLetrasDisponibles(): ArrayList<Ficha>
    + iniciarFichasEnBolsa()
    + agregarFicha(Ficha)
    + sacarFichaDeLaBolsa(): Ficha
    + cantidadFichas(): int
    + esVacia(): boolean
    + mezclar()
  }

  class Atril {
    - fichasAtril: ArrayList<Ficha>
    - guardarAtril: ArrayList<Ficha>
    + Atril()
    + getFichasAtril(): ArrayList<Ficha>
    + setFichasAtril(Ficha)
    + generarAtril(Bolsa)
    + sacarFichaDelAtril(Ficha): Ficha
    + rellenarAtril(Bolsa)
    + obtenerFichaAtril(char): Ficha
    + tieneFichasCambio(ArrayList<Ficha>): boolean
    + cambiarFichas(ArrayList<Ficha>, Bolsa)
    + restaurarAtril(): ArrayList<Ficha>
    + puntosRestantes(): int
  }

  class Celda {
    - posicion: PosicionCelda
    - bonificacion: BonificacionTablero
    - ficha: Ficha
    - estado: EstadoCelda
    + Celda(PosicionCelda, BonificacionTablero)
    + getEstado(): EstadoCelda
    + getFicha(): Ficha
    + setFicha(Ficha)
    + getBonificacionLetra(): int
    + getBonificacionPalabra(): int
    + agregarFicha(Ficha)
    + sacarFicha()
  }
class Tablero {
  - celdas: Celda[][]
  - diccionario: Diccionario
  + Tablero(diccionario: Diccionario)
  + inicializarTablero()
  + obtenerBonificacion(posicion: PosicionCelda): BonificacionTablero
  + agregarFicha(ficha: Ficha, posicion: PosicionCelda): boolean
  + getCeldas(): Celda[][]
  + getCelda(posicion: PosicionCelda): Celda
  + getCelda(fila: int, columna: int): Celda
  + restaurarCelda(posicion: PosicionCelda)
  + esCeldaOcupada(fila: int, columna: int): boolean
  + validarPosicion(posiciones: ArrayList<PosicionCelda>): Direccion
  + obtenerPosiciones(posiciones: ArrayList<PosicionCelda>, direccion: Direccion): ArrayList<PosicionCelda>
  + validarAdyacentes(posiciones: ArrayList<PosicionCelda>, direccion: Direccion): boolean
  - obtenerInicio(fila: int, columna: int, vertical: int, horizontal: int): PosicionCelda
  + validarPalabra(posicionesPalabra: ArrayList<PosicionCelda>): boolean
  + calcularPuntosPalabra(posFichasColocadas: ArrayList<PosicionCelda>, posPalabraCompleta: ArrayList<PosicionCelda>, direccion: Direccion): int
}

class Diccionario {
  - NOMBRE_ARCHIVO: String = "..."
  - palabrasDiccionario: HashSet<String>
  + Diccionario()
  + cargarDiccionario()
  + esPalabraValida(palabra: String): boolean
}

class Jugador {
  - nombre: String
  - atril: Atril
  - puntos: int
  + Jugador(nombre: String)
  + getNombre(): String
  + getPuntos(): int
  + sumarPuntos(puntos: int)
  + restarPuntos(puntos: int)
  + getAtril(): Atril
}
class PosicionCelda {
    - int posicionX
    - int posicionY
    + PosicionCelda(int posX, int posY)
    + int getPosX()
    + int getPosY()
}

class Ficha {
    - char letra
    - int puntos
    + Ficha(char letra, int puntos)
    + char getLetra()
    + boolean esComodin()
    + int getPuntos()
    + void asignarLetraComodin(char letra)
}

class Partida {
    - Tablero tablero
    - Bolsa bolsa
    - ArrayList<Jugador> jugadores
    - String turnoActual
    - String nombreHost
    - ScrabbleGame ScrabbleGame
    - int id
    + Partida(Tablero, Bolsa, String, ArrayList<Jugador>, int, ScrabbleGame, String)
    + Tablero getTablero()
    + Bolsa getBolsa()
    + ArrayList<Jugador> getJugadores()
    + String getTurnoActual()
    + String getNombreHost()
    + ScrabbleGame getScrabbleGame()
    + int getId()
    + boolean contieneJugador(String)
}

class Ranking {
    - static Ranking instancia
    - ArrayList<Jugador> ranking
    + Ranking()
    + static Ranking getInstance()
    - void ordenarRanking()
    + ArrayList<Jugador> obtenerTop5()
}

class PosicionCelda {
  -int posicionX
  -int posicionY
  +getPosX(): int
  +getPosY(): int
}

class Ficha {
  -char letra
  -int puntos
  +getLetra(): char
  +esComodin(): boolean
  +getPuntos(): int
  +asignarLetraComodin(letra: char)
}

class Partida {
  -Tablero tablero
  -Bolsa bolsa
  -ArrayList<Jugador> jugadores
  -String turnoActual
  -String nombreHost
  -ScrabbleGame ScrabbleGame
  -int id
  +getTablero(): Tablero
  +getBolsa(): Bolsa
  +getJugadores(): ArrayList<Jugador>
  +getTurnoActual(): String
  +getNombreHost(): String
  +getScrabbleGame(): ScrabbleGame
  +getId(): int
  +contieneJugador(nombreJugador: String): boolean
}

class Palabra {
  -Tablero tablero
  -ArrayList<PosicionCelda> posicionPalabras
  +esPalabraValida(diccionario: Diccionario): boolean
  +calcularPuntos(posFichasColocadas: ArrayList<PosicionCelda>, direccion: Direccion): int
  -calcularPuntosAdyacentes(posicionesFichas: ArrayList<PosicionCelda>, direccion: Direccion): int
  -calcularPuntosPerpendicular(pos: PosicionCelda, esVertical: boolean, posFichasColocadas: ArrayList<PosicionCelda>): int
  -celdaOcupada(fila: int, columna: int): boolean
}

class ScrabbleControlador implements IControladorRemoto {
    -IVista vista
    -IScrabbleGame juego
    -String jugadorID
    +ScrabbleControlador()
    +ScrabbleControlador(ScrabbleGame modelo)
    +setVista(IVista vista)
    +iniciarJuego()
    +agregarJugador(nombre)
    +perteneceJugadorAPartida(nombre) : boolean
    +partidaYaIniciada() : boolean
    +realizarAccionSiEsTurno() : boolean
    +celdaValida(posicion) : boolean
    +colocarFichaSeleccionadaEnTablero(ficha, posicion)
    +colocarFichaComodinEnTablero(ficha, letraComodin, posicion)
    +existeNombre(nombre) : boolean
    +obtenerTurnoActual() : String
    +obtenerFicha(letra) : Ficha
    +obtenerFichasAtril(jugadorLocal) : List<Ficha>
    +obtenerFichas() : List<Ficha>
    +pasarTurno()
    +cambiarFichas(fichasCambiar) : boolean
    +enviarPalabra(posiciones)
    +obtenerAtrilJugador() : List<Ficha>
    +guardarPartida()
    +cargarPartida(nombre, ID)
    +obtenerRanking() : List<Jugador>
    -restaurarJuego()
    +setModeloRemoto(T modeloRemoto)
    +actualizar(instanciaModelo, cambio)
}

interface IScrabbleGame {
  +inicializarJuego()
  +repartirFichas()
  +conectarJugador(jugador: Jugador)
  +partidaIniciada(): boolean
  +obtenerJugadorPartidaYaIniciada(nombreJugador: String): Jugador
  +inicializarPrimerTurno()
  +getTablero(): Celda[][]
  +esCeldaLibreYValida(posicion: PosicionCelda): boolean
  +colocarFichaEnCelda(ficha: Ficha, posicion: PosicionCelda)
  +verificarNombreJugador(nombre: String): boolean
  +getJugadorActual(): Jugador
  +obtenerJugador(nombreJugador: String): Jugador
  +obtenerJugadores(): ArrayList<Jugador>
  +obtenerFichaDelAtril(letraFicha: char): Ficha
  +colocarFichaComodinEnCelda(ficha: Ficha, posicion: PosicionCelda, letraComodin: char)
  +pasarTurno()
  +siguienteTurno()
  +cambiarFichas(fichasCambiar: ArrayList<Ficha>): boolean
  +formarYvalidarPalabra(posiciones: ArrayList<PosicionCelda>)
  +obtenerFichasAtril(jugador: Jugador): ArrayList<Ficha>
  +restaurarEstadoJuego()
  +rellenarAtril()
  +puntosJugador(): int
  +guardarPartida()
  +cargarPartida(IDPartida: int, nombreJugador: String): boolean
  +getPartidasGuardadas(): ArrayList<Partida>
  +obtenerTop5Jugadores(): ArrayList<Jugador>
  +obtenerGanador(): Jugador
  +esComodin(ficha: Ficha): boolean
  +obtenerFichas(): ArrayList<Ficha>
  +agregarJugador(nombre: String): Jugador
}

interface IControladorRemoto {
    +setModeloRemoto(T modeloRemoto)
    +actualizar(IObservableRemoto instanciaModelo, Object cambio)
}
enum Evento {
  JUGADOR_CONECTADO
  INICIAR_PARTIDA
  CAMBIOS_PARTIDA
  ERROR_POSICIONES
  PASO_TURNO
  ERROR_CAMBIO
  ERROR_FICHAS_ATRIL
  FICHAS_CAMBIADAS
  ERROR_CENTRO
  ERROR_DICCIONARIO
  ERROR_ADYACENTES
  CELDA_OCUPADA
  FIN_PARTIDA
  PALABRA_AGREGADA
  PARTIDA_GUARDADA
}

enum EstadoCelda {
  LIBRE
  OCUPADA
}

enum Direccion {
  HORIZONTAL
  VERTICAL
  INVALIDA
  PUEDE_SER_AMBAS
}

enum BonificacionTablero {
  NORMAL
  DOBLE_PALABRA
  TRIPLE_PALABRA
  DOBLE_LETRA
  TRIPLE_LETRA
  CENTRO
}

interface IVista {
    +mostrarMensaje(s: String): void
    +mostrarTablero(celdas: Celda[][]): void
    +mostrarAtril() throws RemoteException
    +mostrarTurno() throws RemoteException
    +iniciarVista(nombre: String) throws RemoteException
    +getJugadorLocal(): String
    +limpiar() throws RemoteException
    +pedirFicha(posicion: PosicionCelda) throws RemoteException
    +mostrarPuntajes(jugadores: ArrayList<Jugador>): void
    +mostrarFinDePartida(ganador: Jugador, jugadores: ArrayList<Jugador>): void
    +deshabilitarInteraccion(): void
}

' Clase VistaGraficaa que implementa IVista
class VistaGraficaa extends JFrame {
    -controlador: ScrabbleControlador
    -jugadorLocal: String
    -tablero: JPanel
    -atril: JPanel
    -panelDerecho: JPanel
    -panelesAtril: JPanel[]
    -celdasTablero: JPanel[][]
    -fichaSeleccionada: Ficha
    -seleccionFicha: boolean
    -fichasSeleccionadasAtril: ArrayList<Ficha>
    -textMensajes: JTextArea
    -botones: JPanel
    -etiquetasPuntajes: JLabel[]
    -posicionesColocadas: ArrayList<PosicionCelda>
    -gestorImagenes: GestorImagenes
    -pasarTurno: JButton
    -cambiarFicha: JButton
    -enviarPalabra: JButton

    +VistaGraficaa(controlador: ScrabbleControlador, nombre: String) throws IOException
    +iniciarJuego(): void
    +mostrarTablero(celdas: Celda[][]): void
    +turnoActual(): boolean throws RemoteException
    +getJugadorLocal(): String
    +iniciarVista(nombreJugador: String) throws RemoteException
    +mostrarMensaje(s: String): void
    +mostrarAtril() throws RemoteException
    +mostrarTurno() throws RemoteException
    +limpiar() throws RemoteException
    +pedirFicha(posicion: PosicionCelda) throws RemoteException
    +mostrarPuntajes(jugadores: ArrayList<Jugador>): void
    +mostrarFinDePartida(ganador: Jugador, jugadores: ArrayList<Jugador>): void
    +deshabilitarInteraccion(): void
}

' Relación de implementación
IVista <|.. VistaGraficaa
VistaGraficaa --> ScrabbleControlador : usa
VistaGraficaa --> GestorImagenes : usa
ScrabbleGame "1" -- "1" Tablero : tiene >
ScrabbleGame "1" -- "1" Bolsa : tiene >
ScrabbleGame "1" -- "*" Jugador : maneja >
ScrabbleGame "1" -- "*" Partida : guarda >
ScrabbleGame "1" -- "1" Ranking : usa >
ScrabbleControlador --> IVista : usa

Tablero "1" -- "*" Celda : contiene >
Tablero "1" -- "1" Diccionario : usa >

Celda "1" -- "1" PosicionCelda : tiene >
Celda "1" -- "0..1" Ficha : contiene >

Jugador "1" -- "1" Atril : tiene >
Atril "1" -- "*" Ficha : contiene >

Bolsa "1" -- "*" Ficha : contiene >

Partida "1" -- "1" Tablero : usa >
Partida "1" -- "1" Bolsa : usa >
Partida "1" -- "*" Jugador : tiene >

Palabra "1" -- "1" Tablero : usa >
Palabra "1" -- "*" PosicionCelda : contiene >

Ficha "0..1" -- "*" PosicionCelda : está en >

ScrabbleGame ..|> IScrabbleGame

Celda *-- EstadoCelda : estado
Tablero *-- BonificacionTablero : bonificaciones
Tablero *-- Direccion : validaciones
ScrabbleGame *-- Evento : genera
ScrabbleControlador --> IScrabbleGame : usa
@enduml
     */

}
