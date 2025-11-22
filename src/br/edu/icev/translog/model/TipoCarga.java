package br.edu.icev.translog.model;

public enum TipoCarga {
    LEVE(1.0),   // lembrar mudar pesos
    MEDIA(1.5),  
    PESADA(2.0); 
    private final double fatorPreco;

    TipoCarga(double fatorPreco) {
        this.fatorPreco = fatorPreco;
    }

    public double getFatorPreco() {
        return fatorPreco;
    }
}