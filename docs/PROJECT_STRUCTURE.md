# Estrutura do Projeto - MS Product Catalog

Este documento descreve a estrutura de diretórios e arquivos do projeto `ms-product-catalog`, que segue os princípios de **Clean Architecture (Hexagonal)** e **Domain-Driven Design (DDD)**. O projeto foi desenvolvido com Java 21, Spring Boot 4.0.3 e MongoDB, utilizando código Java puro ("Vanilla") sem dependências de Lombok ou MapStruct.

## Raiz do Projeto

- **`build.gradle.kts` / `settings.gradle.kts`**: Gerenciamento de dependências e build com Gradle (Kotlin DSL). Versão do Spring Boot: `4.0.3`. Spring Cloud: `2025.1.0`. SpringDoc: `3.0.1`. ArchUnit: `1.4.1`.
- **`gradlew` / `gradlew.bat`**: Wrapper do Gradle para execução sem instalação prévia. O checksum do `gradle-wrapper.jar` é validado automaticamente no pipeline de CI antes de qualquer compilação.
- **`Dockerfile`**: Build multi-stage. Stage 1 usa `ghcr.io/graalvm/native-image-community:21.0.2` para compilar o binário nativo. Stage 2 usa `alpine:3.21.3` como imagem de runtime (~8 MB), sem JDK. O container é executado com usuário não-root (`appuser`).
- **`compose.yml`**: Orquestração local de containers. Sobe MongoDB 8.0.11 e Mongo Express. O serviço `app` está comentado e pode ser descomentado para rodar o stack completo localmente.
- **`.env`**: Arquivo de variáveis de ambiente para credenciais do MongoDB e Mongo Express. Listado em `.gitignore` e `.dockerignore` para evitar vazamento de segredos.
- **`.dockerignore`**: Exclui diretórios de build, IDEs, testes e arquivos de CI/CD da imagem Docker, reduzindo seu tamanho e evitando inclusão de segredos.
- **`docs/`**: Documentação adicional do projeto.
  - **`curl-examples.sh`**: Scripts de exemplo para testar os endpoints da API via terminal.
  - **`referencias/`**: Documentos de arquitetura e fluxos detalhados.
- **`.github/workflows/build-and-test.yml`**: Pipeline de CI/CD com três jobs: `validate_wrapper` (valida o checksum do Gradle Wrapper contra o registro oficial, prevenindo ataques à cadeia de suprimento), `build_and_test` (compila, testa com JaCoCo e arquiva o JAR — pula `nativeCompile` por ser lento em CI), e `dependency-submission` (submete o grafo de dependências ao GitHub para habilitar alertas do Dependabot, executado apenas em push).

---

## Source Code (`src/main/java/com/example/catalog`)

A estrutura do código fonte é dividida em três camadas principais: `domain`, `application` e `infrastructure`. O fluxo de dependências segue estritamente a regra: `infrastructure` → `application` → `domain`. O domínio não conhece nenhuma das outras camadas.

### 1. `domain` (Núcleo)

Contém a lógica de negócios pura, entidades e regras. **Sem dependências de frameworks externos.** É a camada mais interna e estável do projeto.

- **`ValueObject.java`**: Interface marcadora para Objetos de Valor. VOs são imutáveis e sua igualdade é definida pelos atributos, não pela identidade do objeto.
- **`Identifier.java`**: Classe abstrata base para todos os identificadores de domínio. Estende `ValueObject` e implementa `equals`/`hashCode` com base no valor do identificador (`getValue()`).
- **`Entity.java`**: Classe abstrata base para todas as Entidades. Armazena o `ID` tipado e define igualdade baseada no identificador. Declara o método `validate(ValidationHandler)`, que força todas as entidades a implementar validação via Notification Pattern.
- **`AssertionConcern.java`**: Utilitário de validações defensivas (guards) de fail-fast. Fornece métodos como `assertArgumentNotNull`, `assertArgumentNotEmpty`, `assertArgumentPositive` e `assertArgumentLength`, que lançam `DomainException` imediatamente na primeira violação. O `Product` utiliza esta classe para validações de integridade técnica imediatas, em conjunto com o `ProductValidator` (Notification Pattern) para regras de negócio mais complexas.

#### `product/` — Agregado Principal

- **`Product.java`**: Entidade raiz do agregado (Aggregate Root). Encapsula todos os atributos do produto (`name`, `description`, `category`, `brand`, `price`, `active`, `createdAt`, `updatedAt`). Construtor é privado; instâncias são criadas via factory methods estáticos. Utiliza `AssertionConcern` para validação defensiva técnica (Fail-Fast) tanto no construtor quanto no método `update`.
  - `newProduct(...)`: gera um novo `ProductID` único (UUID) e define `createdAt`/`updatedAt` com `LocalDateTime.now()`.
  - `with(...)`: reconstrói uma entidade a partir de dados persistidos.
  - `update(...)`: aplica validações fail-fast, atualiza os campos e define `updatedAt` com o timestamp atual.
  - `deactivate()`: define `active = false` e atualiza `updatedAt`. Implementa soft-delete.
  - `validate(ValidationHandler)`: delega para `ProductValidator`.
