package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.CONTROLADOR.ScrabbleControlador;
import ar.edu.unlu.poo.MODELO.Celda;
import ar.edu.unlu.poo.MODELO.Ficha;
import ar.edu.unlu.poo.MODELO.Jugador;
import ar.edu.unlu.poo.MODELO.PosicionCelda;

import javax.management.StringValueExp;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Objects;

public class VistaGrafica extends JFrame implements IVista {
    private ScrabbleControlador controlador;
    private String jugadorLocal;
    private JPanel tablero, atril;
    private JButton[] botonesTablero, botonesAtril;
    private Ficha fichaSeleccionada;
    private boolean seleccionFicha = false;
    private ArrayList<Ficha> fichasSeleccionadasAtril;
    private JTextArea textMensajes;
    private JPanel botones;
    private JLabel[] etiquetasPuntajes;
    private ArrayList<PosicionCelda> posicionesColocadas = new ArrayList<>();

    public VistaGrafica(ScrabbleControlador controlador) {
        this.controlador = controlador;
        controlador.setVista(this);
        setTitle("Scrabble Game!");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        inicializarTablero();
        inicializarAtril();

        textMensajes = new JTextArea(13, 33);
        textMensajes.setEditable(false);
        textMensajes.setLineWrap(true); // Ajustar texto automáticamente a la línea
        JScrollPane scrollPane = new JScrollPane(textMensajes);
        scrollPane.setPreferredSize(new Dimension(330, 0)); // Fijamos ancho del panel
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setPreferredSize(new Dimension(400, 600)); // Tamaño del panel lateral
        panelDerecho.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panelDerecho.add(scrollPane, BorderLayout.CENTER);
        obtenerPuntajes(panelDerecho);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(atril, BorderLayout.WEST);
        //inicializarBotones(panelInferior);

        add(tablero, BorderLayout.CENTER);
      //  add(scrollPane, BorderLayout.EAST);
        add(panelInferior, BorderLayout.SOUTH);
        add(panelDerecho, BorderLayout.EAST);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    controlador.guardarPartida();
                    println("ya no se puede seguir jugando esta partida, la partida ha sido guardada");
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void inicializarTablero() {
        tablero = new JPanel(new GridLayout(15, 15));
       // tablero.setPreferredSize(new Dimension(475,475));
        botonesTablero = new JButton[225];
        for (int i = 0; i < 225; i++) {
            JButton celda = new JButton();
            celda.setPreferredSize(new Dimension(45, 45));
            celda.setBackground(Color.LIGHT_GRAY);
            celda.setOpaque(true);
            celda.setBorder(BorderFactory.createLineBorder(Color.black));

            celda.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    JButton celdaSeleccionada = (JButton) e.getSource();
                    if (fichaSeleccionada == null) {
                        return; // No hacer nada si no hay ficha seleccionada o la celda ya tiene ficha
                    }
                    // Obtener fila y columna
                    int fila = tablero.getComponentZOrder(celdaSeleccionada) / 15;
                    int columna = tablero.getComponentZOrder(celdaSeleccionada) % 15;
                    PosicionCelda posicion = new PosicionCelda(fila, columna);

                    try {
                        if(controlador.celdaValida(posicion)) {
                            posicionesColocadas.add(posicion);
                            celdaSeleccionada.setText(String.valueOf(fichaSeleccionada.getLetra()));
                            celdaSeleccionada.setBackground(Color.WHITE);
                            try {
                                controlador.colocarFichaSeleccionadaEnTablero(fichaSeleccionada, posicion);
                                sacarFichaDelAtril(fichaSeleccionada);
                            } catch (RemoteException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    // Limpiar selección
                    fichaSeleccionada = null;
                    setCursor(Cursor.getDefaultCursor());
                }
            });

            botonesTablero[i] = celda;
            tablero.add(celda);
        }
    }

    private void sacarFichaDelAtril(Ficha ficha) {
        for (int i = 0; i < botonesAtril.length; i++) {
            Ficha fichaBoton = (Ficha) botonesAtril[i].getClientProperty("ficha");
            if (fichaBoton != null && fichaBoton == ficha){
                botonesAtril[i].putClientProperty("ficha", null);
                botonesAtril[i].setIcon(null);  // Limpiar el botón
                botonesAtril[i].setBackground(null);
                //botonesAtril[i].setEnabled(false); // Desactivar el botón
                break;
            }
        }
    }

    private void inicializarAtril() {
        atril = new JPanel(new BorderLayout());
        atril.setPreferredSize(new Dimension(800, 50));
        botonesAtril = new JButton[7];
        fichaSeleccionada = null;
        fichasSeleccionadasAtril = new ArrayList<>();
        JPanel panelFichas = new JPanel(new FlowLayout());
        DragSource dragSource = new DragSource();
        for (int i = 0; i < botonesAtril.length; i++) {
            JButton botonFicha = new JButton();
            botonFicha.setPreferredSize(new Dimension(45, 45));
            botonFicha.setEnabled(false);
            panelFichas.add(botonFicha);

            botonesAtril[i] = botonFicha;
            //atril.add(botonFicha);
            botonFicha.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JButton boton = (JButton) e.getSource();
                    try {
                        if (boton.getClientProperty("ficha") != null && turnoActual()) {
                            fichaSeleccionada = (Ficha)  boton.getClientProperty("ficha");//controlador.obtenerFicha(boton.getText().charAt(0)); // Guardar ficha seleccionada
                            boton.putClientProperty("ficha", null);
                            if(seleccionFicha) {
                                if (fichasSeleccionadasAtril.contains(fichaSeleccionada)) {
                                    fichasSeleccionadasAtril.remove(fichaSeleccionada);
                                    boton.setBackground(null); // Restaurar color original
                                } else {
                                    fichasSeleccionadasAtril.add(fichaSeleccionada);
                                    boton.setBackground(Color.RED); // Marcar como seleccionada
                                }
                            }else {
                                // System.out.println("Ficha seleccionada: " + fichaSeleccionada.getLetra());
                                // Cambiar cursor para mostrar la letra de la ficha
                                Toolkit toolkit = Toolkit.getDefaultToolkit();
                                Image img = crearImagenConTexto(fichaSeleccionada.getLetra(), boton.getSize());
                                Cursor cursor = toolkit.createCustomCursor(img, new Point(0, 0), "ficha");
                                setCursor(cursor);
                            }
                        }
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            atril.add(panelFichas, BorderLayout.CENTER);
             inicializarBotones(atril);
        }
    }

    private Image crearImagenConTexto(char letra, Dimension size) {
        BufferedImage imagen = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imagen.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, size.width, size.height);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, size.height / 2));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (size.width - fm.stringWidth(String.valueOf(letra))) / 2;
        int y = (size.height + fm.getAscent()) / 2 - fm.getDescent();
        g2d.drawString(String.valueOf(letra), x, y);

