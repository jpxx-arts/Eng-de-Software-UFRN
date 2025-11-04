#!/bin/sh

# ==================================================================
echo "### 1. Criando Itens, Usuários e Restaurante (Sintaxe SH) ###"
# ==================================================================

# Criar Itens
echo "Criando Itens..."
ITEM_ID_1=$(curl -s -X POST http://localhost:8080/items \
  -H "Content-Type: application/json" \
  -d '{"name": "Pizza Calabresa", "price": 45.0, "preparationTime":10}' | jq -r '.id')

ITEM_ID_2=$(curl -s -X POST http://localhost:8080/items \
  -H "Content-Type: application/json" \
  -d '{"name": "Refrigerante 2L", "price": 10.0, "preparationTime":10}' | jq -r '.id')

echo "  -> Item 1 (Pizza) ID: $ITEM_ID_1"
echo "  -> Item 2 (Refri) ID: $ITEM_ID_2"

# Criar Usuários (Cliente e Entregador)
echo "Criando Usuários..."
CLIENT_ID=$(curl -s -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name": "João Cliente", "email": "joao.cliente@email.com"}' | jq -r '.id')

DELIVERY_MAN_ID=$(curl -s -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Maria Entregadora", "email": "maria.entregadora@email.com"}' | jq -r '.id')

echo "  -> Cliente ID: $CLIENT_ID"
echo "  -> Entregador ID: $DELIVERY_MAN_ID"

# Criar Restaurante (e seu Menu automático)
echo "Criando Restaurante..."
RESTAURANT_ID=$(curl -s -X POST http://localhost:8080/restaurants \
  -H "Content-Type: application/json" \
  -d '{"name": "Pizzaria Umbrella", "phone": "9999-8888", "address": {"street": "Rua da Chuva", "city": "Natal", "state": "RN", "zipCode": "59000-000", "country": "Brasil", "latitude": -5.123, "longitude":-32.123}}' | jq -r '.id')

echo "  -> Restaurante ID: $RESTAURANT_ID"

# ==================================================================
echo "### 2. Populando o Menu do Restaurante ###"
# ==================================================================

echo "Adicionando Item 1 (Pizza) ao menu do Restaurante $RESTAURANT_ID..."
curl -s -X POST "http://localhost:8080/restaurants/$RESTAURANT_ID/menu/items?itemId=$ITEM_ID_1" | jq

echo "Adicionando Item 2 (Refri) ao menu do Restaurante $RESTAURANT_ID..."
curl -s -X POST "http://localhost:8080/restaurants/$RESTAURANT_ID/menu/items?itemId=$ITEM_ID_2" | jq

echo "Verificando o Restaurante $RESTAURANT_ID (o menu deve estar populado):"
curl -s "http://localhost:8080/restaurants/$RESTAURANT_ID" | jq

# ==================================================================
echo "### 3. Criando um Pedido (Fluxo de Compra) - DTO Corrigido ###"
# ==================================================================

echo "Enviando novo pedido..."
ORDER_ID=$(curl -s -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
        "clientId": '"$CLIENT_ID"',
        "restaurantId": '"$RESTAURANT_ID"',
        "paymentMethod": "CREDIT_CARD",
        "originAddress": {
            "street": "Rua da Chuva",
            "city": "Natal",
            "state": "RN",
            "zipCode": "59000-000",
            "country": "Brasil",
            "latitude": -5.123,
            "longitude":-32.123
        },
        "destinationAddress": {
            "street": "Rua do Cliente",
            "city": "Natal",
            "state": "RN",
            "zipCode": "59000-123",
            "country": "Brasil",
            "latitude": -5.223,
            "longitude":-32.323
        },
        "cart": {
            "cartItems": [
                { "id": '"$ITEM_ID_1"', "name": "Pizza Calabresa", "price": 45.0, "preparationTime": 10 },
                { "id": '"$ITEM_ID_2"', "name": "Refrigerante 2L", "price": 10.0, "preparationTime": 10 }
            ]
        }
      }' | jq -r '.id')

echo "  -> Pedido Criado com ID: $ORDER_ID"

echo "Verificando o Pedido $ORDER_ID (Status deve ser PENDING):"
curl -s "http://localhost:8080/orders/$ORDER_ID" | jq '.status'

# ==================================================================
echo "### 4. Testando o Ciclo de Vida do Pedido ###"
# ==================================================================

echo "Restaurante $RESTAURANT_ID aceitando Pedido $ORDER_ID..."
curl -s -X POST "http://localhost:8080/restaurants/$RESTAURANT_ID/orders/$ORDER_ID/accept" | jq '.status'

echo "Restaurante $RESTAURANT_ID finalizando preparação do Pedido $ORDER_ID..."
curl -s -X POST "http://localhost:8080/restaurants/$RESTAURANT_ID/orders/$ORDER_ID/finish-preparation" | jq '.status'

echo "Designando Entregador $DELIVERY_MAN_ID ao Pedido $ORDER_ID..."
curl -s -X POST "http://localhost:8080/orders/$ORDER_ID/assign-delivery?deliveryManId=$DELIVERY_MAN_ID" | jq '.status'

echo "Verificando o Pedido $ORDER_ID (Status deve ser OUT_FOR_DELIVERY):"
curl -s "http://localhost:8080/orders/$ORDER_ID" | jq

# ==================================================================
echo "### 5. Testando Endpoints de Listagem e CRUD ###"
# ==================================================================

echo "Listando pedidos com status OUT_FOR_DELIVERY:"
curl -s "http://localhost:8080/orders?status=OUT_FOR_DELIVERY" | jq

echo "Listando todos os restaurantes:"
curl -s "http://localhost:8080/restaurants" | jq

echo "Listando todos os usuários:"
curl -s "http://localhost:8080/users" | jq

echo "Atualizando nome do Cliente $CLIENT_ID..."
curl -s -X PUT "http://localhost:8080/users/$CLIENT_ID" \
  -H "Content-Type: application/json" \
  -d '{"name": "João Cliente da Silva", "email": "joao.cliente@email.com"}' | jq

echo "Criando Pedido 2 para teste de Cancelamento..."
ORDER_ID_2=$(curl -s -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
        "clientId": '"$CLIENT_ID"',
        "restaurantId": '"$RESTAURANT_ID"',
        "paymentMethod": "PIX",
        "cart": {"cartItems": []},
        "originAddress": {
            "street": "Rua da Chuva",
            "city": "Natal",
            "state": "RN",
            "zipCode": "59000-000",
            "country": "Brasil",
            "latitude": -5.123,
            "longitude":-32.123
        },
        "destinationAddress": {
            "street": "Rua do Cliente",
            "city": "Natal",
            "state": "RN",
            "zipCode": "59000-123",
            "country": "Brasil",
            "latitude": -5.223,
            "longitude":-32.323
        }
      }' | jq -r '.id')

echo "$ORDER_ID_2"

echo "  -> Pedido 2 Criado com ID: $ORDER_ID_2"
echo "Cancelando Pedido $ORDER_ID_2..."
curl -s -X PATCH "http://localhost:8080/orders/$ORDER_ID_2/status?newStatus=CANCELLED" | jq '.status'

echo "Tentando deletar Pedido $ORDER_ID_2 (deve falhar de propósito)..."
curl -s -X DELETE "http://localhost:8080/orders/$ORDER_ID_2" | jq

echo "Tentando deletar Cliente $CLIENT_ID (deve falhar de propósito)..."
curl -s -X DELETE "http://localhost:8080/users/$CLIENT_ID" | jq

echo "### Testes Concluídos ###"
