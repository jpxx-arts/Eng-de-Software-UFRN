# Design Patterns usados no Tomaladaka

Este documento trata dos design patterns usados no Tomaladaka: Builder, Facade e Singleton.

## Builder Pattern

Este pattern é implementado através da bilbioteca Lombok com a annotation `@Builder`. Ele ajuda a construir objetos passo a passo sem precisar fazer várias sobrecargas do construtor.

Exemplos:
1. In `User.java`:
```java
@Builder
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}
```

2. In `Order.java`:
```java
@Builder
@AllArgsConstructor
public class Order {
    private Long id;
    private User client;
    private Restaurant restaurant;
    // ... other fields
}
```

O padrão Builder permite a construção flexível de objetos, onde nem todos os parâmetros são necessários de uma só vez. Por exemplo, em `OrderService.java`:

```java
Order order = Order.builder()
    .client(client)
    .restaurant(restaurant)
    .paymentMethod(paymentMethod)
    .items(cart.getCartItems())
    .totalPrice(cart.getPrice())
    .build();
```

## Facade Pattern

O padrão Facade é implementado por meio das classes da camada de serviço, que fornecem uma interface simplificada para o complexo subsistema de repositórios e lógica de negócios.

Exemplo em `OrderService.java`:
```java
@Service
public class OrderService {
    private final OrderRepository orderRepo;
    private final RestaurantRepository restaurantRepo;
    private final UserRepository userRepo;
    private final ItemRepository itemRepo;

    //  lida com a lógica de criação
    public Order createOrderFromRequest(OrderRequest request) {
    }
}
```

Esse padrão oculta a complexidade da interação com múltiplos repositórios e regras de negócio.

## Singleton Pattern

O padrão Singleton é usado implicitamente por meio do sistema de injeção de dependência do Spring. Todos os serviços do Spring são, por padrão, beans Singleton.

Exemplos são os services:

```java
@Service
public class OrderService {
}
```

Esses serviços mantêm uma única instância durante todo o ciclo de vida da aplicação.
