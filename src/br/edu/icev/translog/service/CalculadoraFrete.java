package br.edu.icev.translog.service;

import br.edu.icev.translog.model.Carga;
import br.edu.icev.translog.model.Cliente;

public class CalculadoraFrete {


    private static final double PRECO_BASE_POR_KM = 0.92; 

    
    public double calcular(Cliente cliente, Carga carga, double distanciaKm) {
        
        // (distancia * preco base * fator do peso)
        //fator classificacao carga leve(1.0), media(1.5), pesada(2.0)
        double precoBruto = distanciaKm * PRECO_BASE_POR_KM * carga.getTipo().getFatorPreco();


        if (carga.ehCargaEspecial()) {
            precoBruto += (precoBruto * 0.40); //acrescenta 40%
        }
        //desconto do cliente
        double valorDesconto = precoBruto * cliente.obterDesconto();

        return precoBruto - valorDesconto;
    }
}