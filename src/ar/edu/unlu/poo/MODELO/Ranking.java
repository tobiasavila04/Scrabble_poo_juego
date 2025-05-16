package ar.edu.unlu.poo.MODELO;

import ar.edu.unlu.poo.Serializador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Ranking implements Serializable {
    private static Ranking instancia;
    private ArrayList<Jugador> ranking;

    public Ranking(){
        this.ranking = Serializador.cargarJugadorHistorico();
    }

    public static Ranking getInstance() {
        if (instancia == null) {
            instancia = new Ranking();
        }
        return instancia;
    }

    private void ordenarRanking() {
        ranking.sort(Comparator.comparingInt(Jugador::getPuntos).reversed());
    }

    public ArrayList<Jugador> obtenerTop5() {
        ordenarRanking();
        ArrayList<Jugador> top5 = new ArrayList<>();

        for (int i = 0; i < ranking.size() && i < 5; i++) {
            if(ranking.get(i) != null) {
                top5.add(ranking.get(i));
            }
        }
        return top5;
    }
}
