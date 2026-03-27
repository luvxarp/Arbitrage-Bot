# Bot de Arbitragem Bitcoin

Esse projeto eu consumi a API da Binance e da Coinbase

Projeto que fiz estudando Spring Boot e Docker. A ideia é monitorar o preço do BTC nas duas exchanges e identificar quando tem diferença de preço suficiente pra ser lucrativo comprar em uma e vender na outra.

Feito com container Docker, pra ser possível subir em um EC2.

---

## Como funciona

A cada 8 segundos o bot busca o preço do Bitcoin na Binance e na Coinbase, calcula o spread entre elas e loga no console se tiver alguma oportunidade.

Isso aparece no terminal bem dessa forma:
```
Verificando precos...
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

**Com o Docker:**
```bash
docker compose up --build
```

**Sem Docker (so pra teste rapido e local):**
```bash
mvn spring-boot:run
```

Adiciona no final do link /arbitragem, dessa forma: http://localhost:8080/arbitragem

---

## Deploy no EC2

```bash
# 1. copia os arquivos pra instancia
scp -i (sua key).pem -r . ubuntu@SeuIpDoEC2:~/arb-bot

# 2. entra nela e sobe pelo ssh
ssh -i (sua key).pem ubuntu@SeuIpDoEC2
cd arb-bot
docker compose up -d --build
```

---

## Ideias

- [ ] Persistir o histórico em banco (agora perde quando reinicia)
- [ ] Notificação por algum app de mensagem quando achar oportunidade
- [ ] Calcular taxas de forma mais precisa
- [ ] Adicionar mais pares além do BTC e permitir a escolha, mesmo que o programa ainda não terá interface já que é um projeto inteiramente backend
