package ar.edu.unlu.poo;

import java.io.File;

public class GeneratorID {
    private static final String ARCHIVO_ID = "PARTIDAS_ID.DAT";

    public static synchronized int generarID(){
        Integer id = Serializador.cargarObjeto(ARCHIVO_ID);
        if(id == null){
            id = 0;
        }else{
            id++;
        }
        Serializador.guardarObjeto(ARCHIVO_ID, id);
        return id;
    }
}
