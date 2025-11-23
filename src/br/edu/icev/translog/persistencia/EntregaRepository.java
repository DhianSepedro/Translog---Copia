package br.edu.icev.translog.persistencia;

import br.edu.icev.translog.model.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EntregaRepository {

    private static final String ARQUIVO_CSV = "relatorio_entregas.csv";

    //salva tudo em csv
    public void salvarTudo(List<Entrega> listaDeEntregas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_CSV))) {
            // Cabeçalho
            writer.write("DOC_CLIENTE;CNH_MOTORISTA;DATA;PESO;TIPO;ESPECIAL;VALOR;DISTANCIA");
            writer.newLine();

            for (Entrega e : listaDeEntregas) {
                //salva cpf e cnpj pra facilitar o carregamento
                String linha = e.getCliente().getCpfCnpj() + ";" +
                               e.getMotorista().getCnh() + ";" +
                               e.getDataHorario().toString() + ";" +
                               e.getCarga().getPeso() + ";" +
                               e.getCarga().getTipo() + ";" +
                               e.getCarga().ehCargaEspecial() + ";" +
                               e.getValorFrete() + ";" +
                               e.getDistanciaKm(); 
                               
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar entregas: " + e.getMessage());
        }
    }

    //carrega tudo do csv
    public List<Entrega> carregar(List<Cliente> clientesDisponiveis, List<Motorista> motoristasDisponiveis) {
        List<Entrega> listaRecuperada = new ArrayList<>();
        File file = new File(ARQUIVO_CSV);

        if (!file.exists()) return listaRecuperada;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha = reader.readLine(); 

            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length < 8) continue; 

                String docCliente = partes[0];
                String cnhMotorista = partes[1];
                String dataStr = partes[2];
                double peso = Double.parseDouble(partes[3]);
                TipoCarga tipo = TipoCarga.valueOf(partes[4]); 
                boolean especial = Boolean.parseBoolean(partes[5]);
                double valor = Double.parseDouble(partes[6]);
                double distancia = Double.parseDouble(partes[7]);

                //linka o documento com o cliente
                Cliente clienteEncontrado = null;
                for (Cliente c : clientesDisponiveis) {
                    String docLimpoMemoria = c.getCpfCnpj().replaceAll("\\D", "");
                    String docLimpoCsv = docCliente.replaceAll("\\D", "");
                    
                    if (docLimpoMemoria.equals(docLimpoCsv)) {
                        clienteEncontrado = c;
                        break;
                    }
                }

                //linka a cnh com o motorista
                Motorista motoristaEncontrado = null;
                for (Motorista m : motoristasDisponiveis) {
                    if (m.getCnh().equals(cnhMotorista)) {
                        motoristaEncontrado = m;
                        break;
                    }
                }

                //recria a entrega se achou os dois
                if (clienteEncontrado != null && motoristaEncontrado != null) {
                    LocalDateTime data = LocalDateTime.parse(dataStr); // O Java lê o formato ISO nativamente
                    Carga carga = new Carga(peso, tipo, especial); // O '0' é aquele valorDeclarado
                    
                    Entrega entregaRecuperada = new Entrega(clienteEncontrado, motoristaEncontrado, carga, data, distancia);
                    entregaRecuperada.setValorFrete(valor);
                    
                    listaRecuperada.add(entregaRecuperada);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar histórico de entregas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return listaRecuperada;
    }
}