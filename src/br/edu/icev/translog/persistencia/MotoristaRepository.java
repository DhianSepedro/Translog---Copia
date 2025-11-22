package br.edu.icev.translog.persistencia;

import br.edu.icev.translog.model.Motorista;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MotoristaRepository {

    private static final String ARQUIVO = "banco_motoristas.csv";

    public void salvar(List<Motorista> motoristas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            writer.write("NOME;CNH");
            writer.newLine();

            for (Motorista m : motoristas) {
                String linha = m.getNome() + ";" + m.getCnh();
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar motoristas: " + e.getMessage());
        }
    }
}