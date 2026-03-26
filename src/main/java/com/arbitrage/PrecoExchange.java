package com.arbitrage;

// classe simples pra guardar o preco que busquei de cada exchange
public class PrecoExchange {

    public String exchange;
    public double precoCompra;  // ask - quanto eu pago pra comprar
    public double precoVenda;   // bid - quanto eu recebo se vender
    public String horario;

    public PrecoExchange(String exchange, double precoCompra, double precoVenda, String horario) {
        this.exchange = exchange;
        this.precoCompra = precoCompra;
        this.precoVenda = precoVenda;
        this.horario = horario;
    }

    @Override
    public String toString() {
        return exchange + " | compra: $" + precoCompra + " | venda: $" + precoVenda;
    }
}
