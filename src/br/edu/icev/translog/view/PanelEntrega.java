package br.edu.icev.translog.view;

import br.edu.icev.translog.model.*;
import javax.swing.*;
import javax.swing.text.MaskFormatter; 
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PanelEntrega extends JPanel {

    private JanelaPrincipal janela;
    
    // Componentes da Tela
    private JComboBox<String> cbClientes;
    private JComboBox<String> cbMotoristas;
    private JTextField txtPeso;
    private JTextField txtDistancia;
    private JComboBox<TipoCarga> cbTipoCarga;
    private JCheckBox chkEspecial;
    private JFormattedTextField txtDataEntrega; 
    private JLabel lblValorCalculado;

    public PanelEntrega(JanelaPrincipal janela) {
        this.janela = janela;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // --- PAINEL DE FORMULÁRIO (GRID) ---
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // Configuração dos Combos
        cbClientes = new JComboBox<>();
        cbMotoristas = new JComboBox<>();
        
        JButton btnAtualizarListas = new JButton("🔄 Recarregar Listas");
        btnAtualizarListas.setBackground(new Color(240, 248, 255));
        btnAtualizarListas.addActionListener(e -> atualizarCombos());

        txtPeso = new JTextField("0");
        txtDistancia = new JTextField("0");
        
        // Combo de Tipo de Carga (Bloqueado para edição manual)
        cbTipoCarga = new JComboBox<>(TipoCarga.values());
        cbTipoCarga.setEnabled(false); 
        cbTipoCarga.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) setText(value.toString() + " (Automático)");
                return this;
            }
        });

        chkEspecial = new JCheckBox("Carga Frágil/Perigosa (+40%)");

        // Configuração da Máscara de Data (dd/MM/yyyy HH:mm)
        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/#### ##:##");
            mascaraData.setPlaceholderCharacter('_');
            txtDataEntrega = new JFormattedTextField(mascaraData);
        } catch (Exception e) {
            txtDataEntrega = new JFormattedTextField();
        }
        // Preenche com a data/hora atual
        txtDataEntrega.setValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        // --- EVENTOS DE CÁLCULO AUTOMÁTICO ---
        // Quando sair do campo de peso, define o tipo de carga
        txtPeso.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) { classificarCargaPeloPeso(); }
        });

        // Recalcula valor se mudar cliente ou checkbox
        cbClientes.addActionListener(e -> tentarCalcular());
        chkEspecial.addActionListener(e -> tentarCalcular());

        // --- ADICIONA COMPONENTES AO LAYOUT ---
        form.add(new JLabel("Cliente:")); form.add(cbClientes);
        form.add(new JLabel("Motorista:")); form.add(cbMotoristas);
        form.add(new JLabel("")); form.add(btnAtualizarListas); // Espaço vazio + Botão
        
        form.add(new JLabel("DIGITE O PESO (kg):")); form.add(txtPeso);
        form.add(new JLabel("Distância (km):")); form.add(txtDistancia);
        
        form.add(new JLabel("Classificação (Auto):")); form.add(cbTipoCarga);
        form.add(new JLabel("Adicionais:")); form.add(chkEspecial);
        
        form.add(new JLabel("Data da Entrega (Dia/Mês/Ano Hora:Min):")); form.add(txtDataEntrega);

        // --- PAINEL DE BOTÕES (INFERIOR) ---
        JPanel panelBotoes = new JPanel(new FlowLayout());
        
        JButton btnCalcular = new JButton("VERIFICAR VALOR");
        btnCalcular.setBackground(new Color(230, 230, 250));
        
        JButton btnAgendar = new JButton("CONFIRMAR ENTREGA");
        btnAgendar.setBackground(new Color(144, 238, 144)); // Verde Claro
        btnAgendar.setFont(new Font("Arial", Font.BOLD, 12));

        lblValorCalculado = new JLabel("Valor: R$ 0.00");
        lblValorCalculado.setFont(new Font("Arial", Font.BOLD, 20));
        lblValorCalculado.setForeground(new Color(0, 100, 0));

        btnCalcular.addActionListener(e -> calcularComAviso());
        btnAgendar.addActionListener(e -> agendar());

        panelBotoes.add(btnCalcular);
        panelBotoes.add(Box.createHorizontalStrut(20)); // Espaçamento
        panelBotoes.add(lblValorCalculado);
        panelBotoes.add(Box.createHorizontalStrut(20));
        panelBotoes.add(btnAgendar);

        // Montagem Final
        add(new JLabel("Solicitação de Frete (Regra: Bloqueio de 1h a cada 100km)"), BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);
        
        atualizarCombos(); // Carrega a lista na primeira vez
    }

    // --- LÓGICA AUXILIAR ---

    private void classificarCargaPeloPeso() {
        try {
            String textoPeso = txtPeso.getText().replace(",", ".");
            if (textoPeso.isEmpty()) return;
            double peso = Double.parseDouble(textoPeso);
            
            if (peso <= 10) cbTipoCarga.setSelectedItem(TipoCarga.LEVE);
            else if (peso <= 100) cbTipoCarga.setSelectedItem(TipoCarga.MEDIA);
            else cbTipoCarga.setSelectedItem(TipoCarga.PESADA);
            
            tentarCalcular(); // Já tenta atualizar o preço
        } catch (NumberFormatException e) { }
    }

    private void atualizarCombos() {
        cbClientes.removeAllItems();
        for(Cliente c : janela.getClientes()) {
            cbClientes.addItem(c.getNome() + " | " + c.getCpfCnpj());
        }
        
        cbMotoristas.removeAllItems();
        for(Motorista m : janela.getMotoristas()) {
            cbMotoristas.addItem(m.getNome());
        }
    }

    private void tentarCalcular() { try { executarLogicaCalculo(); } catch (Exception e) { } }

    private void calcularComAviso() {
        try {
            classificarCargaPeloPeso();
            executarLogicaCalculo();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Peso e Distância devem ser números válidos!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void executarLogicaCalculo() {
        if (cbClientes.getSelectedIndex() < 0 || janela.getClientes().isEmpty()) return;

        Cliente cli = janela.getClientes().get(cbClientes.getSelectedIndex());
        double peso = Double.parseDouble(txtPeso.getText().replace(",", "."));
        double dist = Double.parseDouble(txtDistancia.getText().replace(",", "."));
        TipoCarga tipo = (TipoCarga) cbTipoCarga.getSelectedItem();
        boolean especial = chkEspecial.isSelected();

        //nova carga
        Carga carga = new Carga(peso, tipo, especial); 
        
        //calculo de frete
        double valor = janela.getCalculadora().calcular(cli, carga, dist);
        lblValorCalculado.setText(String.format("Valor: R$ %.2f", valor));
    }

    private void agendar() {
        try {
            //valida seleção
            if (cbClientes.getSelectedIndex() < 0 || cbMotoristas.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(this, "Selecione Cliente e Motorista!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            //classifica o peso correto
            classificarCargaPeloPeso();

            //dados do formulario
            Cliente cli = janela.getClientes().get(cbClientes.getSelectedIndex());
            Motorista mot = janela.getMotoristas().get(cbMotoristas.getSelectedIndex());
            
            double peso = Double.parseDouble(txtPeso.getText().replace(",", "."));
            double dist = Double.parseDouble(txtDistancia.getText().replace(",", "."));
            TipoCarga tipo = (TipoCarga) cbTipoCarga.getSelectedItem();
            boolean especial = chkEspecial.isSelected();

            //data da entrega
            String textoData = txtDataEntrega.getText();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime data;
            try {
                data = LocalDateTime.parse(textoData, fmt);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Data Inválida! Verifique o formato.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //objetos da entrega
            Carga carga = new Carga(peso, tipo, especial);
            double valor = janela.getCalculadora().calcular(cli, carga, dist);
            
            //cria a entrega
            Entrega entrega = new Entrega(cli, mot, carga, data, dist);
            entrega.setValorFrete(valor);

            //tenta agendar
            janela.getAgendamentoService().agendarEntrega(entrega);
            
            //emite nota fiscal
            janela.getNotaFiscalService().emitirNotaFiscal(entrega);

            //confirma entrega
            long horasBloqueio = (long) Math.ceil(dist / 100.0);
            JOptionPane.showMessageDialog(this, 
                "✅ Entrega Confirmada!\n" +
                "Valor Final: R$ " + String.format("%.2f", valor) + "\n" +
                "Motorista Bloqueado por: " + horasBloqueio + " horas (Baseado em " + dist + "km)");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique os números digitados (Peso/Distância).", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            //pega o erro do agendamento, caso houver bloqueio
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao Agendar", JOptionPane.ERROR_MESSAGE);
        }
    }
}