- **`ProductID.java`**: Value Object que encapsula o identificador único do produto como `String` (UUID). Criado via `ProductID.unique()` ou `ProductID.from(String)`.
- **`ProductValidator.java`**: Implementa as invariantes do agregado `Product` via Notification Pattern (acumula todos os erros antes de retornar). Valida: `name` (obrigatório, máximo 255 caracteres), `description` (obrigatório, máximo 4000 caracteres), `category` (obrigatório), `brand` (obrigatório) e `price` (obrigatório, deve ser maior que zero).

#### `pagination/` — Paginação Nativa

- **`Pagination.java`**: Objeto de domínio (`record`) que representa uma página de resultados, mantendo o core livre de dependências do Spring (`Page`).
- **`SearchQuery.java`**: Objeto de domínio (`record`) que encapsula os critérios de busca (página, quantidade, ordenação), mantendo o core livre de dependências do Spring (`Pageable`).

#### `validation/` — Framework de Validação

- **`Error.java`**: `record` que encapsula uma mensagem de erro de validação (`String message`).
- **`ValidationHandler.java`**: Interface central do Notification Pattern. Define `append(Error)`, `append(ValidationHandler)`, `validate(Validation<T>)`, `getErrors()`. Inclui métodos default `hasError()` e `firstError()`. Como boa prática do Java moderno, evita o uso de nulls retornando `Optional<Error>` e `Optional<T>`.
- **`Validator.java`**: Classe abstrata base para todos os validadores. Recebe um `ValidationHandler` no construtor e expõe o método abstrato `validate()`.
- **`handler/Notification.java`**: Implementação concreta de `ValidationHandler`. Acumula múltiplos erros em uma lista interna em vez de lançar exceção imediata. O método `validate(Validation<T>)` captura `DomainException` e `Exception` (evitando capturar `Throwable` para não engolir erros catastróficos da JVM como `OutOfMemoryError`), adicionando seus erros à lista e retornando um `Optional` seguro.

#### `exception/` — Exceções do Domínio

- **`DomainException.java`**: Exceção base para violações de regras de negócio. Estende `RuntimeException` e carrega uma lista de `Error`. A stack trace é desabilitada (`writableStackTrace = false`) por motivos de performance.
- **`NotFoundException.java`**: Sinaliza que um recurso não foi encontrado (mapeado para HTTP 404 pelo `GlobalExceptionHandler`). Criada via `NotFoundException.with(Class<?>, String id)`.
- **`NotificationException.java`**: Lançada quando a validação via `Notification` acumula erros. Criada via `NotificationException.with(String message, Notification notification)`.

---

### 2. `application` (Aplicação)

Camada de orquestração dos casos de uso. Coordena operações entre o domínio e as portas de saída. **Não possui anotações de Spring** — os beans são criados em `UseCaseConfig` na camada de infraestrutura.

- **`UseCase<IN, OUT>`** / **`Command<IN, OUT>`** / **`Query<IN, OUT>`**: Classes abstratas bases baseadas no padrão de segregação CQS.
- **`NullaryUseCase<OUT>`**: Variante de `UseCase` para casos de uso sem input.
- **`Presenter<UC_OUT, NEW_OUT>`**: Interface funcional (`Function`) para transformação da saída dos casos de uso.

#### `port/input/` — Portas de Entrada (Driving Ports)

Contratos que os adaptadores primários (REST) invocam. Cada porta é uma classe abstrata (Command ou Query) que estende as classes base e define **Records** para `Input` e `Output`, garantindo tipagem forte e imutabilidade:

- **`CreateProductUseCase`**: `Input` contém os dados para criação; `Output` retorna apenas o `id` do produto criado.
- **`CreateProductBatchUseCase`**: Opera com listas de Input/Output do `CreateProductUseCase` para inserção em lote.
- **`GetProductByIdUseCase`**: `Input` encapsula o `id`; `Output` contém o DTO de leitura do produto.
- **`UpdateProductUseCase`**: `Input` contém `id` e todos os campos atualizáveis; `Output` retorna o `id` modificado.
- **`UpdateProductBatchUseCase`**: Opera com listas de Input/Output do `UpdateProductUseCase` para atualização em lote.
- **`DeactivateProductUseCase`**: `Input` com `id`; `Output` vazio (void).
- **`ListActiveProductsUseCase`**: Recebe um `SearchQuery` nativo do domínio e retorna uma `Pagination<Output>` nativa (livre de Spring Data).
- **`SearchProductsUseCase`**: `Input` encapsula a `query` de busca; `Output` é uma lista de produtos correspondentes.

#### `port/output/` — Portas de Saída (Driven Ports)

