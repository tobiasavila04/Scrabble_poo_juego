package ar.edu.unlu.poo.MODELO;

public enum BonificacionTablero {
    NORMAL(1,1),
    DOBLE_PALABRA(1,2),
    TRIPLE_PALABRA(1,3),
    DOBLE_LETRA(2,1),
    TRIPLE_LETRA(3,1),
    CENTRO(2,1);

    private final int multiplicadorLetra;
    private final int multiplicadorPalabra;
    BonificacionTablero(int multiplicadorLetra, int multiplicadorPalabra) {
        this.multiplicadorLetra = multiplicadorLetra;
        this.multiplicadorPalabra = multiplicadorPalabra;
    }
    public int getMultiplicadorLetra() {
        return multiplicadorLetra;
    }

    public int getMultiplicadorPalabra() {
        return multiplicadorPalabra;
    }

    public static BonificacionTablero obtenerBonificacion(PosicionCelda posicion){
        int n = 15;
        int minFila = Math.min(posicion.getPosicionX(), n - 1 - posicion.getPosicionX());
        int minColumna = Math.min(posicion.getPosicionY(), n - 1 - posicion.getPosicionY());
        if(minFila == 7 && minColumna == 7) return BonificacionTablero.CENTRO;
        if (minFila == 0 && minColumna == 0 || (minFila == 7 && ( minColumna == 0 || minColumna == 7))) {
            return BonificacionTablero.TRIPLE_PALABRA;
        }
        if ((minFila == 1 && minColumna == 5) || (minFila == 5 && minColumna == 1) ||
                (minFila == 5 && minColumna == 5)) {
            return BonificacionTablero.TRIPLE_LETRA;
        }
        if ((minFila == 0 && (minColumna == 3 || minColumna == 11)) || (minFila == 2 && (minColumna == 6 || minColumna == 8)) ||
                (minFila == 3 && (minColumna == 0 || minColumna == 7)) ||
                (minFila == 6 && (minColumna == 2 || minColumna == 6))) {
            return BonificacionTablero.DOBLE_LETRA;
        }
        if (minFila == minColumna || minFila + minColumna == 14) {
            return BonificacionTablero.DOBLE_PALABRA;
        }
        return BonificacionTablero.NORMAL;
    }
}
