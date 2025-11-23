package br.edu.icev.translog.service;

import br.edu.icev.translog.model.Entrega;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Agendamento {


    private List<Entrega> entregasAgendadas = new ArrayList<>();

    //agenda nova entrega
    public void agendarEntrega(Entrega nova) throws Exception {
        
        //calcula a "duracao" da nova entrega, a cada 100km bloqueia 1h, arredonda pra cima
        long duracaoNovaHoras = (long) Math.ceil(nova.getDistanciaKm() / 100.0);
        if (duracaoNovaHoras < 1) duracaoNovaHoras = 1;

        //bloqueia o horario
        LocalDateTime inicioNova = nova.getDataHorario();
        LocalDateTime fimNova = inicioNova.plusHours(duracaoNovaHoras);

        //verifica os possiveis conflitos
        for (Entrega agendada : entregasAgendadas) {
            
            //verifica se é o mesmo motorista
            if (agendada.getMotorista().getCnh().equals(nova.getMotorista().getCnh())) {
                
                //confere que horas termina a entrega agendada
                long duracaoAgendadaHoras = (long) Math.ceil(agendada.getDistanciaKm() / 100.0);
                if (duracaoAgendadaHoras < 1) duracaoAgendadaHoras = 1;

                LocalDateTime inicioAgendada = agendada.getDataHorario();
                LocalDateTime fimAgendada = inicioAgendada.plusHours(duracaoAgendadaHoras);

                //verifica se os horarios se sobrepoem
                if (inicioNova.isBefore(fimAgendada) && inicioAgendada.isBefore(fimNova)) {
                    
                    throw new Exception("CONFLITO DE ROTA:\n" + 
                                      "O motorista " + nova.getMotorista().getNome() + " já possui entrega neste horário.\n" +
                                      "Entrega Existente: " + inicioAgendada + " até " + fimAgendada + "\n" +
                                      "(Bloqueio de 1h a cada 100km)");
                }
            }
        }

        //se horario liberado, agenda
        entregasAgendadas.add(nova);
        System.out.println(">> Agendado com sucesso! Bloqueio estimado: " + duracaoNovaHoras + " horas.");
    }

    public List<Entrega> listarEntregas() {
        return entregasAgendadas;
    }
}