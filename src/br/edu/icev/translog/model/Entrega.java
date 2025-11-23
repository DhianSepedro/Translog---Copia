package br.edu.icev.translog.model;

import java.time.LocalDateTime;

public class Entrega {
    private Cliente cliente;
    private Motorista motorista;
    private Carga carga;
    private LocalDateTime dataHorario;
    private double valorFrete;
    
    //distancia em km para bloquear no agendamento
    private double distanciaKm;

    public Entrega(Cliente cliente, Motorista motorista, Carga carga, LocalDateTime dataHorario, double distanciaKm) {
        this.cliente = cliente;
        this.motorista = motorista;
        this.carga = carga;
        this.dataHorario = dataHorario;
        this.distanciaKm = distanciaKm;
    }


    public void setValorFrete(double valorFrete) {
        this.valorFrete = valorFrete;
    }

    public double getValorFrete() {
        return valorFrete;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public Carga getCarga() {
        return carga;
    }

    public LocalDateTime getDataHorario() {
        return dataHorario;
    }
    
    public double getDistanciaKm() {
        return distanciaKm;
    }

    @Override
    public String toString() {
        return "Entrega: " + cliente.getNome() + 
               " | Mot: " + motorista.getNome() + 
               " | Dist: " + distanciaKm + "km" +
               " | R$ " + String.format("%.2f", valorFrete);
    }
}