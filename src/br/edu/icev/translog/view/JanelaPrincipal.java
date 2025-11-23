package br.edu.icev.translog.view;

import br.edu.icev.translog.model.*;
import br.edu.icev.translog.service.*;
import br.edu.icev.translog.persistencia.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class JanelaPrincipal extends JFrame {

   //listas de dados em memória
    private List<Cliente> clientesCadastrados;
    private List<Motorista> motoristasCadastrados;
    
    //serviços do sistema
    private Agendamento agendamentoService; 
    private CalculadoraFrete calculadora;
    private NotaFiscal notaFiscalService;
    
    //persistencia de dados
    private ClienteRepository repoCliente;
    private MotoristaRepository repoMotorista;
    private EntregaRepository repoEntrega;

    public JanelaPrincipal() {
        super("Sistema Translog - Gestão Logística");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela

        //inicializa os dados e serviçoos
        inicializarSistema();

        JTabbedPane tabbedPane = new JTabbedPane();
        
        //adiciona os paineis
        tabbedPane.addTab("Cadastros", new PanelCadastro(this));
        tabbedPane.addTab("Nova Entrega", new PanelEntrega(this));
        tabbedPane.addTab("Relatório de Entregas", new PanelListagem(this));

        add(tabbedPane);
        
        //salva tudo ao fechar janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salvarTudo();
            }
        });
    }

    private void inicializarSistema() {
        repoCliente = new ClienteRepository();
        repoMotorista = new MotoristaRepository();
        repoEntrega = new EntregaRepository();
        
        System.out.println("Carregando Clientes e Motoristas...");
        clientesCadastrados = repoCliente.carregar();
        motoristasCadastrados = repoMotorista.carregar();
        
        if (clientesCadastrados.isEmpty()) {
            clientesCadastrados.add(new ClienteEmpresarial("Empresa Teste", "0001", "9999"));
        }
        if (motoristasCadastrados.isEmpty()) {
            motoristasCadastrados.add(new Motorista("Motorista Teste", "12345"));
        }

        agendamentoService = new Agendamento();
        calculadora = new CalculadoraFrete();
        notaFiscalService = new NotaFiscal();

    
        System.out.println("Recuperando histórico de entregas...");
        List<Entrega> historico = repoEntrega.carregar(clientesCadastrados, motoristasCadastrados);
        
       //restaura as entregas carregadas para o serviço de agendamento
        for (Entrega e : historico) {
            try {
                agendamentoService.listarEntregas().add(e); 
                
            } catch (Exception ex) {
                System.err.println("Falha ao restaurar entrega: " + ex.getMessage());
            }
        }
    }

    
    public List<Cliente> getClientes() { 
        return clientesCadastrados; 
    }

    public List<Motorista> getMotoristas() { 
        return motoristasCadastrados; 
    }

    public Agendamento getAgendamentoService() { 
        return agendamentoService; 
    }

    public CalculadoraFrete getCalculadora() { 
        return calculadora; 
    }

    public NotaFiscal getNotaFiscalService() { 
        return notaFiscalService; 
    }

    //salvamento de todos os dados em CSV
    public void salvarTudo() {
        System.out.println("Salvando dados em CSV...");
        repoCliente.salvar(clientesCadastrados);
        repoMotorista.salvar(motoristasCadastrados);
        repoEntrega.salvarTudo(agendamentoService.listarEntregas());
        
        System.out.println("Dados salvos com sucesso!");
    }
}