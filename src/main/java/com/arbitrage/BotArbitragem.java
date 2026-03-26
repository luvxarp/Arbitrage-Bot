package com.arbitrage;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BotArbitragem {

    @Value("${binance.url}")
    String binanceUrl;

    @Value("${coinbase.url}")
    String coinbaseUrl;

    @Value("${par.binance}")
    String parBinance;

    @Value("${par.coinbase}")
    String parCoinbase;

    // guardo os ultimos resultados aqui mesmo, simples
    List<ResultadoArbitragem> historico = new ArrayList<>();

    PrecoExchange ultimoBinance;
    PrecoExchange ultimoCoinbase;
    int totalVerificacoes = 0;
    int totalOportunidades = 0;

    // roda de 8 em 8 segundos conforme coloquei no properties
    @Scheduled(fixedDelayString = "${intervalo.ms}")
    public void verificar() {
        System.out.println("\n[" + horarioAgora() + "] Verificando precos...");

        PrecoExchange binance = buscarBinance();
        PrecoExchange coinbase = buscarCoinbase();

        if (binance == null || coinbase == null) {
            System.out.println("  Nao consegui buscar os precos, tentando de novo na proxima...");
            return;
        }

        ultimoBinance = binance;
        ultimoCoinbase = coinbase;
        totalVerificacoes++;

        System.out.println("  " + binance);
        System.out.println("  " + coinbase);

        // descubro qual ta mais barata pra comprar
        PrecoExchange maisBarata = binance.precoCompra < coinbase.precoCompra ? binance : coinbase;
        PrecoExchange maisCara   = binance.precoVenda  > coinbase.precoVenda  ? binance : coinbase;

        ResultadoArbitragem resultado = new ResultadoArbitragem(maisBarata, maisCara);

        // salvo no historico (mantenho so os ultimos 50 pra nao estourar memoria)
        historico.add(0, resultado);
        if (historico.size() > 50) {
            historico.remove(historico.size() - 1);
        }

        if (resultado.temOportunidade) {
            totalOportunidades++;
            System.out.println("  *** OPORTUNIDADE! Comprar " + resultado.comprarEm
                    + " @ $" + resultado.precoCompra
                    + " | Vender " + resultado.venderEm
                    + " @ $" + resultado.precoVenda);
            System.out.println("  Spread: " + String.format("%.3f", resultado.spreadPercent)
                    + "% | Lucro est. (sim $1000): $"
                    + String.format("%.2f", resultado.lucroEstimado));
        } else {
            System.out.println("  Spread: " + String.format("%.3f", resultado.spreadPercent)
                    + "% - sem oportunidade no momento");
        }
    }

    // busca o preco na binance usando a api publica deles
    // docs: https://binance-docs.github.io/apidocs/spot/en/#symbol-order-book-ticker
    PrecoExchange buscarBinance() {
        try {
            WebClient client = WebClient.create(binanceUrl);

            JsonNode json = client.get()
                    .uri("/api/v3/ticker/bookTicker?symbol=" + parBinance)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            double ask = json.get("askPrice").asDouble(); // preco pra COMPRAR
            double bid = json.get("bidPrice").asDouble(); // preco pra VENDER

            return new PrecoExchange("BINANCE", ask, bid, horarioAgora());

        } catch (Exception e) {
            System.out.println("  [ERRO Binance] " + e.getMessage());
            return null;
        }
    }

    // mesma coisa pra coinbase, so que eles tem uma api diferente
    // endpoint: /products/{id}/ticker
    PrecoExchange buscarCoinbase() {
        try {
            WebClient client = WebClient.create(coinbaseUrl);

            JsonNode json = client.get()
                    .uri("/products/" + parCoinbase + "/ticker")
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            // na coinbase o ticker so tem "price", nao tem bid/ask direto
            // entao uso o price como referencia pros dois lados por enquanto
            // TODO: ver se tem outro endpoint com bid/ask separado
            double price = json.get("price").asDouble();
            double bid   = json.has("bid") ? json.get("bid").asDouble() : price;
            double ask   = json.has("ask") ? json.get("ask").asDouble() : price;

            return new PrecoExchange("COINBASE", ask, bid, horarioAgora());

        } catch (Exception e) {
            System.out.println("  [ERRO Coinbase] " + e.getMessage());
            return null;
        }
    }

    String horarioAgora() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
