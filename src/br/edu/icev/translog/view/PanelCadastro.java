package br.edu.icev.translog.view;

import br.edu.icev.translog.model.*;
import br.edu.icev.translog.util.Validador; 
import javax.swing.*;
import java.awt.*;

public class PanelCadastro extends JPanel {
    
    private JanelaPrincipal janela;

    //campos cliente
    private JTextField txtNomeCli, txtDocCli, txtTelCli;
    private JRadioButton rbEmpresa, rbPrioridade;
    
    //campos motorista
    private JTextField txtNomeMot, txtCnhMot;

    public PanelCadastro(JanelaPrincipal janela) {
        this.janela = janela;
        setLayout(new GridLayout(1, 2, 10, 10)); 

        add(montarFormCliente());
        add(montarFormMotorista());
    }

    private JPanel montarFormCliente() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Novo Cliente"));

        txtNomeCli = new JTextField();
        txtDocCli = new JTextField();
        txtTelCli = new JTextField();
        
        txtDocCli.setToolTipText("Digite apenas números");

        rbEmpresa = new JRadioButton("Empresarial (10%)", true);
        rbPrioridade = new JRadioButton("Prioritário (20%)");
        
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbEmpresa); 
        grupo.add(rbPrioridade);

        JLabel lblDoc = new JLabel("CNPJ:");
        rbEmpresa.addActionListener(e -> lblDoc.setText("CNPJ:"));
        rbPrioridade.addActionListener(e -> lblDoc.setText("CPF:"));

        panel.add(new JLabel("Nome:")); panel.add(txtNomeCli);
        panel.add(lblDoc); panel.add(txtDocCli);
        panel.add(new JLabel("Telefone:")); panel.add(txtTelCli);
        panel.add(rbEmpresa); panel.add(rbPrioridade);

        JButton btnSalvar = new JButton("Cadastrar Cliente");
        btnSalvar.setBackground(new Color(173, 216, 230)); // Azul claro
        btnSalvar.addActionListener(e -> salvarCliente());
        panel.add(btnSalvar);

        return panel;
    }

    private JPanel montarFormMotorista() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Novo Motorista"));

        txtNomeMot = new JTextField();
        txtCnhMot = new JTextField();
        txtCnhMot.setToolTipText("Digite os 11 dígitos da CNH");

        panel.add(new JLabel("Nome:")); panel.add(txtNomeMot);
        panel.add(new JLabel("CNH:")); panel.add(txtCnhMot);
        
        panel.add(new JLabel("")); panel.add(new JLabel(""));
        panel.add(new JLabel("")); 

        JButton btnSalvar = new JButton("Cadastrar Motorista");
        btnSalvar.setBackground(new Color(173, 216, 230));
        btnSalvar.addActionListener(e -> salvarMotorista());
        panel.add(btnSalvar);

        return panel;
    }

    private void salvarCliente() {
        String nome = txtNomeCli.getText().trim();
        String doc = txtDocCli.getText().trim();
        String tel = txtTelCli.getText().trim();

        //verifica campo vazio
        if(nome.isEmpty() || doc.isEmpty() || tel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //valida CPF ou CNPJ com digitos verificadores
        if (rbEmpresa.isSelected()) {
            if (!Validador.isCNPJ(doc)) {
                JOptionPane.showMessageDialog(this, "CNPJ Inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            if (!Validador.isCPF(doc)) {
                JOptionPane.showMessageDialog(this, "CPF Inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        //valida telefone
        if (!Validador.isTelefone(tel)) {
            JOptionPane.showMessageDialog(this, "Telefone Inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //salva cliente
        Cliente novo;
        String docLimpo = doc.replaceAll("\\D", ""); 

        if (rbEmpresa.isSelected()) {
            novo = new ClienteEmpresarial(nome, docLimpo, tel);
        } else {
            novo = new ClientePrioritario(nome, docLimpo, tel);
        }

        janela.getClientes().add(novo);
        JOptionPane.showMessageDialog(this, "✅ Cliente cadastrado com sucesso!");
        txtNomeCli.setText(""); txtDocCli.setText(""); txtTelCli.setText("");
    }

    private void salvarMotorista() {
        String nome = txtNomeMot.getText().trim();
        String cnh = txtCnhMot.getText().trim();

        if(nome.isEmpty() || cnh.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
            return;
        }

        //valida CNH, validacao simples apenas
        if (!Validador.isCNH(cnh)) {
            JOptionPane.showMessageDialog(this, "CNH Inválida!\nDeve conter exatamente 11 dígitos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //salva o motorista
        janela.getMotoristas().add(new Motorista(nome, cnh));
        JOptionPane.showMessageDialog(this, "✅ Motorista cadastrado!");
        
        txtNomeMot.setText(""); txtCnhMot.setText("");
    }
}