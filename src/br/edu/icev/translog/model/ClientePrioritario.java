package br.edu.icev.translog.model;

public class ClientePrioritario extends Cliente {

    public ClientePrioritario(String nome, String cpfCnpj, String telefone) {
        super(nome, cpfCnpj, telefone);
    }

    @Override
    public double obterDesconto() {
        // desconto de 20% para clientes prioritários
        return 0.20; 
    }
}