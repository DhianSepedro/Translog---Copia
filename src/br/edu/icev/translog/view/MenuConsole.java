package br.edu.icev.translog.view;

import br.edu.icev.translog.model.*;
import br.edu.icev.translog.service.*;
import br.edu.icev.translog.persistencia.*;
import br.edu.icev.translog.util.Validador; 

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class MenuConsole {

    private Scanner scanner;
    
    // Serviços
    private CalculadoraFrete calculadora;
    private Agendamento agendamentoService;
    private NotaFiscal nfService;
    
    // Repositórios
    private EntregaRepository entregaRepo;
    private ClienteRepository clienteRepo;
    private MotoristaRepository motoristaRepo;

    // Listas em Memória
    private List<Cliente> clientesCadastrados;
    private List<Motorista> motoristasCadastrados;

    public MenuConsole() {
        this.scanner = new Scanner(System.in);
        
        // Inicializa Repositórios
        this.entregaRepo = new EntregaRepository();
        this.clienteRepo = new ClienteRepository();
        this.motoristaRepo = new MotoristaRepository();
        
        // CARREGA DADOS DO DISCO (Igual à Janela Principal)
        this.clientesCadastrados = clienteRepo.carregar();
        this.motoristasCadastrados = motoristaRepo.carregar();
        
        // Inicializa Serviços
        this.calculadora = new CalculadoraFrete();
        this.agendamentoService = new Agendamento();
        this.nfService = new NotaFiscal();
        
        // Recupera histórico de entregas
        List<Entrega> historico = entregaRepo.carregar(clientesCadastrados, motoristasCadastrados);
        for(Entrega e : historico) {
            try { agendamentoService.listarEntregas().add(e); } catch(Exception ex){}
        }
    }

    public void iniciar() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n=== SISTEMA TRANSLOG (MODO CONSOLE) ===");
            System.out.println("1. Cadastrar Cliente");
            System.out.println("2. Cadastrar Motorista");
            System.out.println("3. Nova Entrega");
            System.out.println("4. Listar Entregas");
            System.out.println("5. Salvar e Sair");
            System.out.print("Escolha: ");
            
            try {
                String input = scanner.nextLine();
                if (!input.trim().isEmpty()) opcao = Integer.parseInt(input);
            } catch (NumberFormatException e) { opcao = -1; }

            switch (opcao) {
                case 1: cadastrarCliente(); break;
                case 2: cadastrarMotorista(); break;
                case 3: realizarEntrega(); break;
                case 4: listarEntregas(); break;
                case 5: salvarTudo(); return;
                default: System.out.println("Opção inválida!");
            }
        }
    }

    private void salvarTudo() {
        System.out.println("Salvando dados...");
        entregaRepo.salvarTudo(agendamentoService.listarEntregas());
        clienteRepo.salvar(clientesCadastrados);
        motoristaRepo.salvar(motoristasCadastrados);
        System.out.println("Tudo salvo! Encerrando.");
    }

    private void cadastrarCliente() {
        System.out.println("\n--- Novo Cliente ---");
        System.out.println("1-Empresarial | 2-Prioritário");
        int tipo = Integer.parseInt(scanner.nextLine());
        
        System.out.print("Nome: "); 
        String nome = scanner.nextLine();
        
        System.out.print("Documento (CPF/CNPJ): "); 
        String doc = scanner.nextLine();
        
        // VALIDAÇÃO NO CONSOLE
        if (tipo == 1 && !Validador.isCNPJ(doc)) {
            System.out.println("ERRO: CNPJ Inválido!"); return;
        }
        if (tipo == 2 && !Validador.isCPF(doc)) {
            System.out.println("ERRO: CPF Inválido!"); return;
        }

        System.out.print("Tel: "); 
        String tel = scanner.nextLine();

        Cliente c = (tipo == 1) ? new ClienteEmpresarial(nome, doc, tel) 
                                : new ClientePrioritario(nome, doc, tel);
        clientesCadastrados.add(c);
        System.out.println("Cliente salvo!");
    }

    private void cadastrarMotorista() {
        System.out.print("Nome: "); String nome = scanner.nextLine();
        System.out.print("CNH: "); String cnh = scanner.nextLine();
        
        if (!Validador.isCNH(cnh)) {
            System.out.println("ERRO: CNH Inválida (deve ter 11 dígitos)!"); return;
        }
        
        motoristasCadastrados.add(new Motorista(nome, cnh));
        System.out.println("Motorista salvo!");
    }

    private void realizarEntrega() {
        if (clientesCadastrados.isEmpty() || motoristasCadastrados.isEmpty()) {
            System.out.println("ERRO: Cadastre pessoas antes.");
            return;
        }
        
        // Seleção simplificada
        for(int i=0; i<clientesCadastrados.size(); i++) 
            System.out.println(i + " - " + clientesCadastrados.get(i).getNome());
        System.out.print("Index Cliente: ");
        int idxCli = Integer.parseInt(scanner.nextLine());
        
        for(int i=0; i<motoristasCadastrados.size(); i++) 
            System.out.println(i + " - " + motoristasCadastrados.get(i).getNome());
        System.out.print("Index Motorista: ");
        int idxMot = Integer.parseInt(scanner.nextLine());

        Cliente cli = clientesCadastrados.get(idxCli);
        Motorista mot = motoristasCadastrados.get(idxMot);

        System.out.print("Peso (kg): "); 
        double peso = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Distância (km): "); 
        double dist = Double.parseDouble(scanner.nextLine());
        
        // Classificação Automática (Lógica do Swing trazida pra cá)
        TipoCarga tipo;
        if (peso <= 10) tipo = TipoCarga.LEVE;
        else if (peso <= 100) tipo = TipoCarga.MEDIA;
        else tipo = TipoCarga.PESADA;
        System.out.println(">> Classificação Automática: " + tipo);
        
        System.out.print("Carga Especial (S/N)? ");
        boolean especial = scanner.nextLine().equalsIgnoreCase("S");

        Carga carga = new Carga(peso, tipo, especial);

        double valor = calculadora.calcular(cli, carga, dist);
        System.out.printf(">>> Valor Frete: R$ %.2f\n", valor);
        
        System.out.print("Confirmar (S/N)? ");
        if (scanner.nextLine().equalsIgnoreCase("S")) {
            System.out.print("Agendar para daqui a quantas horas? ");
            int h = Integer.parseInt(scanner.nextLine());
            

            Entrega e = new Entrega(cli, mot, carga, LocalDateTime.now().plusHours(h), dist);
            e.setValorFrete(valor);
            
            try {
                agendamentoService.agendarEntrega(e);
                nfService.emitirNotaFiscal(e);
                System.out.println("Sucesso!");
            } catch (Exception ex) {
                System.out.println("ERRO: " + ex.getMessage());
            }
        }
    }

    private void listarEntregas() {
        List<Entrega> lista = agendamentoService.listarEntregas();
        if (lista.isEmpty()) System.out.println("Vazio.");
        else for (Entrega e : lista) System.out.println(e);
    }
    
    private void preCarregarDados() {
        // Se não carregou nada do arquivo, cria um dummy
        if(clientesCadastrados.isEmpty())
            clientesCadastrados.add(new ClienteEmpresarial("Loja Teste", "0001", "9999"));
        if(motoristasCadastrados.isEmpty())
            motoristasCadastrados.add(new Motorista("João Piloto", "12345678901"));
    }
}