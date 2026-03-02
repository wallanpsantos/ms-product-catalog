# Guia de Uso — `AssertionConcern`

## Conceito Central

`AssertionConcern` é uma ferramenta de **programação defensiva (Fail-Fast)** para garantir
invariantes técnicas do domínio. Ele não substitui o `Notification Pattern` — os dois coexistem
com responsabilidades distintas.

| Situação | Ferramenta | Resultado |
|---|---|---|
| Usuário não preencheu o nome | `ProductValidator` (Notification) | Acumula com outros erros → HTTP 422 com lista |
| Código tentou criar `Product` sem `ID` | `AssertionConcern` | Exceção imediata → bug exposto na hora |
| CPF / e-mail com formato inválido | `AssertionConcern` (no VO) | VO nunca é instanciado em estado inválido |

> **Regra de ouro:** se é um erro que o *usuário* precisa ser informado → `Notification`.
> Se é um erro que o *código* causou → `AssertionConcern`.

---

## Mudanças Imediatas no Projeto

### 1. `Entity.java` — estender `AssertionConcern`

Esta é a mudança de menor risco e maior retorno. Todas as entidades ganham os métodos
`assertArgument...` automaticamente por herança.

**Antes:**
```java
public abstract class Entity<ID extends Identifier> {

    protected Entity(final ID id) {
        Objects.requireNonNull(id, "id must not be null");
        this.id = id;
    }
}
```

**Depois:**
```java
public abstract class Entity<ID extends Identifier> extends AssertionConcern {

    protected Entity(final ID id) {
        assertArgumentNotNull(id, "'id' must not be null");
        this.id = id;
    }
}
```

**Impacto:** zero alteração nas classes filhas. `Product` já ganha a proteção via herança.

---

### 2. `Identifier.java` — estender `AssertionConcern`

O mesmo raciocínio se aplica à hierarquia de identificadores.

**Antes:**
```java
public abstract class Identifier implements ValueObject {
    public abstract String getValue();
    // ...
}
```

**Depois:**
```java
public abstract class Identifier extends AssertionConcern implements ValueObject {
    public abstract String getValue();
    // ...
}
```

---

### 3. `ProductID.java` — usar `assertArgumentNotEmpty`

Com `Identifier` já estendendo `AssertionConcern`, basta trocar o `Objects.requireNonNull`.

**Antes:**
```java
private ProductID(final String value) {
    this.value = Objects.requireNonNull(value, "id must not be null");
}
```

**Depois:**
```java
private ProductID(final String value) {
    assertArgumentNotEmpty(value, "'id' must not be empty");
    this.value = value;
}
```

> **Por quê `assertArgumentNotEmpty` e não `assertArgumentNotNull`?**
> Porque uma `String` vazia `""` é tão inválida quanto `null` para um ID.
> `assertArgumentNotEmpty` cobre os dois casos.

---

## Regras para Features Futuras

### Quando criar um Value Object com `AssertionConcern`

Crie um VO sempre que um atributo tiver **formato ou regra de estrutura própria** — e faça-o
estender `AssertionConcern` para se auto-proteger no construtor.

**Template base:**
```java
public final class NomeDoVO extends AssertionConcern implements ValueObject {

    private final String value; // ou o tipo adequado

    private NomeDoVO(final String value) {
        // Guards aqui — falha imediata se inválido
        assertArgumentNotEmpty(value, "'nomeDoVO' should not be empty");
        assertArgumentLength(value, 100, "'nomeDoVO' must be at most 100 characters");
        this.value = value.trim(); // normalização pode ocorrer aqui também
    }

    public static NomeDoVO of(final String value) {
        return new NomeDoVO(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NomeDoVO that = (NomeDoVO) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
```

---

### Exemplos de VOs candidatos no projeto atual

#### `Category`

