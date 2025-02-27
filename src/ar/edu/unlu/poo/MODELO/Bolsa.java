package ar.edu.unlu.poo.MODELO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Bolsa {
    private ArrayList<Ficha> fichas;

    public Bolsa(){
        fichas = new ArrayList<>();
        iniciarFichasEnBolsa();
    }

    public void iniciarFichasEnBolsa(){
        agregarFicha('A',12,1);
        agregarFicha('B',3,3);
        agregarFicha('C',4,3);
        agregarFicha('D',5,2);
        agregarFicha('E',12,1);
        agregarFicha('F',1,4);
        agregarFicha('G',2,2);
        agregarFicha('H',2,4);
        agregarFicha('I',6,1);
        agregarFicha('J',1,8);
        agregarFicha('K',1,5);
        agregarFicha('L',4,1);
        agregarFicha('M',3,3);
        agregarFicha('N',5,1);
        agregarFicha('Ñ',1,8);
        agregarFicha('O',9,1);
        agregarFicha('P',2,3);
        agregarFicha('Q',1,10);
        agregarFicha('R',5,1);
        agregarFicha('S',6,1);
        agregarFicha('T',4,1);
        agregarFicha('U',6,1);
        agregarFicha('V',1,4);
        agregarFicha('W',1,4);
        agregarFicha('X',1,8);
        agregarFicha('Y',1,4);
        agregarFicha('Z',1,10);
        agregarFicha('_',2,0);
    }

    private void agregarFicha(char letra, int cantidad, int puntos){
        for(int i = 0;i < cantidad; i++){
            Ficha ficha = new Ficha(letra, puntos);
            fichas.add(ficha);
        }
    }

    public void agregarFicha(Ficha ficha){
        fichas.add(ficha);
    }

    public Ficha sacarFichaDeLaBolsa(){
        if(fichas.isEmpty()) return null;
        mezclar();
        int fichaRandom = new Random().nextInt(fichas.size());
        Ficha ficha = fichas.get(fichaRandom);
        fichas.remove(ficha);
        System.out.println("fichas:: " + fichas.size());
        return ficha;
    }

    public int cantidadFichas(){
        return fichas.size();
    }

    public boolean esVacia(){
        return fichas.isEmpty();
    }

    public void mezclar(){
        Collections.shuffle(fichas);
    }
}
