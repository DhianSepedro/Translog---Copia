package br.edu.icev.translog.service;

import br.edu.icev.translog.model.Entrega;
import java.util.ArrayList;
import java.util.List;

public class Agendamento {

    
    private List<Entrega> entregasAgendadas = new ArrayList<>();

 
    public void agendarEntrega(Entrega novaEntrega) throws Exception {
        
        //bloqueio de horario, requisito
        for (Entrega agendada : entregasAgendadas) {
            if (agendada.getMotorista().getCnh().equals(novaEntrega.getMotorista().getCnh())) {
                // verifica se é o MESMO horario
                if (agendada.getDataHorario().equals(novaEntrega.getDataHorario())) {
                    throw new Exception("ERRO: Motorista " + novaEntrega.getMotorista().getNome() + 
                                      " já possui agendamento para " + novaEntrega.getDataHorario());
                }
            }
        }

        entregasAgendadas.add(novaEntrega);
        System.out.println(">> Sucesso: Entrega agendada para " + novaEntrega.getDataHorario());
    }

    public List<Entrega> listarEntregas() {
        return entregasAgendadas;
    }
}