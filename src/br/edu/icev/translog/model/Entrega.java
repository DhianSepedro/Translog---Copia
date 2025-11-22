package br.edu.icev.translog.model;

import java.time.LocalDateTime;

public class Entrega {
    private Cliente cliente;
    private Motorista motorista;
    private Carga carga;
    private LocalDateTime dataHorario;
    private double valorFrete;

    public Entrega(Cliente cliente, Motorista motorista, Carga carga, LocalDateTime dataHorario) {
        this.cliente = cliente;
        this.motorista = motorista;
        this.carga = carga;
        this.dataHorario = dataHorario;
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

    public Carga getCarga() {
        return carga;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public LocalDateTime getDataHorario() {
        return dataHorario;
    }

    @Override
    public String toString() {
        return "Entrega para " + cliente.getNome() + 
               " | Motorista: " + motorista.getNome() + 
               " | Data: " + dataHorario + 
               " | Valor: R$ " + String.format("%.2f", valorFrete);
    }
}