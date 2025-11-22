package br.edu.icev.translog.model;

public class ClienteEmpresarial extends Cliente {

    public ClienteEmpresarial(String nome, String cpfCnpj, String telefone) {
        super(nome, cpfCnpj, telefone);
    }

    @Override
    public double obterDesconto() {
        // desconto de 10% para clientes empresariais
        return 0.10; 
    }
}