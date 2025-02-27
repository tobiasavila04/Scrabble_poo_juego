package ar.edu.unlu.poo;

import ar.edu.unlu.poo.MODELO.ScrabbleGame;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.Util;
import ar.edu.unlu.rmimvc.servidor.Servidor;

import javax.swing.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class APPServidor {
    public APPServidor() throws IOException, RMIMVCException {
        ArrayList<String> ips = Util.getIpDisponibles();
        String ip = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione la IP en la que escuchar� peticiones el servidor", "IP del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                ips.toArray(),
                null
        );
        String port = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el puerto en el que escuchar� peticiones el servidor", "Puerto del servidor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                8888
        );
        String cantidadJugadoresStr = (String) JOptionPane.showInputDialog(
                null,
                "Ingrese la cantidad de jugadores (debe ser entre 2 y 4)", "Cantidad de Jugadores",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                "2"
        );
        int cantidadJugadores = 2; // Valor por defecto
        try {
            cantidadJugadores = Integer.parseInt(cantidadJugadoresStr);
            if (cantidadJugadores < 2 || cantidadJugadores > 4) {
                JOptionPane.showMessageDialog(null, "La cantidad de jugadores debe estar entre 2 y 4.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        ScrabbleGame modelo = ScrabbleGame.getInstancia();
        Servidor servidor = new Servidor(ip, Integer.parseInt(port));
        try {
            servidor.iniciar(modelo);
            modelo.setCantidadJugadores(cantidadJugadores);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RMIMVCException e) {
            System.out.println("Error al iniciar el servidor RMI: " + e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
