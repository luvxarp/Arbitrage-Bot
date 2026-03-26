# Bot de Arbitragem Bitcoin - Binance vs Coinbase

Projeto que fiz pra estudar Spring Boot e Docker. A ideia é monitorar o preço do BTC nas duas exchanges e identificar quando tem diferença de preço suficiente pra ser lucrativo comprar em uma e vender na outra (arbitragem).

Rodei em container Docker e subi numa instância EC2. O processo foi bem tranquilo.

---

## Como funciona

A cada 8 segundos o bot busca o preço do BTC na Binance e na Coinbase, calcula o spread entre elas e loga no console se tiver alguma oportunidade.

```
[14:32:01] Verificando precos...
  BINANCE | compra: $67005.0 | venda: $67000.0
  COINBASE | compra: $67380.0 | venda: $67370.0
  Spread: 0.544% - sem oportunidade no momento
```

---

## Endpoints

| Endpoint                     | O que faz                          |
|------------------------------|------------------------------------|
| `/arbitragem`                | Status atual + ultimo spread       |
| `/arbitragem/historico`      | Todos os registros (ultimos 50)    |
| `/arbitragem/oportunidades`  | So os que tiveram spread bom       |

---

## Como rodar

**Com Docker (recomendado):**
```bash
docker compose up --build
```

**Sem Docker (so pra testar rapido):**
```bash
mvn spring-boot:run
```

Acessa: http://localhost:8080/arbitragem

---

## Deploy na EC2

```bash
# 1. copia os arquivos pra instancia
scp -i sua-chave.pem -r . ubuntu@SEU_IP_EC2:~/arb-bot

# 2. entra na instancia e sobe
ssh -i sua-chave.pem ubuntu@SEU_IP_EC2
cd arb-bot
docker compose up -d --build
```

Lembrando de liberar a porta 8080 no Security Group da EC2.

---

## O que aprendi com isso

Fiz isso depois de estudar AWS Lambda e quis testar o Docker também.

O Lambda resolveria bem se eu quisesse só verificar o spread uma vez por trigger (ex: via EventBridge). Mas como esse bot precisa ficar rodando continuamente e manter um histórico em memória, o Docker em EC2 fez mais sentido.

---

## TODOs / ideias

- [ ] Persistir o histórico em banco (agora perde quando reinicia)
- [ ] Notificação por email/telegram quando achar oportunidade boa
- [ ] Calcular taxas de forma mais precisa
- [ ] Adicionar mais pares além do BTC

---

> Obs: isso é só pra estudo, não é recomendação de investimento. Arbitragem real é bem mais complicada que isso.
