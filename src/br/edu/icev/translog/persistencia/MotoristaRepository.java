package br.edu.icev.translog.persistencia;

import br.edu.icev.translog.model.Motorista;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MotoristaRepository {

    private static final String ARQUIVO = "banco_motoristas.csv";

    //salva no csv
    public void salvar(List<Motorista> motoristas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            writer.write("NOME;CNH");
            writer.newLine();

            //dados do motorista
            for (Motorista m : motoristas) {
                String linha = m.getNome() + ";" + m.getCnh();
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar motoristas: " + e.getMessage());
        }
    }

    //carrega do csv
    public List<Motorista> carregar() {
        List<Motorista> lista = new ArrayList<>();
        File file = new File(ARQUIVO);
        
        if (!file.exists()) {
            return lista;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha = reader.readLine();
            
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                //verifica se tem os dois dados
                if (partes.length >= 2) {
                    String nome = partes[0];
                    String cnh = partes[1];
                    lista.add(new Motorista(nome, cnh));
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar motoristas: " + e.getMessage());
        }
        
        return lista;
    }
}