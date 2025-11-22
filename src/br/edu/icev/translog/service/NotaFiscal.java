package br.edu.icev.translog.service;

import br.edu.icev.translog.model.Entrega;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class NotaFiscal {

    
    public void emitirNotaFiscal(Entrega entrega) {
        StringBuilder nota = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        nota.append("========================================\n");
        nota.append("           NOTA FISCAL DE SERVIÇO       \n");
        nota.append("           TRANSLOG LOGÍSTICA           \n");
        nota.append("========================================\n");
        nota.append("CLIENTE: ").append(entrega.getCliente().getNome()).append("\n");
        nota.append("CPF/CNPJ: ").append(entrega.getCliente().getCpfCnpj()).append("\n");
        nota.append("----------------------------------------\n");
        nota.append("DETALHES DO FRETE\n");
        nota.append("Motorista: ").append(entrega.getMotorista().getNome()).append("\n");
        nota.append("Data Entrega: ").append(entrega.getDataHorario().format(fmt)).append("\n");
        nota.append("Tipo de Carga: ").append(entrega.getCarga().getTipo()).append("\n");
        
        if (entrega.getCarga().ehCargaEspecial()) {
            nota.append("(!) Carga Especial: Sim (Adicional Aplicado)\n");
        }

        nota.append("----------------------------------------\n");
        nota.append(String.format("VALOR TOTAL: R$ %.2f\n", entrega.getValorFrete()));
        nota.append("========================================\n");

        //salvar arquivo na pasta das notas
        salvarNotaEmArquivo(nota.toString(), entrega.getCliente().getNome());
    }

    private void salvarNotaEmArquivo(String conteudo, String nomeCliente) {
        String nomePasta = "notas_fiscais";
        File diretorio = new File(nomePasta);

        if (!diretorio.exists()) {
            diretorio.mkdirs(); 
        }

        //gera nome do arquivo
        String nomeArquivo = "NF_" + nomeCliente.replaceAll("\\s+", "") + "_" + System.currentTimeMillis() + ".txt";
        
        //cria o arquivo na pasta
        File arquivoFinal = new File(diretorio, nomeArquivo);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoFinal))) {
            writer.write(conteudo);
            System.out.println(">> Nota Fiscal salva em: " + arquivoFinal.getPath());
        } catch (IOException e) {
            System.err.println("Erro ao gerar arquivo da Nota Fiscal: " + e.getMessage());
        }
    }
}