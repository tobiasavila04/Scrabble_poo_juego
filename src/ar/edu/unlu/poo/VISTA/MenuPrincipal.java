package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.APPCliente;
import ar.edu.unlu.poo.APPServidor;
import ar.edu.unlu.rmimvc.RMIMVCException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MenuPrincipal extends JFrame {
    private Image imagenFondo;
    private JLabel backgroundLabel;

    public MenuPrincipal(){
        setTitle("Scrabble - Menu Principal");
        setSize(500,700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        imagenFondo = new ImageIcon("src/LETRAS/Menu.png").getImage();
        JPanel panelFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panelFondo.setLayout(new GridBagLayout());
        panelFondo.setOpaque(false); // ✅ Para que la imagen de fondo se vea bie
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // ✅ Centra el título
        gbc.fill = GridBagConstraints.NONE; // ❌ No se estira horizontalmente
        gbc.insets = new Insets(20, 0, 20, 0);

        JLabel titulo = new JLabel("Scrabble", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        panelFondo.add(titulo, gbc);


        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setOpaque(false);
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST; // ✅ Alinear a la izquierda
        gbc.fill = GridBagConstraints.HORIZONTAL;

        crearBoton(panelBotones,"Nueva Partida", e -> {
            try {
                nuevaPartida();
            } catch (RMIMVCException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        crearBoton(panelBotones,"Cargar Partida", e -> cargarPartida());
        crearBoton(panelBotones,"Unirse a Partida",e -> unirsePartida());
        crearBoton(panelBotones,"Reglas", e -> reglasJuego());
        crearBoton(panelBotones,"Salir",  e -> System.exit(0));

        panelFondo.add(panelBotones, gbc);

        setContentPane(panelFondo);
        setVisible(true);
    }
    private void crearBoton(JPanel panel, String texto, ActionListener action) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 18));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setBackground(new Color(70, 130, 180));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createRaisedBevelBorder());
        boton.setPreferredSize(new Dimension(200, 50)); // Tamaño fijo de botones
        boton.addActionListener(action);
        boton.setMaximumSize(new Dimension(200, 50));
        panel.add(Box.createVerticalStrut(10)); // Espaciado entre botones
        panel.add(boton);
    }

    private void unirsePartida() {
        new APPCliente();
    }

    private void cargarPartida() {
    }

    private void nuevaPartida() throws RMIMVCException, IOException {
        new APPServidor();
    }

    private void reglasJuego() {
        String reglasTexto = """
        📜 REGLAS DEL SCRABBLE 📜
        
        🔹 OBJETIVO DEL JUEGO
        - Formar palabras en el tablero y sumar puntos.
        - Gana quien tenga más puntos al final.
        
        🔹 PREPARACIÓN
        - Se juega entre 2 y 4 jugadores.
        - Cada jugador recibe 7 fichas.
        - El jugador con la letra de mayor valor empieza.
        
        🔹 CÓMO JUGAR
        1️⃣ La primera palabra colocada se debe colocar en el centro (vertical u horizontal) 
        1️⃣ Cada palabra debe estar conectada a otra en el tablero, cuidado con las palabras adyacentes, pero mientras mas palabras modificadas mayores puntos!.
        2️⃣ Se suman los puntos según el valor de cada letra de la palabra formada y palabras existentes modifcadas, ademas se suman puntos extras segun bonificacion.
        3️⃣ Se rellena el atril hasta volver a tener 7 fichas.
        4️⃣ Puedes: jugar una palabra, intercambiar fichas o pasar turno, pero ojo! si un jugador pasa turno dos veces consecutiva el juego termina!
           Antes de intercambiar fichas deberas primero haber formado una palabra en el tablero y ademas deben haber mas de 7 fichas en la bolsa!
           Maximo de intercambios : 2.
        
        🔹 CASILLAS ESPECIALES
        - 🔴 Triple Palabra: x3 puntos en toda la palabra.
        - 🔵 Doble Palabra: x2 puntos en toda la palabra.
        - 🟩 Triple Letra: x3 puntos en la letra.
        - 🟦 Doble Letra: x2 puntos en la letra.
        
        🔹 PUNTOS EXTRA
        ✅ Usar las 7 fichas en una jugada: +50 puntos.
        
        🔹 FIN DEL JUEGO
        - Jugador se quedo sin fichas en su atril y la bolsa este vacie!.
        - Jugador pasa 2 turnos seguidos.
        - Gana quien tenga más puntos.
        
        ¡Buena suerte y diviértete! 🎲
    """;
        JTextArea textArea = new JTextArea(reglasTexto);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 500));

        JOptionPane.showMessageDialog(this, scrollPane, "Reglas del Scrabble", JOptionPane.INFORMATION_MESSAGE);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(MenuPrincipal::new);
    }

}
