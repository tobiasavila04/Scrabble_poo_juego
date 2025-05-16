package ar.edu.unlu.poo.MODELO;

import java.io.Serializable;
import java.util.ArrayList;

public class Atril  implements Serializable {
    private ArrayList<Ficha> fichasAtril;
    private ArrayList<Ficha> guardarAtril;

    public Atril(){
        this.fichasAtril = new ArrayList<>();
        this.guardarAtril = new ArrayList<>();
    }

    public ArrayList<Ficha> getFichasAtril(){
        return this.fichasAtril;
    }

    public void setFichasAtril(Ficha fichasAtril) {
        this.fichasAtril.add(fichasAtril);
        this.guardarAtril.add(fichasAtril);
    }

    public void generarAtril(Bolsa bolsa){
        for(int i = 0; i < 7; i++){
            Ficha ficha = bolsa.sacarFichaDeLaBolsa();
            fichasAtril.add(ficha);
        }
        guardarAtril = new ArrayList<>(fichasAtril);
    }

    public void sacarFichaDelAtril(Ficha fichaSeleccionada){
        fichasAtril.remove(fichaSeleccionada);
    }

    public void rellenarAtril(Bolsa bolsa){
        guardarAtril = new ArrayList<>(fichasAtril);
        while(fichasAtril.size() < 7 && !bolsa.esVacia()){
            Ficha ficha = bolsa.sacarFichaDeLaBolsa();
            setFichasAtril(ficha);
        }
    }

    public Ficha obtenerFichaAtril(char letra){
        for (Ficha ficha : fichasAtril) {
            if (ficha.getLetra() == Character.toUpperCase(letra)) {
                return ficha;
            }
        }
        return null;
    }

    public void cambiarFichas(ArrayList<Ficha> fichasCambio, Bolsa bolsa){
        for (Ficha ficha : fichasCambio){
            if (fichasAtril.contains(ficha)) { // Nos aseguramos de que sea una ficha real del atril
                fichasAtril.remove(ficha);      // Elimina esa instancia exacta
                bolsa.agregarFicha(ficha);      // Devuelve esa misma ficha a la bolsa
            }
        }
        while(fichasAtril.size() < 7){
            setFichasAtril(bolsa.sacarFichaDeLaBolsa());
        }
    }

    public ArrayList<Ficha> restaurarAtril(){
        this.fichasAtril = new ArrayList<>(guardarAtril);
        return fichasAtril;
    }

    public int puntosRestantes() {
        int puntos = 0;
        for(Ficha ficha : fichasAtril){
            if(ficha != null){
                puntos += ficha.getPuntos();
            }
        }
        return puntos;
    }
}
