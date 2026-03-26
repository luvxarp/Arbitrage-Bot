package com.arbitrage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/arbitragem")
public class ArbController {

    @Autowired
    BotArbitragem bot;

    // endpoint principal - resumo do que ta acontecendo
    @GetMapping
    public Map<String, Object> status() {
        Map<String, Object> resposta = new HashMap<>();

        resposta.put("totalVerificacoes", bot.totalVerificacoes);
        resposta.put("totalOportunidades", bot.totalOportunidades);

        if (bot.ultimoBinance != null) {
            resposta.put("binance_compra", bot.ultimoBinance.precoCompra);
            resposta.put("binance_venda", bot.ultimoBinance.precoVenda);
        }

        if (bot.ultimoCoinbase != null) {
            resposta.put("coinbase_compra", bot.ultimoCoinbase.precoCompra);
            resposta.put("coinbase_venda", bot.ultimoCoinbase.precoVenda);
        }

        // pega o ultimo resultado se tiver
        if (!bot.historico.isEmpty()) {
            ResultadoArbitragem ultimo = bot.historico.get(0);
            resposta.put("ultimo_spread_percent", String.format("%.4f", ultimo.spreadPercent));
            resposta.put("ultimo_lucro_estimado", String.format("%.2f", ultimo.lucroEstimado));
            resposta.put("ultima_direcao", ultimo.comprarEm + " -> " + ultimo.venderEm);
            resposta.put("ultima_verificacao", ultimo.horario);
            resposta.put("tem_oportunidade_agora", ultimo.temOportunidade);
        }

        return resposta;
    }

    // historico dos ultimos resultados
    @GetMapping("/historico")
    public List<ResultadoArbitragem> historico() {
        return bot.historico;
    }

    // so as que tiveram oportunidade
    @GetMapping("/oportunidades")
    public List<ResultadoArbitragem> oportunidades() {
        return bot.historico.stream()
                .filter(r -> r.temOportunidade)
                .toList();
    }
}
