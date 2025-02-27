package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.CONTROLADOR.ScrabbleControlador;
import ar.edu.unlu.poo.MODELO.Celda;
import ar.edu.unlu.poo.MODELO.Ficha;
import ar.edu.unlu.poo.MODELO.Jugador;
import ar.edu.unlu.poo.MODELO.PosicionCelda;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class VistaConsola extends JFrame implements IVista {
    private ScrabbleControlador controlador;
    private String jugadorLocal;
    private JTextArea textMenu;
    private JTextField txtInput;
    private JPanel lblAtrilTablero;
    private JLabel atril;
    private JTextArea tablero;
    private ArrayList<PosicionCelda> posicionesColocadas = new ArrayList<>();
    private Estado estadoEntrada;
    private Ficha fichaSeleccionada;
    private ArrayList<Ficha> fichasCambio = new ArrayList<>();

    public VistaConsola(ScrabbleControlador controlador) {
        this.controlador = controlador;
        controlador.setVista(this);
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

        textMenu = new JTextArea(10,20);
        textMenu.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        textMenu.setEditable(false);
        textMenu.setLineWrap(true);
        JScrollPane scrollMenu = new JScrollPane(textMenu);
        scrollMenu.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

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

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.add(scrollMenu, BorderLayout.CENTER);
        panelDerecho.add(txtInput, BorderLayout.SOUTH);

        add(lblAtrilTablero, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.WEST);
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
        }
    }

    private void mostrarMenuJugador() {
        textMenu.append("\n1 - Colocar Ficha\n");
        textMenu.append("2 - Enviar Palabra\n");
        textMenu.append("\nIngrese una opción: ");
    }

    private void procesarMenuJugador(String entrada) throws IOException {
        switch (entrada){
            case "1":{
                textMenu.append("\nIngrese ficha del atril: ");
                estadoEntrada = Estado.INGRESAR_FICHA;
                break;
            }
            case "2": {
                controlador.enviarPalabra(posicionesColocadas);
                break;
            }
            case "3":
        };
    }

    private void procesarIngresoFicha(String entrada) throws RemoteException {
        if (entrada.length() == 1) {
            fichaSeleccionada = controlador.obtenerFicha(entrada.charAt(0));

            if (fichaSeleccionada != null) {
                textMenu.append("\nIngrese fila: ");
                estadoEntrada = Estado.INGRESAR_POSICION;
            } else {
                textMenu.append("\nFicha no encontrada. Intente nuevamente: ");
            }
        } else {
            textMenu.append("\nDebe ingresar una sola letra: ");
        }
    }

    private void procesarIngresoPosicion(String entrada){
        String[] partes = entrada.split(",");
        if (partes.length == 2) {
            try {
                int fila = Integer.parseInt(partes[0].trim());
                int columna = Integer.parseInt(partes[1].trim());

                PosicionCelda posicion = new PosicionCelda(fila, columna);
                posicionesColocadas.add(posicion);
                controlador.colocarFichaSeleccionadaEnTablero(fichaSeleccionada,posicion);
                textMenu.append("\nFicha colocada en (" + fila + "," + columna + ")\n");
                estadoEntrada = Estado.MENU_JUGADOR;
                mostrarMenuJugador();
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        } else {
            textMenu.append("\nFormato incorrecto. Use: fila,columna\n");
        }
    }

    private void mostrarMenu() {
        estadoEntrada = Estado.MENU;
        textMenu.append("1 - Colocar palabra\n");
        textMenu.append("2 - Pasar turno\n");
        textMenu.append("3 - Cambiar Fichas\n");
        textMenu.append("\nIngrese una opción: ");
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
                mostrarMenu();
                break;
            case "3":{
                estadoEntrada = Estado.CAMBIAR_FICHAS;
                mostrarMenuCambioFicha();
                break;
            }
        }
    }

    private void mostrarMenuCambioFicha() {
        textMenu.append("\n1 - Seleccionar ficha para cambiar\n");
        textMenu.append("2 - Cambiar Fichas Seleccionadas\n");
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
                    println("\n⚠️ No ha seleccionado fichas para cambiar.");
                } else {
                    controlador.cambiarFichas(fichasCambio); // Enviar fichas al controlador
                    println("\n✅ Se han cambiado las fichas: " + fichasCambio);
                    fichasCambio.clear(); // Limpiar la lista después del cambio
                }
                mostrarMenu();
            }
        }
    }

    private void procesarCambiandoFicha(String entrada) throws RemoteException {
        if (entrada.length() == 1) {
            fichaSeleccionada = controlador.obtenerFicha(entrada.charAt(0));
            //System.out.println("Intentando obtener ficha: " + entrada);
            if (fichaSeleccionada != null) {
                fichasCambio.add(fichaSeleccionada);
                System.out.println("Intentando obtener ficha: " + entrada);
            } else {
                println("\n Ficha no encontrada en el atril.");
            }
        } else {
            println("\n Ingrese solo una letra.");
        }
        mostrarMenuCambioFicha();  // Actualizar el menú
        estadoEntrada = Estado.CAMBIAR_FICHAS;
    }

    @Override
    public void mostrarMensaje(String texto) {
        textMenu.append("\n" + texto + "\n");
    }

    private void println(String texto) {
        textMenu.append(texto + "\n");
    }

    @Override
    public void actualizarAtril(Jugador jugador) throws RemoteException {

    }

    @Override
    public void mostrarTablero(Celda[][] celdas) {
        StringBuilder mensaje = new StringBuilder();
        int filas = celdas.length;
        int columnas = celdas[0].length;
        tablero.setText("");

        //mensaje.append("    ");
        for (int c = 0; c < columnas; c++) {
            mensaje.append(String.format("\t%2d ", c));
        }
        mensaje.append("\n   ┌");
        mensaje.append("───────────".repeat(columnas));
        mensaje.append("┐\n");

        for (int i = 0; i < celdas.length; i++) {
            mensaje.append(String.format("%2d │  ", i));
            for (int j = 0; j < celdas[i].length; j++) {
                Celda celda = celdas[i][j];
                if (celda.getFicha() != null) {
                    mensaje.append(celda.getFicha().getLetra()).append("   "); // Letra de la ficha
                } else {
                    switch (celda.getBonificacion()) {
                        case TRIPLE_PALABRA -> mensaje.append("3 W "); // Triple Word
                        case DOBLE_PALABRA -> mensaje.append("2 W "); // Double Word
                        case TRIPLE_LETRA -> mensaje.append("3 L "); // Triple Letter
                        case DOBLE_LETRA -> mensaje.append("2 L "); // Double Letter
                        default -> mensaje.append("- - "); // Celda normal
                    }
                }
                mensaje.append(" │   ");
            }
            mensaje.append("\n   ├");
            mensaje.append("─────────".repeat(columnas));
            mensaje.append("─┤\n");
        }
        mensaje.append("   └");
        mensaje.append("──────────".repeat(columnas));
        mensaje.append("┘\n");
        tablero.setText(mensaje.toString());
    }


    @Override
    public void mostrarAtril() throws RemoteException {
        ArrayList<Ficha> fichas = controlador.obtenerFichasAtril(this.jugadorLocal);
        StringBuilder atrilTexto = new StringBuilder("Atril: ");

        for (Ficha ficha : fichas) {
            atrilTexto.append("| ").append(ficha.getLetra()).append(" ");
        }
        atril.setText(atrilTexto.toString());
    }

    @Override
    public void iniciarVista() throws RemoteException {
        String nombreJugador = ingresarNombre();
        this.jugadorLocal = nombreJugador;
        controlador.agregarJugador(nombreJugador);
        setVisible(true);
    }
    public String ingresarNombre() throws RemoteException {
        String nombre;
        do {
            nombre = JOptionPane.showInputDialog(this, "ingrese nombre", "unirse a scrabble", JOptionPane.PLAIN_MESSAGE);

            if (nombre == null || nombre.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un nombre para unirse a la partida.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (controlador.existeNombre(nombre)) {
                JOptionPane.showMessageDialog(this, "El nombre ya existe", "Error", JOptionPane.ERROR_MESSAGE);
                nombre = null; // Forzar repetir el bucle
            }
        } while (nombre == null);

        return nombre;
    }

    @Override
    public String getJugadorLocal() {
        return jugadorLocal;
    }

    @Override
    public void limpiar() {
        posicionesColocadas.clear();

    }

    public void mostrarTurno() throws RemoteException {
        if(turnoActual()){
            mostrarMensaje("es tu turno " + controlador.obtenerTurnoActual());
            mostrarMenu();
        }else{
            mostrarMensaje("es el turno de " +  controlador.obtenerTurnoActual());
        }
    }

    public boolean turnoActual() throws RemoteException {
        String jugadorTurno = controlador.obtenerTurnoActual();
        return jugadorTurno.equals(jugadorLocal);
    }


}
