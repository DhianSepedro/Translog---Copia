package br.edu.icev.translog.persistencia;

import br.edu.icev.translog.model.Cliente;
import br.edu.icev.translog.model.ClienteEmpresarial;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ClienteRepository {

    private static final String ARQUIVO = "banco_clientes.csv";

    public void salvar(List<Cliente> clientes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            writer.write("TIPO;NOME;DOC;TELEFONE");
            writer.newLine();

            for (Cliente c : clientes) {
                //identifica qual tipo de cliente
                String tipo = (c instanceof ClienteEmpresarial) ? "EMPRESARIAL" : "PRIORITARIO";
                
                String linha = tipo + ";" + 
                               c.getNome() + ";" + 
                               c.getCpfCnpj() + ";" + 
                               c.getTelefone();
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar clientes: " + e.getMessage());
        }
    }
}