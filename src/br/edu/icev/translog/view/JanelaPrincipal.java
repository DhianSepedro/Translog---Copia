package br.edu.icev.translog.view;

import br.edu.icev.translog.model.*;
import br.edu.icev.translog.service.*;
import br.edu.icev.translog.persistencia.*;

import javax.swing.*;
//import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JanelaPrincipal extends JFrame {

    //guarda os dados em memória
    private List<Cliente> clientesCadastrados;
    private List<Motorista> motoristasCadastrados;
    
    private Agendamento agendamentoService; 
    private CalculadoraFrete calculadora;
    private NotaFiscal notaFiscalService;
    
    private ClienteRepository repoCliente;
    private MotoristaRepository repoMotorista;
    private EntregaRepository repoEntrega;

    public JanelaPrincipal() {
        super("Sistema Translog - Gestão Logística");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        inicializarSistema();

        //configura as abas
        JTabbedPane tabbedPane = new JTabbedPane();
        
        //adiciona os paineis
        tabbedPane.addTab("Cadastros", new PanelCadastro(this));
        tabbedPane.addTab("Nova Entrega", new PanelEntrega(this));
        tabbedPane.addTab("Relatórios", new PanelListagem(this));

        add(tabbedPane);
    }

    private void inicializarSistema() {
        clientesCadastrados = new ArrayList<>();
        motoristasCadastrados = new ArrayList<>();
        
        agendamentoService = new Agendamento();
        calculadora = new CalculadoraFrete();
        notaFiscalService = new NotaFiscal();
        
        repoCliente = new ClienteRepository();
        repoMotorista = new MotoristaRepository();
        repoEntrega = new EntregaRepository();

        //dados de teste
        clientesCadastrados.add(new ClienteEmpresarial("Tech Solucoes", "0001", "9999"));
        motoristasCadastrados.add(new Motorista("Joao Silva", "CNH-B"));
    }

    //acessando os servicos
    public List<Cliente> getClientes() { return clientesCadastrados; }
    public List<Motorista> getMotoristas() { return motoristasCadastrados; }
    public Agendamento getAgendamentoService() { return agendamentoService; }
    public CalculadoraFrete getCalculadora() { return calculadora; }
    public NotaFiscal getNotaFiscalService() { return notaFiscalService; }

    //um unico metodo para chamar e salvar tudo
    public void salvarTudo() {
        repoCliente.salvar(clientesCadastrados);
        repoMotorista.salvar(motoristasCadastrados);
        repoEntrega.salvarTudo(agendamentoService.listarEntregas());
        JOptionPane.showMessageDialog(this, "Dados salvos em CSV com sucesso!");
    }
}