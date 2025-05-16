package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.CONTROLADOR.ScrabbleControlador;
import ar.edu.unlu.poo.MODELO.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class VistaGraficaa extends JFrame implements IVista {
    private ScrabbleControlador controlador;
    private String jugadorLocal;
    private JPanel tablero, atril, panelDerecho;
    private JPanel[] panelesAtril;
    private JPanel[][] celdasTablero;
    private Ficha fichaSeleccionada;
    private boolean seleccionFicha = false;
    private ArrayList<Ficha> fichasSeleccionadasAtril;
    private JTextArea textMensajes;
    private JPanel botones;
    private JLabel[] etiquetasPuntajes;
    private ArrayList<PosicionCelda> posicionesColocadas = new ArrayList<>();
    private GestorImagenes gestorImagenes;
    private JButton pasarTurno, cambiarFicha, enviarPalabra;

    public VistaGraficaa(ScrabbleControlador controlador, String nombre) throws IOException {
        this.controlador = controlador;
        this.jugadorLocal = nombre;
        this.gestorImagenes = new GestorImagenes();
        controlador.setVista(this);
        new MenuPrincipal(this, controlador, jugadorLocal);
    }

    public void iniciarJuego() throws RemoteException {
        setTitle("Scrabble Game!");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        inicializarTablero();
        inicializarAtril();

        textMensajes = new JTextArea(13, 38);
        textMensajes.setEditable(false);
        textMensajes.setLineWrap(true); // Ajustar texto automáticamente a la línea
        JScrollPane scrollPane = new JScrollPane(textMensajes);
        scrollPane.setPreferredSize(new Dimension(280, 0)); // Fijamos ancho del panel
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setPreferredSize(new Dimension(300, 600)); // Tamaño del panel lateral
        panelDerecho.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        panelDerecho.add(scrollPane, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(atril, BorderLayout.WEST);

        add(tablero, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
        add(panelDerecho, BorderLayout.EAST);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    controlador.guardarPartida();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        iniciarVista(jugadorLocal);
    }

    private void inicializarTablero() {
        tablero = new JPanel(new GridLayout(15, 15,0,0));
        tablero.setPreferredSize(new Dimension(525, 525));
        celdasTablero = new JPanel[15][15];

        for (int fila = 0; fila < 15; fila++) {
            for (int columna = 0; columna < 15; columna++) {
                JPanel celda = getjPanel(fila, columna);
                celda.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                        BorderFactory.createEmptyBorder(0, -10, 0, -10)
                ));

                int finalFila = fila;
                int finalColumna = columna;
                celda.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (fichaSeleccionada == null) {
                            return;
                        }

                        PosicionCelda posicion = new PosicionCelda(finalFila, finalColumna);
                        try {
                            if (controlador.celdaValida(posicion)) {
                                posicionesColocadas.add(posicion);
                                sacarFichaDelAtril(fichaSeleccionada);
                                controlador.colocarFichaSeleccionadaEnTablero(fichaSeleccionada, posicion);
                                // ...
                            } else {
                            }
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                        fichaSeleccionada = null;
                        setCursor(Cursor.getDefaultCursor());
                    }


                    public void mouseEntered(MouseEvent e) {
                        celda.setBackground(Color.YELLOW);  // Resalta la celda al pasar el ratón
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        BonificacionTablero bonificacion = (BonificacionTablero) celda.getClientProperty("bonificacion");
                        if (bonificacion != null) {
                            celda.setBackground(new Color(220, 220, 220));  // Color original de bonificaciones
                        } else {
                            celda.setBackground((finalFila + finalColumna) % 2 == 0 ? new Color(235, 235, 210) : new Color(220, 220, 200));
                        }
                    }
                });

                celdasTablero[fila][columna] = celda;
                tablero.add(celda);
            }
        }
    }

    private JPanel getjPanel(int fila, int columna) {
        JPanel celda = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.LIGHT_GRAY);
            }
        };

        celda.setPreferredSize(new Dimension(35, 35));
        celda.setLayout(new BorderLayout());
        return celda;
    }

    public void mostrarTablero(Celda[][] celdas) {
        for (int fila = 0; fila < celdas.length; fila++) {
            for (int columna = 0; columna < celdas[fila].length; columna++) {
                Celda celdaModelo = celdas[fila][columna];
                JPanel celdaVista = celdasTablero[fila][columna];
                celdaVista.removeAll();

                if (celdaModelo.getFicha() != null) {
                    JLabel etiquetaFicha;
                    if(celdaModelo.getFicha().getPuntos() != 0) {
                        etiquetaFicha = new JLabel(gestorImagenes.cargarImagenFicha(celdaModelo.getFicha().getLetra()));
                    }else{
                        etiquetaFicha = new JLabel(gestorImagenes.cargarImagenFichaComodin(celdaModelo.getFicha().getLetra()));
                    }
                    etiquetaFicha.setHorizontalAlignment(SwingConstants.CENTER);
                    etiquetaFicha.setAlignmentX(0.5f);
                    etiquetaFicha.setAlignmentY(0.5f);
                    celdaVista.add(etiquetaFicha);
                } else {
                    JLabel etiquetaFondo = new JLabel(gestorImagenes.obtenerImagenCeldaEspecial(celdaModelo.getBonificacion()));
                    etiquetaFondo.setHorizontalAlignment(SwingConstants.CENTER);
                    etiquetaFondo.setAlignmentX(0.5f);
                    etiquetaFondo.setAlignmentY(0.5f);
                    celdaVista.add(etiquetaFondo);
                }
                celdaVista.revalidate();
                celdaVista.repaint();
            }
        }
        tablero.revalidate();  // Asegura que los cambios en el panel sean reflejados
        tablero.repaint();
    }

    private void sacarFichaDelAtril(Ficha ficha) {
        for (JPanel panelFicha : panelesAtril) {
            Ficha fichaPanel = (Ficha) panelFicha.getClientProperty("ficha");
            if (fichaPanel == ficha) {
                panelFicha.putClientProperty("ficha", null);
                panelFicha.removeAll(); // Limpiar contenido del panel
                panelFicha.setBackground(null); // Restaurar el color de fondo
                panelFicha.setVisible(false); // Ocultar el panel
                panelFicha.revalidate();
                panelFicha.repaint();
                break;
            }
        }
    }

    private void inicializarAtril() {
        atril = new JPanel(new FlowLayout());
        atril.setPreferredSize(new Dimension(800, 70));
        JPanel panelFichas = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Panel exclusivo para las fichas
        panelesAtril = new JPanel[7];
        fichaSeleccionada = null;
        fichasSeleccionadasAtril = new ArrayList<>();
        for (int i = 0; i < panelesAtril.length; i++) {
            JPanel panelFicha = new JPanel();
            panelFicha.setPreferredSize(new Dimension(45, 45));
            panelFicha.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            panelFicha.setBackground(Color.LIGHT_GRAY);

            panelFicha.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    panelFicha.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));  // Iluminar celda
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    panelFicha.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));  // Restaurar celda
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    JPanel panel = (JPanel) e.getSource();
                    Ficha ficha = (Ficha) panel.getClientProperty("ficha");
                    try {
                        if (ficha != null && turnoActual()) {
                            fichaSeleccionada = ficha;
                            panel.putClientProperty("ficha", null);

                            if (seleccionFicha) {
                                if (fichasSeleccionadasAtril.contains(fichaSeleccionada)) {
                                    fichasSeleccionadasAtril.remove(fichaSeleccionada);
                                    panel.setBackground(Color.LIGHT_GRAY);
                                } else {
                                    fichasSeleccionadasAtril.add(fichaSeleccionada);
                                    panel.setBackground(Color.RED);
                                }
                            } else {
                                ImageIcon imagenFicha = gestorImagenes.cargarImagenFicha(ficha.getLetra());
                                Image img = imagenFicha.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
                                Cursor cursorFicha = Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(22, 22), "ficha");
                                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                setCursor(cursorFicha);
                            }
                        }
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            panelesAtril[i] = panelFicha;
            panelFichas.add(panelFicha);
        }
        atril.add(panelFichas); // Agregar panel de fichas al centro del atril
        inicializarBotones(atril);
    }

    private void inicializarBotones(JPanel atril) {
        botones = new JPanel(new FlowLayout());
        botones.setPreferredSize(new Dimension(48, 48));

        pasarTurno = new JButton("Pasar Turno");
        cambiarFicha = new JButton("Cambiar Ficha");
        enviarPalabra = new JButton("Enviar Palabra");

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
                    /*if(fichaSeleccionada != null){
                        println("No puedes cambiar fichas mientras tienes una seleccionada para colocar.");
                        return;
                    }*/
                    if(!seleccionFicha){
                        seleccionFicha = true;
                        println("seleccione fichas a cambiar y luego presione devuelta el boton!");
                    }else {
                        controlador.cambiarFichas(fichasSeleccionadasAtril);
                        for (JPanel panel : panelesAtril) {
                            panel.setBackground(Color.LIGHT_GRAY);
                        }
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

    public boolean turnoActual() throws RemoteException {
        String jugadorTurno = controlador.obtenerTurnoActual();
        if (jugadorTurno == null) return false;
        return jugadorTurno.equals(jugadorLocal);
    }

    public String getJugadorLocal() {
        return this.jugadorLocal;
    }

    public void iniciarVista(String nombreJugador) throws RemoteException {
        controlador.agregarJugador(nombreJugador);
        setVisible(true);
    }

    public void println(String texto) {
        textMensajes.append("\n" + texto + "\n");
    }

    public void mostrarMensaje(String texto) {
        if(textMensajes!=null) {
            textMensajes.append("\n" + texto + "\n");
        }
    }

    public void mostrarAtril() throws RemoteException {
        ArrayList<Ficha> fichasAtril = controlador.obtenerFichasAtril(jugadorLocal);
        if (fichasAtril == null) {
            return;
        }
        for (int i = 0; i < panelesAtril.length; i++) {
            JPanel panelFicha = panelesAtril[i];
            panelFicha.removeAll();
            if (i < fichasAtril.size()) {
                Ficha ficha = fichasAtril.get(i);
                panelFicha.putClientProperty("ficha", ficha);
                JLabel etiquetaFicha;
                etiquetaFicha = new JLabel(gestorImagenes.cargarImagenFicha(ficha.getLetra()));
                etiquetaFicha.setPreferredSize(new Dimension(45, 45));
                etiquetaFicha.setHorizontalAlignment(SwingConstants.CENTER);
                etiquetaFicha.setVerticalAlignment(SwingConstants.CENTER);

                panelFicha.setLayout(new BorderLayout());
                panelFicha.add(etiquetaFicha, BorderLayout.CENTER);

                panelFicha.setVisible(true);
            } else {
                System.out.println("rfeg");
            }
            panelFicha.revalidate();
            panelFicha.repaint();
        }
    }

    public void pedirFicha(PosicionCelda posicion) throws RemoteException {
        JPanel panel = new JPanel(new BorderLayout());
        JTextField campo = new JTextField(1);
        panel.add(new JLabel("Ingresá la letra para el comodín:"), BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);

        int resultado = JOptionPane.showConfirmDialog(
                null, panel, "Letra del Comodín", JOptionPane.OK_CANCEL_OPTION
        );

        if (resultado == JOptionPane.OK_OPTION) {
            String texto = campo.getText().toUpperCase();
            if (texto.length() == 1 && Character.isLetter(texto.charAt(0))) {
                controlador.colocarFichaComodinEnTablero(fichaSeleccionada, texto.charAt(0),posicion);
            } else {
                JOptionPane.showMessageDialog(null, "Ingresá solo una letra válida.");
            }
        }
    }

    @Override
    public void mostrarPuntajes(ArrayList<Jugador> jugador) {
        JPanel panelPuntajes = new JPanel(new GridLayout(2, 2, 5, 5)); // Panel interno
        panelPuntajes.setBackground(new Color(50, 50, 50)); // Color de fondo
        panelPuntajes.setOpaque(true);
        panelPuntajes.setBorder(BorderFactory.createTitledBorder("Puntajes"));
        etiquetasPuntajes = new JLabel[4]; // Hasta 4 jugadores

        for (int i = 0; i < jugador.size(); i++) {
            etiquetasPuntajes[i] = new JLabel("Jugador " + jugador.get(i).getNombre() + " " + jugador.get(i).getPuntos() + "puntos", SwingConstants.CENTER);
            etiquetasPuntajes[i].setFont(new Font("Arial", Font.BOLD, 14));
            etiquetasPuntajes[i].setForeground(Color.WHITE);
            panelPuntajes.add(etiquetasPuntajes[i]); // Agrega las etiquetas al panel
        }
        panelDerecho.add(panelPuntajes, BorderLayout.SOUTH);

    }

    @Override
    public void mostrarFinDePartida(Jugador ganador, ArrayList<Jugador> jugadores) {
        Image imagenFondo = new ImageIcon("src/ar/edu/unlu/poo/RESOURCES/fondo_fin_partida.jpeg").getImage(); // Usá el fondo que quieras

        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelFondo.setLayout(new GridBagLayout());
        panelFondo.setOpaque(false);

        JPanel panelFin = new JPanel();
        panelFin.setLayout(new BoxLayout(panelFin, BoxLayout.Y_AXIS));
        panelFin.setOpaque(false);

        JLabel lblTitulo = new JLabel("🏆 ¡Ganador: " + ganador.getNombre() + "!");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblTitulo.setForeground(new Color(0, 0, 0));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFin.add(lblTitulo);
        panelFin.add(Box.createVerticalStrut(30));

        JTextArea areaPuntajes = new JTextArea();
        areaPuntajes.setEditable(false);
        areaPuntajes.setFont(new Font("Monospaced", Font.PLAIN, 18));
        areaPuntajes.setOpaque(false);
        areaPuntajes.setForeground(Color.WHITE);
        areaPuntajes.setAlignmentX(Component.CENTER_ALIGNMENT);

        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════╗\n");
        sb.append("║        PUNTAJES FINALES        ║\n");
        sb.append("╚════════════════════════════════╝\n");
        for (Jugador jugador : jugadores) {
            sb.append(String.format(" %-15s : %3d puntos\n", jugador.getNombre(), jugador.getPuntos()));
        }
        areaPuntajes.setText(sb.toString());
        areaPuntajes.setForeground(new Color(0, 0, 0));

        panelFin.add(areaPuntajes);
        panelFin.add(Box.createVerticalStrut(30));

        JButton btnSalir = new JButton("Salir del juego");
        btnSalir.setFont(new Font("SansSerif", Font.PLAIN, 18));
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.addActionListener(e -> System.exit(0));
        panelFin.add(btnSalir);

        panelFondo.add(panelFin);
        this.getContentPane().removeAll();
        this.setContentPane(panelFondo);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void deshabilitarInteraccion() {
        pasarTurno.setEnabled(false);
        cambiarFicha.setEnabled(false);
        enviarPalabra.setEnabled(false);
        for (JPanel panel : panelesAtril) {
            panel.setEnabled(false);
        }
    }

    public void limpiar(){
        posicionesColocadas.clear();
    }

    public void mostrarTurno() throws RemoteException {
        if(turnoActual()){
            mostrarMensaje("es tu turno " + controlador.obtenerTurnoActual());
        }else{
            mostrarMensaje("es el turno de " + controlador.obtenerTurnoActual());
        }
    }
}