```java
public final class Category extends AssertionConcern implements ValueObject {

    private static final int MAX_LENGTH = 50;
    private final String value;

    private Category(final String value) {
        assertArgumentNotEmpty(value, "'category' should not be empty");
        assertArgumentLength(value, MAX_LENGTH,
                "'category' must be at most %d characters".formatted(MAX_LENGTH));
        this.value = value.trim();
    }

    public static Category of(final String value) {
        return new Category(value);
    }

    public String getValue() { return value; }

    // equals, hashCode baseados em value
}
```

#### `Brand`

```java
public final class Brand extends AssertionConcern implements ValueObject {

    private static final int MAX_LENGTH = 100;
    private final String value;

    private Brand(final String value) {
        assertArgumentNotEmpty(value, "'brand' should not be empty");
        assertArgumentLength(value, MAX_LENGTH,
                "'brand' must be at most %d characters".formatted(MAX_LENGTH));
        this.value = value.trim();
    }

    public static Brand of(final String value) {
        return new Brand(value);
    }

    public String getValue() { return value; }
}
```

#### `Price` (com regra numérica)

```java
public final class Price extends AssertionConcern implements ValueObject {

    private final BigDecimal value;

    private Price(final BigDecimal value) {
        assertArgumentNotNull(value, "'price' should not be null");
        assertArgumentPositive(value, "'price' must be greater than zero");
        this.value = value;
    }

    public static Price of(final BigDecimal value) {
        return new Price(value);
    }

    public BigDecimal getValue() { return value; }
}
```

---

### Quando usar `AssertionConcern` em novos Use Cases

Use para **dependências técnicas obrigatórias** — nunca para regras de negócio do usuário.

```java
public class DefaultAlgumUseCase extends AlgumUseCase {

    private final ProductGateway productGateway;
    private final OutroServico outroServico; // nova dependência

    public DefaultAlgumUseCase(
            final ProductGateway productGateway,
            final OutroServico outroServico
    ) {
        // Objects.requireNonNull continua válido aqui — use cases não estendem AssertionConcern
        this.productGateway = Objects.requireNonNull(productGateway, "productGateway is required");
        this.outroServico   = Objects.requireNonNull(outroServico,   "outroServico is required");
    }
}
```

> **Por que não usar `AssertionConcern` nos Use Cases?**
> Use Cases são POJOs da camada de aplicação — não são entidades nem VOs do domínio.
> `Objects.requireNonNull` é suficiente e mais idiomático para injeção de dependência.

---

## Checklist para novas classes

```
Criando uma nova ENTIDADE?
  ✅ Estende Entity<ID> → já herda AssertionConcern via Entity
  ✅ Construtor protege dependências técnicas com assertArgumentNotNull
  ✅ Regras de negócio do usuário ficam no Validator (Notification Pattern)

Criando um novo VALUE OBJECT?
  ✅ Estende AssertionConcern implements ValueObject
  ✅ Construtor privado com guards assertArgument...
  ✅ Factory method estático público (of / from / newXxx)
  ✅ Imutável (campos final, sem setters)
  ✅ equals / hashCode baseados no valor

Criando um novo IDENTIFICADOR?
  ✅ Estende Identifier → já herda AssertionConcern via Identifier
  ✅ assertArgumentNotEmpty no construtor (cobre null e string vazia)
  ✅ Factory methods: unique() para novo ID e from(String) para reconstrução

Criando um novo USE CASE?
  ❌ Não estende AssertionConcern
  ✅ Usa Objects.requireNonNull para dependências injetadas
  ✅ Usa Notification para validação de input do usuário
```

---

## Resumo Visual da Hierarquia

```
AssertionConcern
├── Entity<ID>
│   └── Product                  ← protegido via herança
│
└── Identifier
    └── ProductID                ← protegido via herança
        (futuros: OrderID, CustomerId...)

AssertionConcern + ValueObject
    └── Category                 ← VO tipado (futuro)
    └── Brand                    ← VO tipado (futuro)
    └── Price                    ← VO tipado (futuro)
```