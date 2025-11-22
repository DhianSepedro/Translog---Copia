package br.edu.icev.translog.main;

import br.edu.icev.translog.view.JanelaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        //interface swing
        SwingUtilities.invokeLater(() -> {
            JanelaPrincipal tela = new JanelaPrincipal();
            tela.setVisible(true);
        });
    }
}