package ar.edu.unlu.poo.MODELO;

import java.io.Serializable;
import java.net.ProtocolFamily;

public class Celda implements Serializable {
    private PosicionCelda posicion;
    private BonificacionTablero bonificacion;
    private Ficha ficha;
    private EstadoCelda estado;

    public Celda(PosicionCelda posicion){
        this.posicion = posicion;
        this.bonificacion = BonificacionTablero.obtenerBonificacion(posicion);
        this.ficha = null;
        this.estado = EstadoCelda.LIBRE;
    }

    public EstadoCelda getEstado() {
        return estado;
    }

    public Ficha getFicha(){
        return ficha;
    }

    public void setFicha(Ficha ficha){
        this.ficha = ficha;
    }

    public BonificacionTablero getBonificacion() {
        return bonificacion;
    }

    public void agregarFicha(Ficha ficha) {
        this.ficha = ficha;
        this.estado = EstadoCelda.OCUPADA;
    }
    public void sacarFicha(){
        this.ficha = null;
        this.estado = EstadoCelda.LIBRE;
    }

    public int getBonificacionLetra() {
        return bonificacion.getMultiplicadorLetra();
    }

    public int getBonificacionPalabra(){
        return bonificacion.getMultiplicadorPalabra();
    }

    //static ya que no depende del estado de la celda, La Celda no necesita saber cómo se calcula la bonificación, solo la usa. Si en el futuro las reglas de bonificación cambian, solo modificas el enum, sin tocar el resto del código.
    //Si agregas un nuevo tipo de bonificación, solo tienes que agregar una nueva constante en BonificacionTablero, sin afectar la lógica en Celda.

}
