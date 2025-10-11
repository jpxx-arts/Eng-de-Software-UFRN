# Princípios de Projeto

Este documento é um relatório que trata sobre a utilização de princípios de projetos no desenvolvimento do sistema Tomaladaka. Os princípios discutidos e planejados neste trabalho são: **Single Responsibility Principle**, **Interface Segregation Principle**, **Dependency Injection Principle** e **Liskov Substitution Principle**.

Sendo este projeto uma aplicação fullstack, alguns princípios já são intrínsecos aos frameworks (Django, Spring boot, Angular...) utilizados. Ou seja, os frameworks já os utilizam nativamente. No entanto, tentaremos abordar de uma forma agnóstica de tecnologia (até porque as tecnologias nem foram definidas ainda), mas utilizando exemplos em alguma linguagem e nos inspirando em algum framework. Outro detalhe é que também levaremos em consideração alguns padrões de projeto (repository, service...) que sugerem a aplicação desses princípios.

Além dos padrões mencionados, serão utilizados os diagramas de classe e de casos de uso previamente elaborados como base para a análise da aplicação dos princípios. Abaixo estão os diagramas:

![Diagrama de classe](diagrams/diagrama_de_classes.drawio.svg)
Diagrama 1: Diagrama de classe

![Diagrama de casos de uso](diagrams/diagrama_casos_usuario.drawio.svg)
Diagrama 2: Diagrama de casos de uso

## Single Responsibility Principle

Seguindo a definição do **Single Responsibility Principle**, temos que criar módulos com uma função bem definida para cada um. No Diagrama 1, temos métodos que exemplificam isso, como `addToOrder(Order, Item)`,  `requestDelivery(Restaurant, Order)`, `addItem(item)`. Como o próprio nome desses métodos sugerem, temos que cada um possui uma função específica. Abaixo, o código mostra a implementação de um método conforme sua única responsabilidade.
```python
class Client:
    # ...

    def add_to_order(order: Order, item: Item):
        order.add_item(item)

    #...
```

Além disso, a própria construção de entidades é um forma de criar abstração com suas funções definidas.

A utilização de padrões como *Repository* também pode ser enxergada como a utilização deste princípio, pois podemos definir métodos específicos para acesso ao banco de dados. No exemplo abaixo, temos uma classe `OrderRepository` com a função de fazer uma interface com o banco de dados e um método que possui a única função de retornar uma lista de `Order`'s vinculadas a um usuário.

```python
class OrderRepository(Repository):
    # ...
    def get_orders_by_u_number(self, u_number: str) -> list[Order]:
        return Order.objects.filter(client__u_number=u_number).all()
    # ...
```

## Interface Segregation Principle

Podemos utilizar o princípio de segregação de interface na criação de classes que reutilizam funcionalidades, mas que podem definir também outras funcionalidades que não são comuns às suas inferfaces herdadas.

Podemos utilizar o exemplo do `Repository` já mencionado para ilustrar isso.

Definimos uma interface `Repository` para criar métodos de CRUD. Após isso, apenas basta utilizar a interface no `Repository`'s de cada entidade. Exemplo:

```python
# repository.py

class Repository:

    def  __init__(self, entity):
        self.entity = entity
    
    def read(self, entity_id):
        return self.entity.objects.find(entity_id)
    
    def list(self):
        return self.entity.objects.all()
    
    def create(self, **kwargs):
        entity = self.entity(**kwargs)
        return entity.save()
    
    #...
```

```python
# client_repository.py
import Repository

class ClientRepository(Repository):

    def get_client_by_phone(self, phone):
        return self.list().filter(phone=phone)
```

No `ClientRepository` temos as funcionalidades de CRUD já implementadas, além de outros métodos que possamos precisar. Assim, podemos estender o Repository facilmente para as outras entidades.

## Dependency Injection Principle

Podemos utilizar o princípio de injeção de dependência em partes do código que queremos reduzir o acoplamento e facilitar a subsituição de implementações.

Utilizando um `Service`, podemos passar um `Repository` no construtor para que a responsabilidade de definir qual `Repository` usado seja apenas na instanciação do `Service`. Assim, podemos alterar facilmente a interface com o banco de dados. Por exemplo, podemos usar diferentes repositories, um utilizando SQL, ou outra ferramenta de ORM, ou diferentes DBs...

Abaixo está uma ilustração de exemplo com `Service` e `Repository`:

```python
# client_service.py
class ClientService:
    def __init__(self, client_repository):
        self.client_repository = client_repository

    def list_clients(self):
        return self.client_repository.list()

# main.py
client_repository = ClientSQLRepository(Client)
client_service = ClientService(client_repository)

print(client.service.list_clients())
```

## Liskov Substitution Principle

No diagrama de classes, temos que `Client` e `Restaurant` estendem de `User`, então a utilização de funcionalidades que são comuns a `User` nos permite utilizar a interface no lugar da classe pai.

Um exemplo de utilização, seria o de fazer uma tela do sistema para o `Client` e outra para o `Restaurant`, mas que as duas telas mostram o nome do `User`. Assim, podemos ter:

```typescript
// user.ts
class User{
    private name: string = "";

    public getName() {
        return this.name;
    }
}

// client.ts
class Client extends User{
    //...
}
// restaurant.ts

class Restaurant extends User{
    //...
}

// homeScreen.ts
export class HomeScreen{

    public displayName: string = "";

    constructor(user: User){
        this.displayName = user.getName();
    }
}

// main.ts
class ClientMain{
    private client: Client;
    
    constructor(){
        // aqui o princípio de substituição está
        // sendo usado, já que client herda user
        homeScreen = new HomeScreen(client)
    }
}

class RestaurantMain{
    private restaurant: Restaurant;
    
    constructor(){
        // aqui o princípio de substituição está
        // sendo usado, já que restaurant herda user
        homeScreen = new HomeScreen(restaurant)
    }
}
```

Com isso, garantimos que qualquer classe filha de `User` possa substituir sua classe base sem comprometer o comportamento esperado das demais partes do sistema.

## Conclusão

A aplicação dos princípios de projeto no sistema Tomaladaka contribui para a criação de uma arquitetura mais modular, extensível e de fácil manutenção, reduzindo o acoplamento e favorecendo a reutilização de código.