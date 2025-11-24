# JSONS PARA TESTES

Aqui estão os JSON para usarmos na apresentação para testar o sistema.

## Users

### Criar Cliente (Client)
**POST** `/users`
```json
{
  "name": "Jair Inácio",
  "email": "jair.inacio@gmail.com"
}
```

### Criar Entregador (Delivery Person)
**POST** `/users`
```json
{
  "name": "Lula Messias",
  "email": "lula.messias@gmail.com"
}
```

## Restaurant

### Criar Restaurante
**POST** `/restaurants`
```json
{
  "name": "MC Donalds",
  "phone": "84996173514",
  "address": {
    "street": "Av. Engenheiro Roberto Freire",
    "city": "Natal",
    "state": "RN",
    "zipCode": "59090000",
    "country": "Brazil",
    "latitude": -5.878352,
    "longitude": -35.209492
  }
}
```

## Items

### Criar Item
**POST** `/items`
```json
{
  "name": "Big Mac",
  "price": 35.0,
  "preparationTime": 15
}
```

### Adicionar Item ao Menu do Restaurante
**POST** `/restaurants/{restaurantId}/menu/items?itemId={itemId}`
*(Sem corpo JSON, apenas parâmetros na URL)*

## Orders

### Criar Pedido
**POST** `/orders`
```json
{
  "clientId": 1,
  "restaurantId": 1,
  "paymentMethod": "CREDIT_CARD",
  "cart": {
      "cartItems": [
        {
          "id": 1,
          "name": "Big Mac",
          "price": 35.0,
          "preparationTime": 15
        }
      ]
  },
  "originAddress": {
    "street": "Av. Engenheiro Roberto Freire",
    "city": "Natal",
    "state": "RN",
    "zipCode": "59090000",
    "country": "Brazil",
    "latitude": -5.878352,
    "longitude": -35.209492
  },
  "destinationAddress": {
    "street": "Rua da Casa do João",
    "city": "Natal",
    "state": "RN",
    "zipCode": "59000000",
    "country": "Brazil",
    "latitude": -5.800000,
    "longitude": -35.200000
  }
}
```

### Aceitar Pedido (Restaurante)
**POST** `/restaurants/{restaurantId}/orders/{orderId}/accept`
*(Sem corpo JSON)*

### Finalizar Preparação (Restaurante)
**POST** `/restaurants/{restaurantId}/orders/{orderId}/finish-preparation`
*(Sem corpo JSON)*

### Atribuir Entregador (Pedido)
**POST** `/orders/{orderId}/assign-delivery?deliveryManId={deliveryManId}`
*(Sem corpo JSON, apenas parâmetros na URL)*

### Atualizar Status do Pedido
**PATCH** `/orders/{orderId}/status?newStatus={status}`
*Status possíveis: PENDING, PROCESSING, COMPLETED, CANCELLED, READY_FOR_DELIVERY, OUT_FOR_DELIVERY*
*(Sem corpo JSON, apenas parâmetros na URL)*
