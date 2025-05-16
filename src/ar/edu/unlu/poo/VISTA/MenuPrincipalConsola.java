package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.CONTROLADOR.ScrabbleControlador;
import ar.edu.unlu.poo.MODELO.Jugador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class MenuPrincipalConsola extends JFrame {
    private ScrabbleControlador controlador;
    private VistaConsola vista;
    private String nombreJugador;
    private JTextField txtInput;
    private JTextArea txtSalida;

    public MenuPrincipalConsola(VistaConsola vista, ScrabbleControlador controlador, String nombre) {
        this.controlador = controlador;
        this.vista = vista;
        this.nombreJugador = nombre;
        setTitle("Menú Scrabble");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        txtSalida = new JTextArea();
        txtSalida.setEditable(false);
        txtSalida.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));
        txtSalida.setBackground(Color.BLACK);
        txtSalida.setForeground(Color.WHITE);
        txtSalida.setLineWrap(true);
        txtSalida.setWrapStyleWord(true);

        txtInput = new JTextField();
        txtInput.setBackground(Color.BLACK);
        txtInput.setForeground(Color.WHITE);
        txtInput.setCaretColor(Color.WHITE);
        txtInput.setFont(new Font("JetBrains Mono", Font.PLAIN, 12));

        txtInput = new JTextField();
        txtInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    procesarEntrada(txtInput.getText().trim());
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
                txtInput.setText("");
            }
        });
        setLayout(new BorderLayout());
        add(new JScrollPane(txtSalida), BorderLayout.CENTER);
        add(txtInput, BorderLayout.SOUTH);
        mostrarMenu();
        setVisible(true);
    }

    private void mostrarMenu(){
        txtSalida.setText(""); // Limpiar salida antes de mostrar
        txtSalida.append("========== SCRABBLE ==========\n");
        txtSalida.append("1. Cargar Partida\n");
        txtSalida.append("2. Unirse a Partida\n");
        txtSalida.append("3. Ranking\n");
        txtSalida.append("4. Salir\n");
        txtSalida.append("Seleccione una opción: ");
    }

    private void procesarEntrada(String opcion) throws RemoteException {
        if (txtSalida.getText().contains("Ranking de Jugadores")) {
            // Estamos viendo el ranking
            // Si el usuario presiona Enter sin escribir nada, volvemos al menú
            if (opcion.isEmpty()) {
                mostrarMenu();
            } else {
                txtSalida.append("\n→ Presione Enter para volver al menú.");
            }
            return;
        }
        switch (opcion) {
            case "1" -> unirsePartida();
            case "2" -> cargarPartida();
            case "3" -> mostrarRanking();
            case "4" -> System.exit(0);
            default -> txtSalida.append("\n→ Opción inválida. Intente de nuevo.\n");
        }
    }

    private void unirsePartida() throws RemoteException {
        vista.iniciarJuego();
        vista.iniciarVista(nombreJugador);
    }

    private void cargarPartida() {
        new VentanaCargarPartidaConsola(vista, nombreJugador);
    }

    private void mostrarRanking() throws RemoteException {
        ArrayList<Jugador> jugadores = controlador.obtenerRanking();
        txtSalida.setText("");
        txtSalida.append("--- 🏆 Ranking de Jugadores ---\n");
        int posicion = 1;
        for (Jugador jugador : jugadores) {
            txtSalida.append(posicion + ". " + jugador.getNombre() + " - Puntaje: " + jugador.getPuntos() + "\n");
            posicion++;
        }
        txtSalida.append("\nPresione Enter para volver al menú principal.");

    }

}
