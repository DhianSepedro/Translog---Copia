package br.edu.icev.translog.model;

public class Carga {
    private double peso;
    private TipoCarga tipo; // leve, media, pesada
    private boolean isCargaEspecial; // True se for fragil ou perigosa

    public Carga(double peso, TipoCarga tipo, boolean isCargaEspecial) {
        this.peso = peso;
        this.tipo = tipo;
        this.isCargaEspecial = isCargaEspecial;
    }

    
    public double getPeso() { return peso; }
    public TipoCarga getTipo() { return tipo; }
    
    //auxiliar para calculo do frete
    public boolean ehCargaEspecial() {
        return isCargaEspecial;
    }
}