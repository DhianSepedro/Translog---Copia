package br.edu.icev.translog.persistencia;

import br.edu.icev.translog.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {

    private static final String ARQUIVO = "banco_clientes.csv";

    public void salvar(List<Cliente> clientes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            writer.write("TIPO;NOME;DOC;TELEFONE");
            writer.newLine();

            for (Cliente c : clientes) {
                String tipo = (c instanceof ClienteEmpresarial) ? "EMPRESARIAL" : "PRIORITARIO";
                String linha = tipo + ";" + c.getNome() + ";" + c.getCpfCnpj() + ";" + c.getTelefone();
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar clientes: " + e.getMessage());
        }
    }

    //carrega ao iniciar
    public List<Cliente> carregar() {
        List<Cliente> lista = new ArrayList<>();
        File file = new File(ARQUIVO);
        
        if (!file.exists()) return lista; 

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha = reader.readLine(); 
            
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length < 4) continue;

                String tipo = partes[0];
                String nome = partes[1];
                String doc = partes[2];
                String tel = partes[3];

                if (tipo.equals("EMPRESARIAL")) {
                    lista.add(new ClienteEmpresarial(nome, doc, tel));
                } else {
                    lista.add(new ClientePrioritario(nome, doc, tel));
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar clientes: " + e.getMessage());
        }
        return lista;
    }
}