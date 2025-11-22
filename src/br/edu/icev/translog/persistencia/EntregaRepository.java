package br.edu.icev.translog.persistencia;

import br.edu.icev.translog.model.Entrega;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class EntregaRepository {

    
    private static final String ARQUIVO_CSV = "relatorio_entregas.csv";

    /**
     * salva a lista completa de entregas em um arquivo CSV.
     * @param listaDeEntregas a lista vem do Agendamento.java
     */
    public void salvarTudo(List<Entrega> listaDeEntregas) {
        
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_CSV))) {
                
            writer.write("CLIENTE;MOTORISTA;DATA;TIPO_CARGA;VALOR_FRETE");
            writer.newLine();

            for (Entrega entrega : listaDeEntregas) {
                String linha = montarLinhaCSV(entrega);
                writer.write(linha);
                writer.newLine();
            }

            System.out.println(">> Persistência: Dados salvos com sucesso em '" + ARQUIVO_CSV + "'");

        } catch (IOException e) {
            System.err.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    //funcao pra montar a linha do csv direito
    private String montarLinhaCSV(Entrega e) {
        //formato: NomeCliente;NomeMotorista;Data;TipoCarga;Valor
        return e.getCliente().getNome() + ";" +
               e.getMotorista().getNome() + ";" +
               e.getDataHorario().toString() + ";" +
               e.getCarga().getTipo() + ";" +
               "R$ " + String.format("%.2f", e.getValorFrete());
    }
}