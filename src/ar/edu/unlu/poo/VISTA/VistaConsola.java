package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.CONTROLADOR.ScrabbleControlador;
import ar.edu.unlu.poo.MODELO.Celda;
import ar.edu.unlu.poo.MODELO.Ficha;
import ar.edu.unlu.poo.MODELO.Jugador;
import ar.edu.unlu.poo.MODELO.PosicionCelda;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class VistaConsola extends JFrame implements IVista {
    private ScrabbleControlador controlador;
    private String jugadorLocal;
    private JTextArea textMenu, puntajes;
    private JTextField txtInput;
    private JPanel lblAtrilTablero;
    private JLabel atril;
    private JTextArea tablero;
    private ArrayList<PosicionCelda> posicionesColocadas = new ArrayList<>();
    private Estado estadoEntrada;
    private Ficha fichaSeleccionada;
    private ArrayList<Ficha> fichasCambio = new ArrayList<>();
    private PosicionCelda posicionComodin;

    public VistaConsola(ScrabbleControlador controlador, String nombre) {
        this.controlador = controlador;
        this.jugadorLocal = nombre;
        controlador.setVista(this);
        new MenuPrincipalConsola(this, controlador, jugadorLocal);
    }

    public void iniciarJuego(){
        setTitle("Scrabble Game - Consola");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        tablero = new JTextArea();
        tablero.setPreferredSize(new Dimension(400, 30));
        tablero.setEditable(false);
        tablero.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Para alineación
        tablero.setLineWrap(true);
        tablero.setBackground(Color.BLACK);
        tablero.setForeground(Color.WHITE);

        textMenu = new JTextArea(10,34);
        textMenu.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        textMenu.setEditable(false);
        textMenu.setLineWrap(true);
        textMenu.setBackground(Color.BLACK);
        textMenu.setForeground(Color.WHITE);
        JScrollPane scrollMenu = new JScrollPane(textMenu);;
        scrollMenu.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Panel del atril
        atril = new JLabel("ATRIL: ");
        atril.setFont(new Font("Monospaced", Font.BOLD, 16));
        atril.setForeground(Color.WHITE);
        JPanel panelAtril = new JPanel();
        panelAtril.setPreferredSize(new Dimension(400, 60)); // Más grande
        panelAtril.setBackground(Color.BLACK);
        panelAtril.setOpaque(true);
        panelAtril.add(atril, BorderLayout.CENTER);

        lblAtrilTablero = new JPanel(new BorderLayout());
        lblAtrilTablero.add(tablero, BorderLayout.CENTER);
        lblAtrilTablero.add(panelAtril, BorderLayout.SOUTH);

        txtInput = new JTextField(20);
        txtInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(turnoActual()) {
                        procesarEntrada(txtInput.getText()); // Procesa lo que el jugador escribe
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                txtInput.setText("");
            }
        });

        puntajes = new JTextArea(5,5);
        puntajes.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        puntajes.setEditable(false);
        puntajes.setLineWrap(true);
        puntajes.setBackground(Color.BLACK);
        puntajes.setForeground(Color.WHITE);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(txtInput, BorderLayout.NORTH);
        panelInferior.add(puntajes, BorderLayout.SOUTH); // este es el que vos definiste

        panelDerecho.add(scrollMenu, BorderLayout.CENTER);
        panelDerecho.add(panelInferior, BorderLayout.SOUTH);


        add(lblAtrilTablero, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);
    }

    private void procesarEntrada(String entrada) throws IOException {
        entrada = entrada.toUpperCase();
        switch (estadoEntrada){
            case MENU -> procesarMenu(entrada);
            case INGRESAR_FICHA -> procesarIngresoFicha(entrada);
            case MENU_JUGADOR -> procesarMenuJugador(entrada);
            case INGRESAR_POSICION -> procesarIngresoPosicion(entrada);
            case CAMBIAR_FICHAS -> procesarCambioFicha(entrada);
            case CAMBIANDO_FICHAS -> procesarCambiandoFicha(entrada);
            case FICHA_COMODIN -> procesarIngresoFichaComodin(entrada);
        }
    }

    private void mostrarMenuJugador() {
        println("\n1 - Colocar Ficha\n");
        println("2 - Enviar Palabra\n");
        println("\nIngrese una opción: ");
    }

    private void procesarMenuJugador(String entrada) throws IOException {
        switch (entrada){
            case "1":{
                println("\nIngrese ficha del atril: ");
                estadoEntrada = Estado.INGRESAR_FICHA;
                break;
            }
            case "2": {
                if(!posicionesColocadas.isEmpty()) {
                    controlador.enviarPalabra(posicionesColocadas);
                }
                break;
            }
            case "3":
        };
    }

    private void procesarIngresoFicha(String entrada) throws RemoteException {
        if (entrada.length() == 1) {
            fichaSeleccionada = controlador.obtenerFicha(entrada.charAt(0));

            if (fichaSeleccionada != null) {
                println("\nIngrese fila, columna (x,y): ");
                estadoEntrada = Estado.INGRESAR_POSICION;
            } else {
                println("\nFicha no encontrada. Intente nuevamente: ");
            }
        } else {
            println("\nDebe ingresar una sola letra: ");
        }
    }

    private void procesarIngresoPosicion(String entrada){
        String[] partes = entrada.split(",");
        if (partes.length == 2) {
            try {
                int fila = Integer.parseInt(partes[0].trim());
                int columna = Integer.parseInt(partes[1].trim());

                PosicionCelda posicion = new PosicionCelda(fila, columna);
                if(controlador.celdaValida(posicion)) {
                    posicionesColocadas.add(posicion);
                    controlador.colocarFichaSeleccionadaEnTablero(fichaSeleccionada, posicion);
                    /*println("\nFicha colocada en (" + fila + "," + columna + ")\n");
                    estadoEntrada = Estado.MENU_JUGADOR;
                    mostrarMenuJugador();*/
                }else{
                    println("la celda ya esta ocupada!");
                    println("\nIngrese fila, columna (x,y): ");
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        } else {
            println("\nFormato incorrecto. Use: fila,columna\n");
        }
    }

    private void mostrarMenu() {
        estadoEntrada = Estado.MENU;
        println("1 - Colocar palabra\n");
        println("2 - Pasar turno\n");
        println("3 - Cambiar Fichas\n");
        println("\nIngrese una opción: ");
    }

    private void procesarMenu(String entrada) throws RemoteException {
        switch (entrada){
            case "1":
                estadoEntrada = Estado.MENU_JUGADOR;
                mostrarMenuJugador();
                break;
            case "2":
                controlador.pasarTurno();
                estadoEntrada = Estado.MENU;
                break;
            case "3":{
                estadoEntrada = Estado.CAMBIAR_FICHAS;
                mostrarMenuCambioFicha();
                break;
            }
        }
    }

    private void mostrarMenuCambioFicha() {
        println("\n1 - Seleccionar ficha para cambiar\n");
        println("2 - Cambiar Fichas Seleccionadas\n");
       // println("Fichas seleccionadas " + fichasCambio);
    }

    private void procesarCambioFicha(String entrada) throws RemoteException {
        switch (entrada){
            case "1"->{
                println("Ingrese ficha a cambiar: ");
                estadoEntrada = Estado.CAMBIANDO_FICHAS;
            }
            case "2"->{
                if (fichasCambio.isEmpty()) {
                    println("\nNo ha seleccionado fichas para cambiar.");
                } else {
                    if(!controlador.cambiarFichas(fichasCambio)){
                        mostrarMenu();
                        estadoEntrada = Estado.MENU;
                    }
                }
            }
        }
    }

    private void procesarCambiandoFicha(String entrada) throws RemoteException {
        if (entrada.length() == 1) {
            fichaSeleccionada = controlador.obtenerFicha(entrada.charAt(0));
            if (fichaSeleccionada != null) {
                fichasCambio.add(fichaSeleccionada);
            } else {
                println("\n Ficha no encontrada en el atril.");
            }
        } else {
            println("\n Ingrese solo una letra.");
        }
        mostrarMenuCambioFicha();
        estadoEntrada = Estado.CAMBIAR_FICHAS;
    }

    @Override
    public void mostrarMensaje(String texto) {
        if(textMenu != null) {
            textMenu.append("\n" + texto + "\n");
        }
    }

    private void println(String texto) {
        textMenu.append(texto);
        textMenu.setCaretPosition(textMenu.getDocument().getLength());
    }

    @Override
    public void mostrarTablero(Celda[][] celdas) {
        StringBuilder mensaje = new StringBuilder();
        int filas = celdas.length;
        int columnas = celdas[0].length;
        tablero.setText("");

        mensaje.append("     ");
        for (int c = 0; c < columnas; c++) {
            mensaje.append(String.format("  %-6d", c));
        }
        mensaje.append("\n");
        mensaje.append("   ┌");
        mensaje.append("────────".repeat(columnas));
        mensaje.append("┐\n");


        for (int i = 0; i < celdas.length; i++) {
            mensaje.append(String.format("%2d |", i));
            for (int j = 0; j < celdas[i].length; j++) {
                Celda celda = celdas[i][j];
                String contenido;
                if (celda.getFicha() != null) {
                    contenido = "   " + celda.getFicha().getLetra() + "   ";// Letra de la ficha
                } else {
                    switch (celda.getBonificacion()) {
                        case TRIPLE_PALABRA -> contenido = "  3 W  ";
                        case DOBLE_PALABRA -> contenido = "  2 W  ";
                        case TRIPLE_LETRA  -> contenido = "  3 L  ";
                        case DOBLE_LETRA  -> contenido = "  2 L  ";
                        default            -> contenido = "  - -  ";
                    }
                }
                mensaje.append(contenido);
                mensaje.append("|");
            }
            mensaje.append("\n");
            if (i < filas - 1) {
                mensaje.append("   |");
                mensaje.append("────────".repeat(columnas));
                mensaje.append("|\n");
            }
        }
        mensaje.append("   └");
        mensaje.append("────────".repeat(columnas));
        mensaje.append("┘\n");
        tablero.setFont(new Font("Consolas", Font.PLAIN, 13));
        tablero.setText(mensaje.toString());
    }

    @Override
    public void mostrarAtril() throws RemoteException {
        estadoEntrada = Estado.MENU_JUGADOR;
        mostrarMenuJugador();
        ArrayList<Ficha> fichas = controlador.obtenerFichasAtril(this.jugadorLocal);
        StringBuilder atrilTexto = new StringBuilder("Atril: ");

        for (Ficha ficha : fichas) {
            atrilTexto.append("| ").append(ficha.getLetra()).append(" ");
        }
        atril.setText(atrilTexto.toString());
    }

    public void mostrarPuntajes(ArrayList<Jugador> jugadores) {
        StringBuilder sb = new StringBuilder();
        int ancho = 45;

        sb.append("═".repeat(ancho)).append("\n");
        sb.append("║             PUNTAJES DE JUGADORES             ║\n");
        sb.append("═".repeat(ancho)).append("\n");

        for (Jugador jugador : jugadores) {
            sb.append(String.format("%-10s: %3d puntos\n", jugador.getNombre(), jugador.getPuntos()));
        }

        sb.append("═".repeat(ancho)).append("\n");
        puntajes.setText(sb.toString()); // Reemplaza el contenido
        puntajes.setCaretPosition(0); // Opcional: mostrar desde arriba
    }

    public void mostrarFinDePartida(Jugador ganador, ArrayList<Jugador> jugadores) {
        txtInput.setEnabled(false);
        tablero.setText("═════════════════════════════════════════\n");
        tablero.append("¡La partida ha terminado!\n");
        tablero.append("El ganador es: " + ganador.getNombre() + " con " + ganador.getPuntos() + " puntos.\n");
        tablero.append("═════════════════════════════════════════\n\n");

        tablero.append("🎯 RESULTADOS FINALES 🎯\n");
        for (Jugador jugador : jugadores) {
            tablero.append("👤 " + jugador.getNombre() + " - " + jugador.getPuntos() + " puntos\n");
        }

        tablero.append("\nGracias por jugar 🎉");
    }

    @Override
    public void deshabilitarInteraccion() {
        txtInput.setEnabled(false);
    }


    @Override
    public void iniciarVista(String nombre) throws RemoteException {
        controlador.agregarJugador(nombre);
        setVisible(true);
    }


    @Override
    public String getJugadorLocal() {
        return jugadorLocal;
    }

    @Override
    public void limpiar() throws RemoteException {
        posicionesColocadas.clear();
        fichasCambio.clear();
    }

    @Override
    public void pedirFicha(PosicionCelda posicion) throws RemoteException {
        println("ingrese letra comodin (A-Z): ");
        estadoEntrada = Estado.FICHA_COMODIN;
        this.posicionComodin = posicion;
    }

    private void procesarIngresoFichaComodin(String entrada) throws RemoteException {
        if (entrada.length() == 1 &&  Character.isLetter(entrada.charAt(0))) {
            controlador.colocarFichaComodinEnTablero(fichaSeleccionada, Character.toUpperCase(entrada.charAt(0)), posicionComodin);
        } else {
            mostrarMensaje("Letra inválida. Ingrese una sola letra (A-Z).");
        }
    }

    public void mostrarTurno() throws RemoteException {
        if(turnoActual()){
            println("\nes tu turno " + controlador.obtenerTurnoActual() + "\n");
            mostrarMenu();
        }else{
            println("\nes el turno de " +  controlador.obtenerTurnoActual() + "\n");
        }
    }

    public boolean turnoActual() throws RemoteException {
        String jugadorTurno = controlador.obtenerTurnoActual();
        return jugadorTurno.equals(jugadorLocal);
    }
}