        g2d.dispose();
        return imagen;
    }

    private void obtenerPuntajes(JPanel panelDerecho) {
        //setLayout(new GridLayout(4, 1, 4, 4)); // 2 filas, 2 columnas, espacio entre ellos
       // setPreferredSize(new Dimension(200, 100)); // Tamaño fijo del panel

        JPanel panelPuntajes = new JPanel(new GridLayout(2, 2, 5, 5)); // Panel interno
        panelPuntajes.setBackground(new Color(50, 50, 50)); // Color de fondo
        panelPuntajes.setOpaque(true);
        panelPuntajes.setBorder(BorderFactory.createTitledBorder("Puntajes"));


        etiquetasPuntajes = new JLabel[4]; // Hasta 4 jugadores

        for (int i = 0; i < 4; i++) {
            etiquetasPuntajes[i] = new JLabel("Jugador " + (i + 1) + ": 0 puntos", SwingConstants.CENTER);
            etiquetasPuntajes[i].setFont(new Font("Arial", Font.BOLD, 14));
            etiquetasPuntajes[i].setForeground(Color.WHITE);
            panelPuntajes.add(etiquetasPuntajes[i]); // Agrega las etiquetas al panel
        }

        //setBackground(new Color(50, 50, 50));
        panelDerecho.add(panelPuntajes, BorderLayout.SOUTH);
    }


    private void inicializarBotones(JPanel atril) {
        botones = new JPanel(new BorderLayout());
        botones.setPreferredSize(new Dimension(48, 48));

        ImageIcon iconoPasarTurno = imagenb("src/ar/edu/unlu/poo/RESOURCES/BOTONES/PASAR.png");
        ImageIcon iconoCambiarFicha = imagenb("src/ar/edu/unlu/poo/RESOURCES/BOTONES/CAMBIAR.png");
        ImageIcon iconoEnviarPalabra = imagenb("src/ar/edu/unlu/poo/RESOURCES/BOTONES/ENVIAR.png");


        JButton pasarTurno = new JButton("Pasar Turno", iconoPasarTurno);
        JButton cambiarFicha = new JButton("Cambiar Ficha", iconoCambiarFicha);
        JButton enviarPalabra = new JButton("Enviar Palabra", iconoEnviarPalabra);

        pasarTurno.setHorizontalTextPosition(SwingConstants.CENTER);
        pasarTurno.setVerticalTextPosition(SwingConstants.BOTTOM);

        cambiarFicha.setHorizontalTextPosition(SwingConstants.CENTER);
        cambiarFicha.setVerticalTextPosition(SwingConstants.BOTTOM);

        enviarPalabra.setHorizontalTextPosition(SwingConstants.CENTER);
        enviarPalabra.setVerticalTextPosition(SwingConstants.BOTTOM);

        pasarTurno.addActionListener(e -> {
            try {
                if (turnoActual()) {
                    fichaSeleccionada = null;
                    setCursor(Cursor.getDefaultCursor());
                    controlador.pasarTurno();
                } else {
                    println("espere su turno!");

                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        cambiarFicha.addActionListener(e -> {
            try {
                if (turnoActual()) {
                    fichaSeleccionada = null;
                    setCursor(Cursor.getDefaultCursor());
                    if(!seleccionFicha){
                        seleccionFicha = true;
                        println("seleccione fichas a cambiar y luego presione devuelta el boton!");
                    }else {
                        controlador.cambiarFichas(fichasSeleccionadasAtril);
                        fichasSeleccionadasAtril.clear();
                        seleccionFicha = false;
                    }
                } else {
                    println("espere su turno!");
                }
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        });
        enviarPalabra.addActionListener(e -> {
            try {
                if (turnoActual()) {
                    fichaSeleccionada = null;
                    setCursor(Cursor.getDefaultCursor());
                    controlador.enviarPalabra(posicionesColocadas);
                } else {
                    println("espere su turno!");
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        JPanel panelIzquiero = new JPanel(new FlowLayout());
        panelIzquiero.add(pasarTurno);
        JPanel panelDerecho = new JPanel(new FlowLayout());
        panelDerecho.add(cambiarFicha);
        panelDerecho.add(enviarPalabra);
        atril.add(panelIzquiero, BorderLayout.WEST);
        atril.add(panelDerecho, BorderLayout.EAST);
    }

    private ImageIcon imagenb(String path) {
        ImageIcon icono = new ImageIcon(path);
        Image imagenOriginal = icono.getImage();

        // Asegurar una escala óptima basada en el tamaño de la celda del tablero
        int ancho = 95; // Ajusta el tamaño según el botón
        int alto = 60;  // Ajusta el tamaño según el botón

        Image imagenEscalada = imagenOriginal.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);

    }

    public void mostrarTablero(Celda[][] celdas) {
        tablero.removeAll();  // Limpiar el panel para actualizar el tablero

        for (int i = 0; i < celdas.length; i++) {
            for (int j = 0; j < celdas[i].length; j++) {
                int indice = i * celdas.length + j;
                Celda celda = celdas[i][j];
                JButton boton = botonesTablero[indice];
                boton.setPreferredSize(new Dimension(40, 40));
                boton.setEnabled(true);
                boton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));


                if (celda.getFicha() != null) {
                    boton.setIcon(cargarImagenFicha(celda.getFicha().getLetra()));
                } else {

                    switch (celda.getBonificacion()) {
                        case DOBLE_PALABRA -> boton.setIcon(imagen("src/ar/edu/unlu/poo/RESOURCES/BONIFICACIONES/DOBLE_PALABRA.png"));
                        case TRIPLE_PALABRA -> boton.setIcon(imagen("src/ar/edu/unlu/poo/RESOURCES/BONIFICACIONES/TRIPLE_PALABRA.png"));
                        case DOBLE_LETRA -> boton.setIcon(imagen("src/ar/edu/unlu/poo/RESOURCES/BONIFICACIONES/DOBLE_LETRA.png"));
                        case TRIPLE_LETRA -> boton.setIcon(imagen("src/ar/edu/unlu/poo/RESOURCES/BONIFICACIONES/TRIPLE_LETRA.png"));
                        case CENTRO -> boton.setIcon(imagen("src/ar/edu/unlu/poo/RESOURCES/BONIFICACIONES/CENTRO.png"));
                        default -> boton.setIcon(imagen("src/ar/edu/unlu/poo/RESOURCES/BONIFICACIONES/NORMAL.png"));
                    }
                }
                boton.setOpaque(true);
                tablero.add(boton);
            }
        }
        tablero.revalidate();  // Asegura que los cambios en el panel sean reflejados
        tablero.repaint();     // Redibuja el panel con los nuevos cambios
    }

    private ImageIcon imagen(String path) {
        ImageIcon icono = new ImageIcon(path);
        Image imagenOriginal = icono.getImage();

        // Asegurar una escala óptima basada en el tamaño de la celda del tablero
        int ancho = 55; // Ajusta el tamaño según el botón
        int alto = 55;  // Ajusta el tamaño según el botón

        Image imagenEscalada = imagenOriginal.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);

    }

    public boolean turnoActual() throws RemoteException {
        String jugadorTurno = controlador.obtenerTurnoActual();
        if (jugadorTurno == null) return false;
        return jugadorTurno.equals(jugadorLocal);
    }

    public String getJugadorLocal() {
        return this.jugadorLocal;
    }

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

    public void println(String texto) {
        textMensajes.append("\n" + texto + "\n");
    }

    public void mostrarMensaje(String texto) {
        textMensajes.append("\n" + texto + "\n");
    }

    public void mostrarAtril() throws RemoteException {
        ArrayList<Ficha> fichasAtril = controlador.obtenerFichasAtril(jugadorLocal);
        if (fichasAtril == null) {
            System.err.println("Error: fichasAtril es null.");
            return; // Salir del método para evitar el error.
        }
        for (int i = 0; i < botonesAtril.length; i++) {
            if (i < fichasAtril.size()) {
                Ficha ficha = fichasAtril.get(i);
                botonesAtril[i].putClientProperty("ficha", ficha);
                botonesAtril[i].setIcon(cargarImagenFicha(ficha.getLetra()));
                botonesAtril[i].setEnabled(true);
                botonesAtril[i].setBackground(null);
                botonesAtril[i].setText("");
            } else {
                System.out.println("rfeg");
            }
        }
    }

    @Override
    public void actualizarAtril(Jugador jugador) throws RemoteException {
        ArrayList<Ficha> fichasAtril = controlador.obtenerFichasAtril(jugadorLocal);
        for (int i = 0; i < botonesAtril.length; i++) {
            if (i < fichasAtril.size()) {
                botonesAtril[i].setBackground(null);
            }
        }
    }

    public void limpiar(){
        posicionesColocadas.clear();
    }

    private ImageIcon cargarImagenFicha(char letra) {
        String ruta = "src/ar/edu/unlu/poo/RESOURCES/LETRAS/" + Character.toUpperCase(letra) + ".jpg"; // Ajusta la ruta
        ImageIcon icono = new ImageIcon(ruta);
        Image imagenEscalada = icono.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        return new ImageIcon(imagenEscalada);
    }

    public void mostrarTurno() throws RemoteException {
        if(turnoActual()){
            mostrarMensaje("es tu turno " + controlador.obtenerTurnoActual());
        }else{
            mostrarMensaje("es el turno de " + controlador.obtenerTurnoActual());
        }
    }
}
