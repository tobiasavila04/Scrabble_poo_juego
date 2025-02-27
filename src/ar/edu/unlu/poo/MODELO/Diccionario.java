package ar.edu.unlu.poo.MODELO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import java.util.Set;

public class Diccionario {
    private static final String NOMBRE_ARCHIVO = "C:\\Users\\Tobias\\IdeaProjects\\Scrabble_poo\\src\\ar\\edu\\unlu\\poo\\MODELO/es_dic.txt";
    private HashSet<String> palabrasDiccionario;

    public Diccionario() throws IOException {
        palabrasDiccionario = new HashSet<>();
        cargarDiccionario();
    }

    public void cargarDiccionario() throws IOException {
        BufferedReader leerArchivo = new BufferedReader(new FileReader(NOMBRE_ARCHIVO));
        String palabra;
        while ((palabra = leerArchivo.readLine()) != null){
            palabrasDiccionario.add(palabra.trim().toLowerCase());
        }
    }

    public boolean esPalabraValida(String palabra){
        return palabrasDiccionario.contains(palabra.trim().toLowerCase());
    }
}
