package ar.edu.unlu.poo.VISTA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VistaGraficaa extends JPanel {
    private static final int FILAS = 15;
    private static final int COLUMNAS = 15;
    private static final int TAMANO_CELDA = 40;
    private static final int ALTURA_CELDA = 10; // Para simular 3D

    private final char[][] tablero = new char[FILAS][COLUMNAS];

    public VistaGraficaa() {
        // Manejar clics para colocar fichas
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                colocarFicha(e.getX(), e.getY());
            }
        });
    }

    private void colocarFicha(int x, int y) {
        // Convertir coordenadas del mouse a celda del tablero
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                int isoX = (i - j) * TAMANO_CELDA / 2 + getWidth() / 2;
                int isoY = (i + j) * TAMANO_CELDA / 4 + 50;
                if (x >= isoX && x <= isoX + TAMANO_CELDA &&
                        y >= isoY && y <= isoY + TAMANO_CELDA / 2) {
                    tablero[i][j] = 'A'; // Ejemplo: coloca una 'A'
                    repaint();
                    return;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar tablero en perspectiva isométrica
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                int x = (i - j) * TAMANO_CELDA / 2 + getWidth() / 2;
                int y = (i + j) * TAMANO_CELDA / 4 + 50;

                // Dibujar la celda con perspectiva
                g2d.setColor((i + j) % 2 == 0 ? Color.LIGHT_GRAY : Color.GRAY);
                Polygon celda = new Polygon();
                celda.addPoint(x, y);
                celda.addPoint(x + TAMANO_CELDA / 2, y + TAMANO_CELDA / 4);
                celda.addPoint(x, y + TAMANO_CELDA / 2);
                celda.addPoint(x - TAMANO_CELDA / 2, y + TAMANO_CELDA / 4);
                g2d.fillPolygon(celda);

                // Si hay ficha, dibujarla
                if (tablero[i][j] != '\0') {
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.valueOf(tablero[i][j]), x - 5, y + 15);
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Scrabble 3D en JFrame");
        VistaGraficaa panel = new VistaGraficaa();
        frame.add(panel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
