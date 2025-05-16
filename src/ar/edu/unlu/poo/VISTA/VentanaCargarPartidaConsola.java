package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.CONTROLADOR.ScrabbleControlador;
import ar.edu.unlu.poo.MODELO.Partida;
import ar.edu.unlu.poo.MODELO.ScrabbleGame;
import ar.edu.unlu.poo.Serializador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

public class VentanaCargarPartidaConsola extends JFrame {
    private JTextArea areaTexto;
    private JTextField campoEntrada;
    private Map<Integer, Partida> partidasGuardadas;
    private ArrayList<Integer> idsOrdenados;
    private String nombre;
    private IVista vista;

    public VentanaCargarPartidaConsola(IVista vista, String nombre) {
        setTitle("Cargar Partida (Consola)");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.nombre = nombre;
        this.vista = vista;

        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(areaTexto);

        campoEntrada = new JTextField();
        campoEntrada.addActionListener(e -> {
            String entrada = campoEntrada.getText().trim();
            campoEntrada.setText("");
            manejarEntrada(entrada);
        });

        add(scroll, BorderLayout.CENTER);
        add(campoEntrada, BorderLayout.SOUTH);

        mostrarPartidasDisponibles();

        setVisible(true);
    }

    private void mostrarPartidasDisponibles() {
        partidasGuardadas = Serializador.cargarPartidaGuardada();
        if (partidasGuardadas.isEmpty()) {
            areaTexto.append("No hay partidas guardadas.\n");
            return;
        }
        idsOrdenados = new ArrayList<>(partidasGuardadas.keySet());
        idsOrdenados.sort(Comparator.naturalOrder());

        areaTexto.append("=== PARTIDAS DISPONIBLES ===\n");
        for (int i = 0; i < idsOrdenados.size(); i++) {
            Integer id = idsOrdenados.get(i);
            Partida partida = partidasGuardadas.get(id);
            String descripcion = String.format("[%d] Partida #%d - Jugadores: %d - Turno: %s",
                    i + 1, id, partida.getJugadores().size(), partida.getTurnoActual());
            areaTexto.append(descripcion + "\n");
        }
        areaTexto.append("\nEscribí el número de la partida a cargar:\n");
    }

    private void manejarEntrada(String entrada) {
        try {
            int opcion = Integer.parseInt(entrada) - 1;
            if (opcion < 0 || opcion >= idsOrdenados.size()) {
                areaTexto.append("❌ Opción inválida. Intentá de nuevo.\n");
                return;
            }

            Integer idSeleccionado = idsOrdenados.get(opcion);
            Partida partidaSeleccionada = partidasGuardadas.get(idSeleccionado);

            ScrabbleGame scrabbleGame = partidaSeleccionada.getScrabbleGame();
            ScrabbleControlador controlador = new ScrabbleControlador(scrabbleGame);

            boolean pertenece = controlador.perteneceJugadorAPartida(nombre);
            if (!pertenece) {
                areaTexto.append("❌ No pertenecés a esta partida.\n");
                return;
            }

            String nombreHost = partidaSeleccionada.getNombreHost();
            if (nombreHost.equals(nombre)) {
                controlador.cargarPartida(nombre, idSeleccionado);
            } else if (!controlador.partidaYaIniciada()) {
                areaTexto.append("⚠️ Solo el jugador host (" + nombreHost + ") puede cargar la partida.\n");
                return;
            }

            vista.iniciarVista(nombre);
            dispose();

        } catch (NumberFormatException ex) {
            areaTexto.append("❌ Entrada inválida. Escribí un número.\n");
        } catch (IOException ex) {
            areaTexto.append("❌ Error al cargar la partida: " + ex.getMessage() + "\n");
        }
    }
}
