# MS Product Catalog

Microsserviço de **Catálogo de Produtos** desenvolvido com **Clean Architecture**, **DDD**, **Hexagonal Architecture**,
**SOLID** e os Padrões
**CQS (Command Query Separation)** e **CQRS (Command Query Responsibility Segregation)**.

✨ **Foco**: Separação estrita de responsabilidades, inversão de dependência e domínio desacoplado da infraestrutura.

## 👨‍💻 Desenvolvido por Humano + IA

[![Gemini](https://img.shields.io/badge/Gemini%20CLI-8E75B2?style=flat-square&logo=googlegemini&logoColor=white)](https://gemini.google.com/)
[![Perplexity](https://img.shields.io/badge/Perplexity-1FB8CD?style=flat-square&logo=perplexity&logoColor=white)](https://www.perplexity.ai/)
[![Claude](https://img.shields.io/badge/Claude-D97757?style=flat-square&logo=claude&logoColor=white)](https://anthropic.com/claude)

> **Feito com muito café** ☕ **+ conhecimento humano** 🧠 **e auxílio de IAs** para ideias, pesquisas e refinamentos
> rápidos.

---

## 📊 Quality & Security

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=wallanpsantos_ms-product-catalog&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=wallanpsantos_ms-product-catalog)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=wallanpsantos_ms-product-catalog&metric=coverage)](https://sonarcloud.io/summary/new_code?id=wallanpsantos_ms-product-catalog)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=wallanpsantos_ms-product-catalog&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=wallanpsantos_ms-product-catalog)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=wallanpsantos_ms-product-catalog&metric=bugs)](https://sonarcloud.io/summary/new_code?id=wallanpsantos_ms-product-catalog)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=wallanpsantos_ms-product-catalog&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=wallanpsantos_ms-product-catalog)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=wallanpsantos_ms-product-catalog&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=wallanpsantos_ms-product-catalog)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=wallanpsantos_ms-product-catalog&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=wallanpsantos_ms-product-catalog)

[//]: # ([![Technical Debt]&#40;https://sonarcloud.io/api/project_badges/measure?project=wallanpsantos_ms-product-catalog&metric=sqale_index&#41;]&#40;https://sonarcloud.io/summary/new_code?id=wallanpsantos_ms-product-catalog&#41;)

## 🔄 CI/CD

[![Build and Test](https://github.com/wallanpsantos/ms-product-catalog/actions/workflows/build-and-test.yml/badge.svg)](https://github.com/wallanpsantos/ms-product-catalog/actions/workflows/build-and-test.yml)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-8.0-green?logo=mongodb)](https://www.mongodb.com/)
[![GraalVM](https://img.shields.io/badge/GraalVM-Native%20Image-orange)](https://www.graalvm.org/)
[![Docker Compose](https://img.shields.io/badge/Docker%20Compose-blue?logo=docker)](https://docs.docker.com/compose/)
[![Gradle](https://img.shields.io/badge/Gradle-9.3.1-blue?logo=gradle)](https://gradle.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📖 Sobre o Projeto

O `ms-product-catalog` é um microsserviço de catálogo de produtos que implementa:

- **Arquitetura Hexagonal (Ports & Adapters):** isolamento total entre o núcleo de negócio e os detalhes de
  infraestrutura.
- **Domain-Driven Design (DDD):** Aggregate Root, Value Objects, Domain Services e Notification Pattern.
- **CQRS com Domain Bypass:** separação física de adaptadores de leitura e escrita, com o lado de leitura ignorando
  completamente as entidades de domínio ricas (bypass direto para DTOs).
- **CQS no nível de código:** todos os métodos de comando retornam `void` ou apenas identificadores; nunca retornam
  dados de leitura.
- **GraalVM Native Image:** compilação AOT para startup ultrarrápido (~50–200ms) e footprint de memória mínimo.

![Arquitetura Hexagonal](docs/images/ArquiteturaHexagonal.png)
> *Fonte da Imagem: [Domain-Driven Hexagon](https://github.com/Sairyss/domain-driven-hexagon)*

---

## 🚀 Tecnologias

| Categoria             | Tecnologia                       | Versão mínima               |
|-----------------------|----------------------------------|-----------------------------|
| Linguagem             | Java                             | 21                          |
| Framework             | Spring Boot                      | 4.0.3                       |
| Banco de Dados        | MongoDB                          | 8.0.11                      |
| Compilação Nativa     | GraalVM Native Image             | 21.0.2                      |
| Resiliência           | Resilience4j (Circuit Breaker)   | 2025.1.0 (via Spring Cloud) |
| Documentação API      | SpringDoc OpenAPI 3 (Swagger UI) | 3.0.1                       |
| Análise de Qualidade  | SonarQube Cloud                  | —                           |
| Testes Unitários      | JUnit + AssertJ + Mockito        | via Spring Boot + 3.27.7    |
| Testes de Integração  | Testcontainers (MongoDB)         | via Spring Boot             |
| Testes de API Externa | Contract Stub Runner (WireMock)  | 2025.1.0 (via Spring Cloud) |
| Testes E2E            | REST Assured                     | 6.0.0                       |
| Testes de Arquitetura | ArchUnit                         | 1.4.1                       |
| Cobertura de Código   | JaCoCo                           | via Gradle                  |
| Build                 | Gradle (Kotlin DSL)              | 9.3.1                       |

---

## 🛠️ Instalação e Execução

### Pré-requisitos

* JDK 21 instalado.
* Docker e Docker Compose **ou** Podman instalados no ambiente local.

### Passos para rodar localmente

**1. Clone o repositório:**

```bash
git clone https://github.com/wallanpsantos/ms-product-catalog.git
cd ms-product-catalog
```

**2. Configure as variáveis de ambiente:**

Crie um arquivo `.env` na raiz do projeto (já listado no `.gitignore`):

```bash
cp .env.example .env  # ou crie manualmente
```

```env
MONGO_ROOT_USERNAME=admin
MONGO_ROOT_PASSWORD=change-me-in-production
MONGO_DB_NAME=db-product-catalog
MONGO_EXPRESS_USER=admin
MONGO_EXPRESS_PASSWORD=change-me-in-production
```

**3. Suba a infraestrutura (MongoDB + Mongo Express):**

Com Docker:

```bash
docker compose up -d
```

Com Podman:

```bash
podman compose up -d
```

**4. Compile e execute a aplicação:**

```bash
./gradlew bootRun
```

Ou compile o JAR e execute:

```bash
./gradlew clean build -x nativeCompile
java -jar build/libs/ms-product-catalog-1.0.0.jar
```

### Compilação Nativa (GraalVM)

Para compilar o executável nativo com startup ultrarrápido:

```bash
./gradlew nativeCompile
./build/native/nativeCompile/ms-product-catalog
```

> **Nota:** A compilação nativa pode levar alguns minutos. Em CI, o passo `nativeCompile` é ignorado com
`-x nativeCompile` para agilizar o pipeline.

---

## 🧪 Testes

Execute todos os testes (unitários + integração via Testcontainers):

```bash
./gradlew test
```

O relatório de cobertura JaCoCo é gerado automaticamente em `build/reports/jacoco/test/`.

Execute apenas testes de arquitetura:

```bash
./gradlew test --tests "*ArchTest*"
```

---

## 📖 Documentação da API

Após iniciar a aplicação, acesse:

| Interface     | URL                                                                            |
|---------------|--------------------------------------------------------------------------------|
| Swagger UI    | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| OpenAPI JSON  | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)         |
| Health Check  | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) |
| Mongo Express | [http://localhost:8081](http://localhost:8081)                                 |

---

## 🧭 Exemplos de Uso (cURL)

Um script completo com exemplos de chamadas para todos os endpoints está disponível em [
`docs/curl-examples.sh`](docs/curl-examples.sh).

### Criar um Produto

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Smartphone Galaxy S24",
    "description": "256GB, Titanium Gray",
    "category": "Eletronicos",
    "brand": "Samsung",
    "price": 5999.00,
    "active": true
  }'
```

### Buscar por ID

```bash
curl http://localhost:8080/api/v1/products/{id}
```

### Listar Produtos Ativos (com paginação)

```bash
curl "http://localhost:8080/api/v1/products?page=0&perPage=10&sort=name&dir=asc"
```

### Pesquisar Produtos

```bash
curl -X POST http://localhost:8080/api/v1/products/search \
  -H "Content-Type: application/json" \
  -d '{ "query": "samsung" }'
```

### Criar em Lote

```bash
curl -X POST http://localhost:8080/api/v1/products/batch \
  -H "Content-Type: application/json" \
  -d '[
    { "name": "Produto A", "description": "Desc A", "category": "Cat", "brand": "Brand", "price": 100.0, "active": true },
    { "name": "Produto B", "description": "Desc B", "category": "Cat", "brand": "Brand", "price": 200.0, "active": true }
  ]'
```

### Desativar (Soft Delete)

```bash
curl -X DELETE http://localhost:8080/api/v1/products/{id}
# HTTP 204 No Content
```

---

## 🏛️ Arquitetura

### Visão Geral — CQRS com Domain Bypass

```mermaid
flowchart TB
    Client(["🌐 Client (HTTP)"])
    Client --> Controller

    subgraph Infrastructure ["⚙️ Infrastructure"]
        Controller["ProductController\n(Driving Adapter)"]

        subgraph Adapters ["MongoDB Adapters"]
            direction TB
            CMD["ProductMongoCommandAdapter\nimplements ProductCommandGateway"]
            QRY["ProductMongoQueryAdapter\nimplements ProductQueryGateway"]
        end
    end

    subgraph Application ["📦 Application Layer"]
        direction LR
        subgraph Commands ["Commands (Write)"]
            C1["CreateProduct"]
            C2["UpdateProduct"]
            C3["DeactivateProduct"]
        end
        subgraph Queries ["Queries (Read)"]
            Q1["GetProductById"]
            Q2["ListActiveProducts"]
            Q3["SearchProducts"]
        end
    end

    subgraph Domain ["🔷 Domain (Zero Dependencies)"]
        AGG["Product\n(Aggregate Root)"]
        VAL["ProductValidator"]
        AGG --> VAL
    end

    DB[("🍃 MongoDB")]
    Controller --> Commands
    Controller --> Queries
    Commands --> CMD
    Queries --> QRY
    CMD -->|" load / persist\nAggregate "| AGG
    CMD <-->|" INSERT / UPDATE "| DB
    QRY -->|" Domain Bypass\nProduct.java ignorado "| DB
    DB -->|" ProductDocument\n→ ProductSummary (DTO) "| QRY
    style Domain fill: #1a3a5c, color: #fff, stroke: #4a9ede
    style Application fill: #1a4a2e, color: #fff, stroke: #4aaa6e
    style Infrastructure fill: #3a2a1a, color: #fff, stroke: #cc8844
    style Commands fill: #0d3320, color: #fff, stroke: #4aaa6e
    style Queries fill: #0d3320, color: #fff, stroke: #4aaa6e
    style Adapters fill: #2a1a0a, color: #fff, stroke: #cc8844
    style QRY fill: #1a3a1a, color: #90ee90, stroke: #4aaa6e
    style CMD fill: #3a1a1a, color: #ffaaaa, stroke: #cc4444

```

### Fluxo de Leitura — Domain Bypass Total

```mermaid
sequenceDiagram
    participant API as Controller
    participant UC as GetProductById (Query)
    participant GW as ProductMongoQueryAdapter
    participant DB as MongoDB
    Note over UC, DB: DOMAIN BYPASS — Product.java nunca é instanciado
    API ->> UC: execute(Input)
    UC ->> GW: findSummaryById(id)
    GW ->> DB: findById(id)
    DB -->> GW: ProductDocument
    GW ->> GW: toSummary(Document)
    GW -->> UC: ProductSummary (DTO)
    UC -->> API: Output (DTO)
```

### Fluxo de Escrita — Agregado Rico

```mermaid
sequenceDiagram
    participant API as Controller
    participant UC as UpdateProduct (Command)
    participant GW as ProductMongoCommandAdapter
    participant Dom as Product (Aggregate)
    participant DB as MongoDB
    API ->> UC: execute(Input)
    UC ->> GW: findById(id)
    GW ->> DB: findById(id)
    DB -->> GW: Document
    GW -->> UC: Product (Entity)
    UC ->> Dom: update(dados)
    Dom ->> Dom: validate()
    UC ->> GW: update(Product)
    GW ->> DB: save(Document)
    UC -->> API: Output(id apenas)
```

---

## 📊 Sequence Diagrams por Endpoint

<details>
<summary>POST /api/v1/products — Criar Produto</summary>

```mermaid
sequenceDiagram
    autonumber
    participant C as Client (HTTP)
    participant CTRL as ProductController
    participant UC as DefaultCreateProductUseCase
    participant DOM as Product (Domain)
    participant VAL as ProductValidator
    participant GW as ProductMongoCommandAdapter
    participant DB as MongoDB
    C ->> CTRL: POST /products (JSON)
    CTRL ->> UC: execute(Input)
    UC ->> DOM: newProduct(...)
    UC ->> DOM: validate(Notification)
    DOM ->> VAL: validate()
    alt Notification has Errors
        UC -->> CTRL: Throw NotificationException
        CTRL -->> C: 422 Unprocessable Entity
    else Success
        UC ->> GW: create(Product)
        GW ->> DB: save(Document)
        DB -->> GW: Saved Document
        GW -->> UC: Product (Persisted)
        UC -->> CTRL: Output(ID)
        CTRL -->> C: 201 Created + Location Header
    end
```

</details>

<details>
<summary>POST /api/v1/products/batch — Criar em Lote</summary>

```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant CTRL as Controller
    participant UC as BatchUseCase
    participant DOM as Product
    participant GW as Gateway
    participant DB as MongoDB
    C ->> CTRL: POST /batch (List<Request>)
    CTRL ->> UC: execute(List<Input>)
    loop For each Item
        UC ->> DOM: newProduct(...)
        UC ->> DOM: validate()
        opt Error
            UC -->> CTRL: Throw NotificationException
        end
    end
    UC ->> GW: createAll(List<Product>)
    GW ->> DB: saveAll(List<Document>)
    DB -->> GW: Saved Documents
    GW -->> UC: List<Product>
    UC -->> CTRL: List<Output>
    CTRL -->> C: 201 Created (List<Response>)
```

</details>

<details>
<summary>GET /api/v1/products/{id} — Busca por ID</summary>

```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant CTRL as Controller
    participant UC as GetByIdUseCase
    participant GW as QueryAdapter
    participant DB as MongoDB
    C ->> CTRL: GET /products/{id}
    CTRL ->> UC: execute(id)
    UC ->> GW: findSummaryById(id)
    GW ->> DB: findOne(id)
    alt Found
        DB -->> GW: Document
        GW -->> UC: ProductSummary
        UC -->> CTRL: Output
        CTRL -->> C: 200 OK
    else Not Found
        DB -->> GW: null
        UC -->> CTRL: Throw NotFoundException
        CTRL -->> C: 404 Not Found
    end
```

</details>

<details>
<summary>GET /api/v1/products — Listagem Paginada</summary>

```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant CTRL as Controller
    participant UC as ListUseCase
    participant GW as QueryAdapter
    participant DB as MongoDB
    C ->> CTRL: GET /?page=0&perPage=10
    CTRL ->> UC: execute(Pageable)
    UC ->> GW: findAllActiveSummary(Pageable)
    GW ->> DB: find({active: true}).skip(0).limit(10)
    DB -->> GW: List<Document>
    GW -->> UC: Page<ProductSummary>
    UC -->> CTRL: Page<Output>
    CTRL -->> C: 200 OK (Page JSON)
```

</details>

<details>
<summary>PUT /api/v1/products/{id} — Atualizar Produto</summary>

```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant CTRL as Controller
    participant UC as UpdateUseCase
    participant DOM as Product
    participant GW as CommandAdapter
    participant DB as MongoDB
    C ->> CTRL: PUT /products/{id}
    CTRL ->> UC: execute(Input)
    UC ->> GW: findById(id)
    alt Not Found
        GW -->> UC: empty
        UC -->> CTRL: Throw NotFoundException
        CTRL -->> C: 404 Not Found
    else Found
        GW -->> UC: Product (Existing)
        UC ->> DOM: update(fields...)
        UC ->> DOM: validate(Notification)
        UC ->> GW: update(Product)
        GW ->> DB: save(Document)
        UC -->> CTRL: Output(id)
        CTRL -->> C: 200 OK
    end
```

</details>

<details>
<summary>PUT /api/v1/products/batch — Atualizar em Lote (Bulk Read Anti N+1)</summary>

```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant CTRL as Controller
    participant UC as BatchUpdateUC
    participant GW as CommandAdapter
    participant DB as MongoDB
    C ->> CTRL: PUT /batch (List<UpdateReq>)
    CTRL ->> UC: execute(List<Input>)
    Note right of UC: 1. Busca em Massa (evita N+1)
    UC ->> GW: findAllById(ids)
    GW ->> DB: find({_id: {$in: ids}})
    DB -->> GW: List<Documents>
    Note right of UC: 2. Processamento em Memória
    alt Missing IDs
        UC -->> CTRL: Throw NotFoundException
    else All Found
        UC ->> UC: Update & Validate (Loop)
        UC ->> GW: updateAll(List<Product>)
        GW ->> DB: saveAll(Documents)
        GW -->> UC: List<Product>
        UC -->> CTRL: List<Output>
        CTRL -->> C: 200 OK
    end
```

</details>

<details>
<summary>DELETE /api/v1/products/{id} — Desativar (Soft Delete)</summary>

```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant CTRL as Controller
    participant UC as DeactivateUC
    participant DOM as Product
    participant GW as CommandAdapter
    participant DB as MongoDB
    C ->> CTRL: DELETE /products/{id}
    CTRL ->> UC: execute(id)
    UC ->> GW: findById(id)
    alt Found
        GW -->> UC: Product
        UC ->> DOM: deactivate()
        UC ->> GW: update(Product)
        GW ->> DB: save(Document)
        UC -->> CTRL: void
        CTRL -->> C: 204 No Content
    else Not Found
        UC -->> CTRL: Throw NotFoundException
        CTRL -->> C: 404 Not Found
    end
```

</details>

<details>
<summary>POST /api/v1/products/search — Pesquisa Textual</summary>

```mermaid
sequenceDiagram
    autonumber
    participant C as Client
    participant CTRL as Controller
    participant UC as SearchUseCase
    participant GW as QueryAdapter
    participant DB as MongoDB
    C ->> CTRL: POST /search (query="samsung")
    CTRL ->> UC: execute(Input)
    UC ->> GW: searchProductsSummary("samsung")
    Note right of GW: Regex Case-Insensitive<br/>Campos: name, description, category, brand
    GW ->> DB: find({ $or: [...], active: true })
    DB -->> GW: List<Document>
    GW -->> UC: List<ProductSummary>
    UC -->> CTRL: List<Output>
    CTRL -->> C: 200 OK (Results)
```

</details>

---

## 🔒 Segurança & DevSecOps

- **SonarQube Cloud:** análise contínua de vulnerabilidades, bugs e code smells em cada push.
- **Dependabot:** monitoramento automático do grafo de dependências via GitHub Dependency Graph.
- **Gradle Wrapper Validation:** o checksum do `gradle-wrapper.jar` é validado contra o registro oficial do Gradle antes
  de qualquer compilação no CI, prevenindo ataques à cadeia de suprimento.
- **Imagem Docker não-root:** o container executa com usuário `appuser` (sem privilégios de root).
- **Secrets via variáveis de ambiente:** credenciais nunca são hardcoded; gerenciadas por `.env` (gitignored) ou um
  secrets manager em produção.
- **`no-new-privileges`:** opção Docker que impede que processos internos ao container adquiram privilégios adicionais
  pós-startup.

---

## 📂 Documentação Adicional

| Documento                                                   | Descrição                                            |
|-------------------------------------------------------------|------------------------------------------------------|
| [Estrutura do Projeto](docs/PROJECT_STRUCTURE.md)           | Detalhamento de cada camada e decisões arquiteturais |
| [Padrões CQS & CQRS](docs/referencias/PATTERNS_CQS_CQRS.md) | Guia de arquitetura com exemplos e diagramas         |
| [curl-examples.sh](docs/curl-examples.sh)                   | Scripts prontos para testar todos os endpoints       |

---

## 📄 Licença

Veja [`LICENSE`](LICENSE) para mais informações.