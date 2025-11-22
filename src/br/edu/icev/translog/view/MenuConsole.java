package br.edu.icev.translog.view;

import br.edu.icev.translog.model.*;
import br.edu.icev.translog.service.*;
import br.edu.icev.translog.persistencia.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuConsole {

    private Scanner scanner;
    
    //servicos
    private CalculadoraFrete calculadora;
    private Agendamento agendamentoService;
    private NotaFiscal nfService;
    
    //dados
    private EntregaRepository entregaRepo;
    private ClienteRepository clienteRepo;
    private MotoristaRepository motoristaRepo;

    
    private List<Cliente> clientesCadastrados;
    private List<Motorista> motoristasCadastrados;

    public MenuConsole() {
        this.scanner = new Scanner(System.in);
        
        //iniciando sistemas
        this.calculadora = new CalculadoraFrete();
        this.agendamentoService = new Agendamento();
        this.nfService = new NotaFiscal();
        
        
        this.entregaRepo = new EntregaRepository();
        this.clienteRepo = new ClienteRepository();
        this.motoristaRepo = new MotoristaRepository();
        
        this.clientesCadastrados = new ArrayList<>();
        this.motoristasCadastrados = new ArrayList<>();
        
        //**dados de TESTE */
        preCarregarDados();
    }

    public void iniciar() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n=== SISTEMA TRANSLOG (Logística & Entregas) ===");
            System.out.println("1. Cadastrar Cliente");
            System.out.println("2. Cadastrar Motorista");
            System.out.println("3. Nova Entrega (Cotação, Agendamento e NF)");
            System.out.println("4. Listar Entregas Agendadas");
            System.out.println("5. Salvar Tudo e Sair");
            System.out.print("Escolha uma opção: ");
            
            try {
                String input = scanner.nextLine();
                if (!input.trim().isEmpty()) {
                    opcao = Integer.parseInt(input);
                }
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            switch (opcao) {
                case 1: cadastrarCliente(); break;
                case 2: cadastrarMotorista(); break;
                case 3: realizarEntrega(); break;
                case 4: listarEntregas(); break;
                case 5: 
                    salvarTudo();
                    return; //sai do programa
                default: System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }

    
    private void salvarTudo() {
        System.out.println("\n--- Salvando Dados no Disco ---");
        
        
        entregaRepo.salvarTudo(agendamentoService.listarEntregas());
        
        clienteRepo.salvar(clientesCadastrados);
        System.out.println(">> Cadastro de clientes salvo em 'banco_clientes.csv'");
        
        motoristaRepo.salvar(motoristasCadastrados);
        System.out.println(">> Cadastro de motoristas salvo em 'banco_motoristas.csv'");
        
        System.out.println("Dados salvos com sucesso. Encerrando o sistema.");
    }

    //cadastros
    private void cadastrarCliente() {
        System.out.println("\n--- Novo Cliente ---");
        System.out.println("Tipo de Contrato:");
        System.out.println("1 - Empresarial (10% desconto)");
        System.out.println("2 - Prioritário (20% desconto)");
        System.out.print("Opção: ");
        int tipo = Integer.parseInt(scanner.nextLine());

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF/CNPJ: ");
        String doc = scanner.nextLine();
        System.out.print("Telefone: ");
        String tel = scanner.nextLine();

        Cliente novo;
        if (tipo == 1) {
            novo = new ClienteEmpresarial(nome, doc, tel);
        } else {
            novo = new ClientePrioritario(nome, doc, tel);
        }
        clientesCadastrados.add(novo);
        System.out.println("Cliente cadastrado com sucesso!");
    }

    private void cadastrarMotorista() {
        System.out.println("\n--- Novo Motorista ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CNH: ");
        String cnh = scanner.nextLine();
        
        motoristasCadastrados.add(new Motorista(nome, cnh));
        System.out.println("Motorista cadastrado com sucesso!");
    }

    //cadastro de entrega
    private void realizarEntrega() {
        if (clientesCadastrados.isEmpty() || motoristasCadastrados.isEmpty()) {
            System.out.println("ERRO: É necessário cadastrar Clientes e Motoristas antes.");
            return;
        }

        System.out.println("\n--- Nova Solicitação de Entrega ---");
        
        Cliente cliente = selecionarCliente();
        if (cliente == null) return;

        Motorista motorista = selecionarMotorista();
        if (motorista == null) return;

        System.out.print("Peso da carga (kg): ");
        double peso = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Distância total (km): ");
        double distancia = Double.parseDouble(scanner.nextLine());
        
        System.out.println("Classificação da Carga:");
        System.out.println("1 - LEVE (Fator x1.0)");
        System.out.println("2 - MEDIA (Fator x1.5)");
        System.out.println("3 - PESADA (Fator x2.0)");
        System.out.print("Opção: ");
        int tIdx = Integer.parseInt(scanner.nextLine());
        TipoCarga tipo = (tIdx == 1) ? TipoCarga.LEVE : (tIdx == 2) ? TipoCarga.MEDIA : TipoCarga.PESADA;

        System.out.print("A carga é Frágil ou Perigosa? (S/N): ");
        boolean perigosa = scanner.nextLine().trim().equalsIgnoreCase("S");

        Carga carga = new Carga(peso, tipo, perigosa);

        //definindo horario
        System.out.print("Agendar para daqui a quantas horas? ");
        int horas = Integer.parseInt(scanner.nextLine());
        LocalDateTime data = LocalDateTime.now().plusHours(horas);

        //chama o calculo
        double valor = calculadora.calcular(cliente, carga, distancia);
        
        System.out.println("\n--- ORÇAMENTO ---");
        System.out.printf("Valor Calculado: R$ %.2f\n", valor);
        if (perigosa) System.out.println("AVISO: Adicional de carga especial aplicado.");
        
        System.out.print("Confirmar Agendamento? (S/N): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("S")) {
            Entrega entrega = new Entrega(cliente, motorista, carga, data);
            entrega.setValorFrete(valor);
            
            try {
                //valida bloqueios e agenda
                agendamentoService.agendarEntrega(entrega);
                
                //gera nota
                nfService.emitirNotaFiscal(entrega);
                
                System.out.println(">> Operação finalizada com sucesso!");
            } catch (Exception e) {
                System.out.println("ERRO NO AGENDAMENTO: " + e.getMessage());
            }
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private void listarEntregas() {
        System.out.println("\n--- Agenda de Entregas ---");
        List<Entrega> lista = agendamentoService.listarEntregas();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma entrega agendada no momento.");
        } else {
            for (Entrega e : lista) {
                System.out.println(e);
            }
        }
    }

    //metodos auxiliares
    private Cliente selecionarCliente() {
        System.out.println("Selecione o Cliente:");
        for (int i = 0; i < clientesCadastrados.size(); i++) {
            System.out.println(i + " - " + clientesCadastrados.get(i).getNome());
        }
        System.out.print("Código: ");
        int idx = Integer.parseInt(scanner.nextLine());
        if (idx >= 0 && idx < clientesCadastrados.size()) return clientesCadastrados.get(idx);
        System.out.println("Cliente inválido.");
        return null;
    }

    private Motorista selecionarMotorista() {
        System.out.println("Selecione o Motorista:");
        for (int i = 0; i < motoristasCadastrados.size(); i++) {
            System.out.println(i + " - " + motoristasCadastrados.get(i).getNome());
        }
        System.out.print("Código: ");
        int idx = Integer.parseInt(scanner.nextLine());
        if (idx >= 0 && idx < motoristasCadastrados.size()) return motoristasCadastrados.get(idx);
        System.out.println("Motorista inválido.");
        return null;
    }
    
    private void preCarregarDados() {
        //dados templates
        clientesCadastrados.add(new ClienteEmpresarial("Tech Solutions", "12345678000199", "8699999999"));
        clientesCadastrados.add(new ClientePrioritario("Fast Delivery", "98765432000188", "8688888888"));
        motoristasCadastrados.add(new Motorista("João da Silva", "CNH12345"));
        motoristasCadastrados.add(new Motorista("Maria Oliveira", "CNH67890"));
    }
}