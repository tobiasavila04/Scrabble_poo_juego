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
}
