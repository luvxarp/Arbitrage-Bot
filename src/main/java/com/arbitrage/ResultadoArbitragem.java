package com.arbitrage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResultadoArbitragem {

    public String comprarEm;
    public String venderEm;
    public double precoCompra;
    public double precoVenda;
    public double spreadPercent;
    public double lucroEstimado;   // simulando com $1000
    public boolean temOportunidade;
    public String horario;

    // calculo basico: compra numa, vende na outra
    // ainda nao to descontando as taxas direito, TODO melhorar isso
    public ResultadoArbitragem(PrecoExchange barata, PrecoExchange cara) {
        this.comprarEm = barata.exchange;
        this.venderEm = cara.exchange;
        this.precoCompra = barata.precoCompra;
        this.precoVenda = cara.precoVenda;

        this.spreadPercent = ((cara.precoVenda - barata.precoCompra) / barata.precoCompra) * 100;

        // simulando com mil dolares
        double qtdBTC = 1000.0 / barata.precoCompra;
        double recebo = qtdBTC * cara.precoVenda;
        // taxa binance 0.1% + coinbase 0.6% = 0.7% no total
        double taxas = (1000.0 * 0.001) + (recebo * 0.006);
        this.lucroEstimado = recebo - 1000.0 - taxas;

        this.temOportunidade = spreadPercent >= 0.5 && lucroEstimado > 0;
        this.horario = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