A arquitetura aplica segregação de interfaces (ISP) baseada em CQRS-Lite, dividindo as portas de saída em duas:
- **`ProductCommandGateway.java`**: Lado de escrita. Métodos que alteram estado: `create`, `createAll`, `update`, `updateAll`, e métodos para resgatar entidades ricas do banco de dados `findById(ProductID)`, `findAllById(List<ProductID>)`.
- **`ProductQueryGateway.java`**: Lado de leitura. Métodos focados em projeção e buscas sem efeito colateral: `findSummaryById`, `findAllActiveSummary(SearchQuery)`, `searchProductsSummary(String)`. Utiliza a classe `ProductSummary` para contornar a hidratação desnecessária da entidade de domínio rica (Domain Bypass).

#### `usecase/` — Implementações dos Casos de Uso

Todos os casos de uso são POJOs puros que recebem o(s) Gateway(s) via construtor.

- **`DefaultCreateProductUseCase`**: Cria `Product`, valida via `Notification` e persiste.
- **`DefaultCreateProductBatchUseCase`**: Itera sobre a lista de inputs, cria e valida cada produto individualmente (acumulando erros por item se necessário), e utiliza `gateway.createAll` para persistência eficiente.
- **`DefaultUpdateProductUseCase`**: Busca o produto, atualiza, valida e persiste.
- **`DefaultUpdateProductBatchUseCase`**: Otimizado para performance. Realiza "Bulk Read" (`gateway.findAllById`) para buscar todos os produtos de uma vez, processa as atualizações em memória utilizando um Mapa para acesso O(1), e persiste em lote via `gateway.updateAll`. Valida a existência de todos os IDs antes de iniciar.
- **`DefaultGetProductByIdUseCase`**, **`DefaultDeactivateProductUseCase`**, **`DefaultListActiveProductsUseCase`**, **`DefaultSearchProductsUseCase`**: Implementações padrão delegando para o Gateway.

---

### 3. `infrastructure` (Infraestrutura)

Implementações concretas das interfaces definidas no domínio e aplicação. Contém todas as dependências de frameworks (Spring, MongoDB Driver, SpringDoc).

#### `config/` — Configurações Spring

- **`UseCaseConfig.java`**: "Glue Code" da arquitetura hexagonal. Instancia todos os casos de uso (single e batch) como `@Bean`, injetando os respectivos Gateways.
- **`MongoConfig.java`**: Habilita MongoDB Auditing.

#### `adapter/input/` — Adaptadores Primários (Driving Adapters)

##### `rest/`

O projeto segue a segregação de responsabilidades de leitura e escrita (CQRS-Lite):
- **`ProductCommandApi.java`** / **`ProductQueryApi.java`**: Interfaces que definem os contratos da API REST e documentação Swagger/OpenAPI (OpenAPI 3), divididas entre mutações (Commands) e leituras (Queries).
- **`controller/ProductCommandController.java`**: `@RestController` focado nas operações `POST`, `PUT` e `DELETE`.
- **`controller/ProductQueryController.java`**: `@RestController` focado em operações `GET` e `POST /search`. Traduz os parâmetros HTTP de paginação para o objeto de domínio `SearchQuery`.

##### `rest/dto/`

- **`request/ProductRequest.java`**: DTO de entrada para criação. Records imutáveis com validação (`@NotBlank`, `@Positive`).
- **`request/UpdateProductRequest.java`**: DTO específico para atualização em lote, contendo obrigatoriamente o campo `id`.
- **`request/SearchRequest.java`**: DTO para busca textual.
- **`response/CreateProductResponse.java`**, **`ProductResponse.java`**, **`ErrorResponse.java`**: DTOs de saída padronizados.

##### `rest/mapper/`

- **`ProductRestMapper.java`**: Componente crucial que converte os DTOs da API (`ProductRequest`, `UpdateProductRequest`) para os Records de Input da camada de Aplicação. Garante que a camada de domínio/aplicação não conheça os DTOs web.

##### `exception/`

- **`GlobalExceptionHandler.java`**: `@RestControllerAdvice` que intercepta `DomainException`, `NotFoundException` e erros de validação, retornando respostas HTTP padronizadas (404, 422, 500).

#### `adapter/output/` — Adaptadores Secundários (Driven Adapters)

##### `gateway/`

- **`ProductMongoCommandAdapter.java`**: Implementação de `ProductCommandGateway`. Lida com inserção e atualização usando `ProductMongoRepository`.
- **`ProductMongoQueryAdapter.java`**: Implementação de `ProductQueryGateway`. Lida com consultas usando traduções nativas (do `SearchQuery` do domínio para o `PageRequest` do Spring) e uso do `MongoTemplate` para queries complexas (regex).

##### `persistence/`

- **`ProductDocument.java`**: Entidade `@Document` do MongoDB.
- **`ProductMongoRepository.java`**: Interface `MongoRepository`.

#### `utils/`

- **`JsonUtils.java`**: Utilitário para serialização JSON.

---

## Tests (`src/test/`)

- **Estratégias Recomendadas:** JUnit 5 (Unitários), Testcontainers (Integração de Gateway e Repositório em banco de dados isolado), REST Assured (Testes End-to-End validando a controller e o protocolo HTTP).