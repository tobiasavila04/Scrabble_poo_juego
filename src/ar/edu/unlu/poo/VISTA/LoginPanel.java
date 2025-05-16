package ar.edu.unlu.poo.VISTA;

import ar.edu.unlu.poo.CONTROLADOR.ScrabbleControlador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginPanel extends JFrame {
    private JTextField txtUsuario;
    private String nombreUsuario = null;
    private ScrabbleControlador controlador;



    public LoginPanel(ScrabbleControlador controlador) {
        setTitle("Sign In");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        this.controlador = controlador;

        JPanel contenedor = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contenedor.setBackground(new Color(230, 230, 230));

        JPanel mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(350, 400));
        mainPanel.setBackground(new Color(255, 255, 255, 200));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(iconLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        txtUsuario = new JTextField();
        txtUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        txtUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtUsuario.setToolTipText("Username");
        mainPanel.add(txtUsuario);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));


        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        crearBoton(botonesPanel, "Ingresar", e -> {
            try {
                ingresar();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        crearBoton(botonesPanel, "Registrarse", e -> {
            try {
                registrarse();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        mainPanel.add(botonesPanel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        contenedor.add(mainPanel);
        add(contenedor, BorderLayout.CENTER);

        setVisible(true);
    }


    private void ingresar() throws IOException {
        String user = txtUsuario.getText().trim();
        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete los campos");
            return;
        }
        if(controlador.existeNombre(user)){
            JOptionPane.showMessageDialog(this, "¡Bienvenido " + user + "!");
            nombreUsuario = user;
            elegirVista();
            dispose();
        }else{
        JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
        }
    }

    private void registrarse() throws IOException {
        String user = txtUsuario.getText().trim();
        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete los campos");
            return;
        }

        if(controlador.existeNombre(user)){
            JOptionPane.showMessageDialog(this, "Ese usuario ya existe");
            return;
        }
        JOptionPane.showMessageDialog(this, "Usuario registrado correctamente");
        nombreUsuario = user;
        elegirVista();
        dispose();
    }

    private void crearBoton(JPanel panel, String texto, ActionListener action) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Arial", Font.BOLD, 18));
        boton.setBackground(new Color(70, 130, 180));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createRaisedBevelBorder());
        boton.setPreferredSize(new Dimension(100, 40));
        boton.addActionListener(action);
        panel.add(boton);
    }

    private void elegirVista() throws IOException {
        String[] opciones = {"Vista Gráfica", "Vista Consola"};
        int eleccion = JOptionPane.showOptionDialog(
                this,
                "¿Qué vista desea utilizar?",
                "Elegir Vista",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (eleccion == 0) {
            new VistaGraficaa(controlador, nombreUsuario);
        } else if (eleccion == 1) {
            new VistaConsola(controlador, nombreUsuario);
        }
    }

}

