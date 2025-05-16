package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.MODELO.Jugador;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.jar.JarFile;

public class RankingVentana extends JFrame {
    private Image imagenFondo;
    private ArrayList<Jugador> topJugadores;

    public RankingVentana(ArrayList<Jugador> topJugadores) {
        this.topJugadores = topJugadores;
        configurarVentana();
    }

    private void configurarVentana() {
        setTitle("Ranking Scrabble");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        imagenFondo = new ImageIcon("src/ar/edu/unlu/poo/RESOURCES/fondo_ranking.jpeg").getImage();


        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelFondo.setLayout(new GridBagLayout());
        panelFondo.setOpaque(false);

        JPanel panelRanking = new JPanel();
        panelRanking.setOpaque(false);
        panelRanking.setLayout(new BoxLayout(panelRanking, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("TOP JUGADORES");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        //titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelRanking.add(titulo);
        panelRanking.add(Box.createVerticalStrut(20));

        for (Jugador jugador : topJugadores) {
            JLabel label = new JLabel(jugador.getNombre() + " - " + jugador.getPuntos());
            label.setFont(new Font("Monospaced", Font.PLAIN, 24));
            //
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelRanking.add(label);
            panelRanking.add(Box.createVerticalStrut(10));
        }

        JButton volverBtn = new JButton("Volver al Menú");
        volverBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        volverBtn.setFont(new Font("SansSerif", Font.PLAIN, 18));
        volverBtn.addActionListener(e -> {
            dispose();
        });
        panelRanking.add(Box.createVerticalStrut(20));
        panelRanking.add(volverBtn);


        panelFondo.add(panelRanking);
        setContentPane(panelFondo);
        setVisible(true);
    }
}

