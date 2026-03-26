package com.arbitrage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ArbBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArbBotApplication.class, args);

        System.out.println("=========================================");
        System.out.println("  Bot de Arbitragem Bitcoin - Rodando!");
        System.out.println("  Binance <-> Coinbase");
        System.out.println("  http://localhost:8080/arbitragem");
        System.out.println("=========================================");
    }
}
