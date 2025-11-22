package br.edu.icev.translog.view;

import br.edu.icev.translog.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDateTime;

public class PanelEntrega extends JPanel {

    private JanelaPrincipal janela;
    
    private JComboBox<String> cbClientes;
    private JComboBox<String> cbMotoristas;
    
    private JTextField txtPeso;
    private JTextField txtDistancia;
    private JComboBox<TipoCarga> cbTipoCarga;
    private JCheckBox chkEspecial;
    private JTextField txtHoras;
    
    private JLabel lblValorCalculado;

    public PanelEntrega(JanelaPrincipal janela) {
        this.janela = janela;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        
        //selecao
        cbClientes = new JComboBox<>();
        cbMotoristas = new JComboBox<>();
        JButton btnAtualizarListas = new JButton("🔄 Recarregar Listas");
        btnAtualizarListas.addActionListener(e -> atualizarCombos());

        txtPeso = new JTextField("0");
        txtDistancia = new JTextField("0");
        
        //campo de tipo de carga automatico
        cbTipoCarga = new JComboBox<>(TipoCarga.values());
        cbTipoCarga.setEnabled(false);
        cbTipoCarga.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(value.toString() + " (Automático)");
                    setForeground(Color.DARK_GRAY);
                }
                return this;
            }
        });

        chkEspecial = new JCheckBox("Carga Frágil/Perigosa (+40%)");
        txtHoras = new JTextField("2");

        //try de calcular peso automatico
        txtPeso.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                classificarCargaPeloPeso();
            }
        });

        //calculando automatico o frete
        cbClientes.addActionListener(e -> tentarCalcular());
        chkEspecial.addActionListener(e -> tentarCalcular());

        //formularios
        form.add(new JLabel("Cliente:")); form.add(cbClientes);
        form.add(new JLabel("Motorista:")); form.add(cbMotoristas);
        form.add(new JLabel("")); form.add(btnAtualizarListas);
        
        form.add(new JLabel("DIGITE O PESO (kg):")); form.add(txtPeso); // Destaque
        form.add(new JLabel("Distância (km):")); form.add(txtDistancia);
        
        //explicacao peso automatico
        form.add(new JLabel("Classificação (Auto):")); form.add(cbTipoCarga);
        
        form.add(new JLabel("Adicionais:")); form.add(chkEspecial);
        form.add(new JLabel("Agendar para (horas):")); form.add(txtHoras);

        //botoes-
        JPanel panelBotoes = new JPanel(new FlowLayout());
        
        JButton btnCalcular = new JButton("VERIFICAR VALOR");
        btnCalcular.setBackground(new Color(230, 230, 250));
        
        JButton btnAgendar = new JButton("CONFIRMAR ENTREGA");
        btnAgendar.setBackground(new Color(144, 238, 144));

        lblValorCalculado = new JLabel("Valor: R$ 0.00");
        lblValorCalculado.setFont(new Font("Arial", Font.BOLD, 20));
        lblValorCalculado.setForeground(new Color(0, 100, 0));

        btnCalcular.addActionListener(e -> calcularComAviso());
        btnAgendar.addActionListener(e -> agendar());

        panelBotoes.add(btnCalcular);
        panelBotoes.add(Box.createHorizontalStrut(15));
        panelBotoes.add(lblValorCalculado);
        panelBotoes.add(Box.createHorizontalStrut(15));
        panelBotoes.add(btnAgendar);

        add(new JLabel("Solicitação de Frete (Classificação Automática por Peso)"), BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);
        
        atualizarCombos();
    }

    //classifica peso direto
    private void classificarCargaPeloPeso() {
        try {
            String textoPeso = txtPeso.getText().replace(",", ".");
            if (textoPeso.isEmpty()) return;

            double peso = Double.parseDouble(textoPeso);
            
            // REGRA DE NEGÓCIO: Separação nos tipos
            if (peso <= 10) {
                cbTipoCarga.setSelectedItem(TipoCarga.LEVE);
            } else if (peso <= 100) {
                cbTipoCarga.setSelectedItem(TipoCarga.MEDIA);
            } else {
                cbTipoCarga.setSelectedItem(TipoCarga.PESADA);
            }
            
            // Após definir o tipo, tenta calcular o preço
            tentarCalcular();
            
        } catch (NumberFormatException e) {
            //ignora letra 
        }
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

    private void tentarCalcular() {
        try {
            executarLogicaCalculo();
        } catch (Exception e) { }
    }

    private void calcularComAviso() {
        try {
            classificarCargaPeloPeso(); //classifica antes de calcular
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
        
        //tipo setado pelo peso
        TipoCarga tipo = (TipoCarga) cbTipoCarga.getSelectedItem();
        boolean especial = chkEspecial.isSelected();

        Carga carga = new Carga(peso, tipo, especial); 

        double valor = janela.getCalculadora().calcular(cli, carga, dist);
        lblValorCalculado.setText(String.format("Valor: R$ %.2f", valor));
    }

    private void agendar() {
        try {
            if (cbClientes.getSelectedIndex() < 0 || cbMotoristas.getSelectedIndex() < 0) {
                JOptionPane.showMessageDialog(this, "Selecione Cliente e Motorista!");
                return;
            }
            
            classificarCargaPeloPeso(); //classificacao do peso

            Cliente cli = janela.getClientes().get(cbClientes.getSelectedIndex());
            Motorista mot = janela.getMotoristas().get(cbMotoristas.getSelectedIndex());
            
            double peso = Double.parseDouble(txtPeso.getText().replace(",", "."));
            double dist = Double.parseDouble(txtDistancia.getText().replace(",", "."));
            TipoCarga tipo = (TipoCarga) cbTipoCarga.getSelectedItem();
            boolean especial = chkEspecial.isSelected();

            Carga carga = new Carga(peso, tipo, especial);

            int horas = Integer.parseInt(txtHoras.getText());
            LocalDateTime data = LocalDateTime.now().plusHours(horas);
            double valor = janela.getCalculadora().calcular(cli, carga, dist);

            Entrega entrega = new Entrega(cli, mot, carga, data);
            entrega.setValorFrete(valor);

            janela.getAgendamentoService().agendarEntrega(entrega);
            janela.getNotaFiscalService().emitirNotaFiscal(entrega);

            JOptionPane.showMessageDialog(this, "✅ Entrega Confirmada!\nTipo de Carga: " + tipo + "\nValor: R$ " + String.format("%.2f", valor));
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Verifique os números digitados.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao Agendar: " + ex.getMessage());
        }
    }
}