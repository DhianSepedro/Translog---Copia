package br.edu.icev.translog.model;

public class Motorista {
    private String nome;
    private String cnh;

    public Motorista(String nome, String cnh) {
        this.nome = nome;
        this.cnh = cnh;
    }

    public String getNome() { return nome; }
    public String getCnh() { return cnh; }

    @Override
    public String toString() {
        return nome + " (CNH: " + cnh + ")";
    }
}