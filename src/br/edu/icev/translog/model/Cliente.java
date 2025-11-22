package br.edu.icev.translog.model;

public abstract class Cliente {
    private String nome;
    private String cpfCnpj;
    private String telefone;

    public Cliente(String nome, String cpfCnpj, String telefone) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.telefone = telefone;
    }

    public abstract double obterDesconto();


    public String getNome() { return nome; }
    public String getCpfCnpj() { return cpfCnpj; }
    public String getTelefone() { return telefone; }
    
    @Override
    public String toString() {
        return nome + " (" + cpfCnpj + ")";
    }
}