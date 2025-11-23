package br.edu.icev.translog.main;

import br.edu.icev.translog.view.JanelaPrincipal;
// import br.edu.icev.translog.view.MenuConsole; // descomentar para usar console

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        //interface swing
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                //se falhar usa o padrao
            }
            
            JanelaPrincipal tela = new JanelaPrincipal();
            tela.setVisible(true);
        });

        //modo console, descomentar para usar
        /*
        MenuConsole menu = new MenuConsole();
        menu.iniciar();
        */
    }
}