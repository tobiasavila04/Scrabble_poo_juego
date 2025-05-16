package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.APPCliente;
import ar.edu.unlu.poo.APPServidor;
import ar.edu.unlu.poo.CONTROLADOR.ScrabbleControlador;
import ar.edu.unlu.poo.MODELO.Jugador;
import ar.edu.unlu.poo.MODELO.Partida;
import ar.edu.unlu.poo.MODELO.ScrabbleGame;
import ar.edu.unlu.poo.Serializador;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class VentanaCargarPartida extends JFrame {
    private JList<String> listaPartidas;
    private DefaultListModel<String> modeloLista;
    private Map<Integer, Partida> partidasGuardadas;
    private ArrayList<Integer> idsOrdenados;
    private String nombre;
    private IVista vista;

    public VentanaCargarPartida(IVista vista, String nombre) {
        setTitle("Cargar Partida");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.nombre = nombre;
        this.vista = vista;

        partidasGuardadas = Serializador.cargarPartidaGuardada();
        if (partidasGuardadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay partidas guardadas.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }
        modeloLista = new DefaultListModel<>();
        idsOrdenados = new ArrayList<>();
        for (Map.Entry<Integer, Partida> entrada : partidasGuardadas.entrySet()) {
            Integer id = entrada.getKey();
            Partida partida = entrada.getValue();
            String descripcion = "Partida #" + id + " - Jugadores: " + partida.getJugadores().size() + " - Turno: " + partida.getTurnoActual();
            modeloLista.addElement(descripcion);
            idsOrdenados.add(id);
        }

        listaPartidas = new JList<>(modeloLista);
        listaPartidas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaPartidas.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(listaPartidas);

        JButton botonCargar = new JButton("Cargar");
        botonCargar.addActionListener(e -> {
            try {
                cargarSeleccionada();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        JPanel panelInferior = new JPanel();
        panelInferior.add(botonCargar);

        add(scroll, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void cargarSeleccionada() throws IOException {
        int indice = listaPartidas.getSelectedIndex();
        if (indice != -1) {
            ArrayList<Integer> idsOrdenados = new ArrayList<>(partidasGuardadas.keySet());
            Integer idSeleccionado = idsOrdenados.get(indice);
            Partida partidaSeleccionada = partidasGuardadas.get(idSeleccionado);

            ScrabbleGame scrabbleGame = partidaSeleccionada.getScrabbleGame();
            ScrabbleControlador controlador = new ScrabbleControlador(scrabbleGame);

            boolean pertenece = controlador.perteneceJugadorAPartida(nombre);
            if (!pertenece) {
                JOptionPane.showMessageDialog(this, "No pertenecés a esta partida", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String nombreHost = partidaSeleccionada.getNombreHost();
            if(nombreHost.equals(nombre)) {
                controlador.cargarPartida(nombre, idSeleccionado);
            }else if(!controlador.partidaYaIniciada()){
                JOptionPane.showMessageDialog(this,
                        "Solo el jugador host (" + nombreHost + ") puede cargar la partida.\nEsperá a que la inicie.",
                        "No sos el host",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            vista.iniciarVista(nombre);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccioná una partida de la lista", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}