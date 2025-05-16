package ar.edu.unlu.poo.MODELO;

public class BonificacionPalabra implements Bonificacion{
    private BonificacionTablero bonificacion;

    public BonificacionPalabra(BonificacionTablero bonificacion){
        this.bonificacion = bonificacion;
    }


    @Override
    public BonificacionTablero getBonificacion() {
        return bonificacion;
    }

    @Override
    public int getPuntos() {
        if(bonificacion == BonificacionTablero.DOBLE_PALABRA){
            return 2;
        }else{
            return 3;
        }
    }
}
