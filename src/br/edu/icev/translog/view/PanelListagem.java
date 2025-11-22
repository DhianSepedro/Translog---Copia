package br.edu.icev.translog.view;

import br.edu.icev.translog.model.Entrega;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class PanelListagem extends JPanel {

    private JanelaPrincipal janela;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    public PanelListagem(JanelaPrincipal janela) {
        this.janela = janela;
        setLayout(new BorderLayout(10, 10));

        //configuraçao da tabela
        String[] colunas = {"Cliente", "Motorista", "Data/Hora", "Tipo Carga", "Valor (R$)"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabela = new JTable(modeloTabela);
        
        JScrollPane scroll = new JScrollPane(tabela);
        
        //botoes de controle
        JPanel panelBotoes = new JPanel();
        JButton btnAtualizar = new JButton("Atualizar Lista");
        JButton btnSalvarCsv = new JButton("💾 SALVAR TUDO EM CSV");
        btnSalvarCsv.setBackground(new Color(200, 255, 200)); // Verde claro

        btnAtualizar.addActionListener(e -> carregarDados());
        btnSalvarCsv.addActionListener(e -> janela.salvarTudo());

        panelBotoes.add(btnAtualizar);
        panelBotoes.add(btnSalvarCsv);

        add(new JLabel("Entregas Agendadas"), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0); //limpa tabela
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");

        for (Entrega e : janela.getAgendamentoService().listarEntregas()) {
            Object[] linha = {
                e.getCliente().getNome(),
                e.getMotorista().getNome(),
                e.getDataHorario().format(fmt),
                e.getCarga().getTipo(),
                String.format("%.2f", e.getValorFrete())
            };
            modeloTabela.addRow(linha);
        }
    }
}