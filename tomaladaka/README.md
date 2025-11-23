# Tomaladaka - Sistema de Delivery de Comida

**Tomaladaka** é uma API RESTful desenvolvida com Spring Boot para gerenciar um sistema de delivery de comida. Este projeto simula as operações essenciais de um aplicativo de entrega, conectando clientes, restaurantes e entregadores.

## 📋 Sobre o Projeto

O **Tomaladaka** foi projetado para demonstrar boas práticas de desenvolvimento de software, utilizando tecnologias em Java. O sistema permite:

*   **Gerenciamento de Restaurantes**: Cadastro, atualização de cardápios e gestão de pedidos.
*   **Gerenciamento de Clientes**: Cadastro e realização de pedidos.
*   **Fluxo de Pedidos**: Desde a criação do carrinho, processamento, preparação até a entrega.
*   **Rastreamento**: Monitoramento do status do pedido em tempo real.

## 🚀 Tecnologias Utilizadas

*   **Java 21**
*   **Spring Boot 3**: Framework para a API.
*   **Spring Data JPA**: Para persistência de dados.
*   **H2 Database**: Banco de dados em memória para desenvolvimento e testes.
*   **Docker & Docker Compose**: Para containerização e implantação.
*   **Swagger (SpringDoc)**: Para documentação interativa da API.
*   **Lombok**: Para redução de código boilerplate.

## 🛠️ Instalação e Requisitos

Ferramentas necessárias:

*   **Java JDK 21**
*   **Maven 3.9+**
*   **Docker** e **Docker Compose**

### Clonando o Repositório

```bash
git clone https://github.com/seu-usuario/tomaladaka.git
cd tomaladaka
```

## ▶️ Como Rodar o Projeto

O projeto pode ser executado de duas formas: usando Docker (recomendado) ou localmente com Maven.

### Opção 1: Via Docker

Não requer a instalação do Java ou Maven na máquina local, apenas o Docker.

1.  Na raiz do projeto, execute:
    ```bash
    docker compose up -d --build
    ```
2.  Aguarde alguns instantes até que o container inicie.
3.  A aplicação estará disponível em `http://localhost:8080`.

### Opção 2: Localmente via Maven

1.  Compile e rode o projeto:
    ```bash
    mvn spring-boot:run
    ```
2.  A aplicação estará disponível em `http://localhost:8080`.

## 📖 Documentação da API (Swagger)

O projeto possui documentação automática e interativa gerada pelo Swagger. Após iniciar a aplicação, acesse:

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Lá você poderá testar todos os endpoints, ver os modelos de dados e entender os códigos de resposta.

## 📂 Estrutura do Projeto

A estrutura de diretórios segue o padrão do Spring Boot:

```
tomaladaka/
├── src/
│   ├── main/
│   │   ├── java/com/umbrella/tomaladaka/
│   │   │   ├── controller/   # Controladores REST (Endpoints)
│   │   │   ├── service/      # Regras de Negócio
│   │   │   ├── model/        # Entidades JPA e Modelos de Domínio
│   │   │   ├── repository/   # Interfaces de Acesso a Dados (JPA)
│   │   │   ├── dto/          # Objetos de Transferência de Dados (Requests/Responses)
│   │   │   └── TomaladakaApplication.java # Classe Principal
│   │   └── resources/
│   │       └── application.properties # Configurações da Aplicação
├── Dockerfile                # Configuração da Imagem Docker
├── docker-compose.yml        # Orquestração dos Containers
├── pom.xml                   # Dependências do Maven
└── README.md                 # Documentação do Projeto
```

## 🧩 Principais Componentes

### Controllers
*   **RestaurantController**: Gerencia operações de restaurantes (criar, listar, aceitar pedidos).
*   **OrderController**: Gerencia o ciclo de vida dos pedidos (criar, atualizar status, atribuir entregador).
*   **UserController**: Gerencia usuários (clientes e entregadores).
*   **ItemController**: Gerencia itens do cardápio.

### Services
*   **RestaurantService**: Lógica para criação de restaurantes e manipulação de cardápios.
*   **OrderService**: Lógica complexa de pedidos, cálculo de totais e transições de status.
*   **UserService**: CRUD de usuários.

### Models
*   **Restaurant**: Entidade que representa um estabelecimento.
*   **Order**: Entidade central que liga clientes, restaurantes e itens.
*   **User**: Representa tanto clientes quanto entregadores.
*   **Item**: Itens de comida vendidos pelos restaurantes.

## 🧪 Testando com JSONs Prontos

Arquivo com payloads JSONs prontos para testes:

📄 **[TEST_JSONS.md](TEST_JSONS.md)**

---
Desenvolvido com ☕ e Spring Boot.