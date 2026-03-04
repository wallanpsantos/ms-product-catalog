# Domain-Driven Design (DDD) no Java 21

Com base na estrutura de pastas de um projeto seguindo a Arquitetura Hexagonal, o **Domain Layer** (Camada de Domínio) é o coração da aplicação. Esta camada deve ser a mais pura possível, sem dependências de frameworks externos (como Spring ou Hibernate), focando apenas nas regras de negócio.

Abaixo está a explicação detalhada de cada componente, com exemplos em **Java 21**.

---

## 1. Value Objects (Objetos de Valor)

* **O que significa:** São objetos que importam apenas pelos **valores dos seus atributos**, e não por uma identidade única. Se dois Value Objects têm os mesmos dados, eles são considerados iguais. Eles **devem ser imutáveis**.
* **Quando usar:** Quando você precisa agrupar atributos que fazem sentido juntos ou medir/quantificar algo (ex: Dinheiro, Endereço, CPF, CEP, E-mail).
* **Onde fica:** `domain/model/` (ou pacote equivalente de domínio)
* **Exemplo em Java 21:** Aqui os `records` brilham! O Java 21 oferece a estrutura perfeita para Value Objects, pois records são imutáveis e já implementam `equals` e `hashCode` baseados nos valores.

```java
package com.suaempresa.ecommerce.domain.model;

import java.math.BigDecimal;

// O record garante imutabilidade. Se precisar alterar o valor, você cria um novo Money.
public record Money(BigDecimal amount, String currency) {
    
    // Compact constructor do Java para validações de negócio
    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor não pode ser negativo");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("A moeda é obrigatória");
        }
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency())) {
            throw new IllegalArgumentException("Moedas diferentes");
        }
        return new Money(this.amount.add(other.amount()), this.currency);
    }
}
```

## 2. Entities (Entidades)

* **O que significa:** São objetos que possuem uma **Identidade única (ID)** que os acompanha durante todo o seu ciclo de vida. Mesmo que todos os outros atributos mudem (ex: um cliente muda de nome e endereço), ele continua sendo a mesma Entidade por causa do seu ID.
* **Quando usar:** Quando a identidade do objeto importa para o negócio e ele possui um ciclo de vida com estados que mudam no tempo (ex: Cliente, Produto, Item do Pedido).
* **Onde fica:** `domain/model/`
* **Exemplo em Java 21:** `OrderItem` (Item do Pedido).

```java
package com.suaempresa.ecommerce.domain.model;

import java.util.UUID;
import java.math.BigDecimal;

public class OrderItem {
    private final UUID id; // Identidade imutável
    private String productId;
    private int quantity;
    private Money price; // Usando o Value Object!

    public OrderItem(String productId, int quantity, Money price) {
        this.id = UUID.randomUUID();
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    // Regra de negócio dentro da entidade
    public void increaseQuantity(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        this.quantity += amount;
    }

    public Money calculateTotal() {
        return new Money(price.amount().multiply(new BigDecimal(quantity)), price.currency());
    }

    // Getters omitidos para brevidade. 
    // Em DDD, evitamos Setters abertos (anêmicos). Mudamos estado através de métodos com intenção de negócio (como increaseQuantity).
}
```

## 3. Aggregates / Aggregate Roots (Agregados / Raízes de Agregação)

* **O que significa:** Um Agregado é um agrupamento de Entidades e Value Objects tratados como uma única unidade para alteração de dados. A **Raiz de Agregação** é a Entidade principal que "abraça" as outras. O mundo externo (como a camada de Aplicação) **só pode interagir com os objetos de dentro através da Raiz**. Isso garante que todas as regras de negócio e invariantes sejam respeitadas.
* **Quando usar:** Quando existem objetos que dependem totalmente de outro para fazer sentido. Um `OrderItem` não faz sentido sem um `Order`. O `Order` é a Raiz.
* **Onde fica:** `domain/model/`
* **Exemplo em Java 21:** `Order` (Pedido).

```java
package com.suaempresa.ecommerce.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID id;
    private OrderStatus status; // Enum
    private final List<OrderItem> items; // Protegido do mundo exterior

    public Order() {
        this.id = UUID.randomUUID();
        this.status = OrderStatus.CREATED;
        this.items = new ArrayList<>();
    }

    // A Raiz de Agregação controla como itens são adicionados.
    // Ninguém fora daqui pode fazer um items.add() diretamente.
    public void addItem(String productId, int quantity, Money price) {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Não é possível adicionar itens a um pedido já processado.");
        }
        this.items.add(new OrderItem(productId, quantity, price));
    }

    public UUID getId() { return id; }
    // ...
}
```

## 4. Domain Services (Serviços de Domínio)

* **O que significa:** São classes sem estado (stateless) que contêm regras de negócio que não se encaixam naturalmente dentro de uma única Entidade ou Agregado. Geralmente, orquestram lógica que envolve *múltiplas* entidades diferentes.
* **Quando usar:** Quando uma regra de negócio força você a passar muitas entidades para dentro de um método de outra entidade, ou quando a operação conceitualmente não pertence a nenhuma delas. (Atenção: não confunda com *Application Services*, que lidam com transações de banco de dados e APIs).
* **Onde fica:** `domain/service/`
* **Exemplo em Java 21:** Aplicar um desconto que depende do Pedido e do Perfil do Cliente (VIP, Ouro, etc.).

```java
package com.suaempresa.ecommerce.domain.service;

import com.suaempresa.ecommerce.domain.model.Order;
import com.suaempresa.ecommerce.domain.model.CustomerProfile; // Outra Raiz de Agregação

// Serviço de Domínio Puro: Sem @Service do Spring, sem dependências de infraestrutura.
public class DiscountDomainService {

    public void applyVIPDiscount(Order order, CustomerProfile customer) {
        if (customer.isVip() && order.getTotal().amount().compareTo(new BigDecimal("1000")) > 0) {
            // Lógica complexa de domínio para calcular e aplicar desconto
            order.applyDiscountPercentage(15); 
        }
    }
}
```

---

## Por que NÃO usar `record` para Entidades e Agregados?

Se `records` do Java (lançados no Java 14 e estabilizados no 16) são tão bons para diminuir o código, por que usamos classes regulares para `Order` e `OrderItem`?

Existem dois motivos arquiteturais cruciais no Domain-Driven Design:

1. **Mutabilidade e Ciclo de Vida:** Entidades e Agregados **mudam de estado** ao longo do tempo. Um Pedido (`Order`) nasce como `CREATED`, depois muda para `PAID`, depois para `SHIPPED`. Ele recebe novos itens. Um `record` em Java é projetado para ser **imutável**. Se `Order` fosse um record, para adicionar um item você teria que criar uma cópia totalmente nova do Pedido na memória. Em domínios complexos, gerenciar a recriação de estruturas gigantes de agregados a cada pequena alteração quebra o encapsulamento e a clareza do modelo.
2. **Identidade vs. Igualdade Estrutural:** A regra de ouro de uma Entidade é que ela é identificada *exclusivamente pelo seu ID*. Se dois clientes têm o mesmo ID `123`, eles são o mesmo cliente no sistema, mesmo que um tenha o nome "João" e o outro "João Silva".
    * Um `record` em Java gera um método `equals()` que compara **todos** os campos.
    * Em uma `class` regular representando uma Entidade, você sobrescreve o `equals()` e o `hashCode()` para comparar **apenas o ID**.

**Resumindo a regra de bolso para Java moderno e DDD:**
* **Value Objects** = Use `record`.
* **Entities / Aggregates** = Use `class`.
* **Domain Services** = Use `class` (sem estado).

---

## Referências Bibliográficas e Defesa Técnica

Ir para um debate técnico com a "literatura embaixo do braço" é a melhor estratégia possível. Aqui está o "arsenal" de referências bibliográficas, capítulos e artigos oficiais para justificar o uso de DDD e Java moderno.

### 1. As "Bíblias" do Domain-Driven Design
A base teórica vem de dois livros clássicos. Se alguém questionar a natureza mutável de uma Entidade ou a imutabilidade de um Value Object, cite estes autores:

* **O Livro Azul (The Blue Book):** *Domain-Driven Design: Tackling Complexity in the Heart of Software* por **Eric Evans** (O criador do DDD).
    * **Onde procurar:** **Capítulo 5 (A Model Expressed in Software).** É aqui que Evans define a diferença crucial: *Entities* têm identidade que transcende o tempo e sofrem mutações, enquanto *Value Objects* descrevem características e devem ser imutáveis.
    * **Onde procurar:** **Capítulo 6 (The Life Cycle of a Domain Object).** Aqui ele introduz os *Aggregates* como fronteiras de consistência e regras de negócio.
* **O Livro Vermelho (The Red Book):** *Implementing Domain-Driven Design* por **Vaughn Vernon**.
    * **Onde procurar:** **Capítulo 5 (Entities)** e **Capítulo 6 (Value Objects)**. Vernon é muito pragmático e detalha exatamente como modelar isso em código. No capítulo 6, ele argumenta fortemente que Value Objects devem ser imutáveis e comparados pelos seus valores (igualdade estrutural).

> **Argumento de Ouro:** "Segundo Eric Evans no Capítulo 5 do livro original de DDD, uma Entidade é definida pela sua continuidade e identidade ao longo de um ciclo de vida, sofrendo alterações de estado. Um Value Object é apenas uma descrição de propriedades e deve ser estritamente imutável."

### 2. A Decisão Arquitetural do Java: Por que `record` não serve para Entidades?
A documentação oficial da própria linguagem Java (JEPs - *JDK Enhancement Proposals*) apoia esta decisão arquitetural:

* **A Especificação Oficial:** **JEP 395: Records** (Disponível no site oficial da OpenJDK: [openjdk.org/jeps/395](https://openjdk.org/jeps/395)).
* **O que diz a JEP:** Logo no resumo, a JEP define que os records são *"transparent carriers for immutable data"* (portadores transparentes para dados imutáveis). O objetivo deles é modelar "dados puros" (data aggregates), onde a identidade do objeto não importa, apenas os dados que ele carrega.
* **A consequência:** Como os records geram automaticamente os métodos `equals()` e `hashCode()` baseados em **todos** os campos declarados, se você usar um `record` para uma Entidade (ex: `Cliente`), e o cliente mudar de endereço, o `equals()` dirá que ele é um cliente diferente. Isso fere a regra fundamental de Entidades do DDD (que devem ser comparadas apenas pelo ID).

> **Argumento de Ouro:** "A JEP 395, que introduziu os records no Java, especifica que eles são portadores transparentes de dados **imutáveis** e implementam igualdade estrutural (comparam todos os campos). Como Entidades no DDD possuem um ciclo de vida mutável e devem ser comparadas estritamente pelo seu ID, usar `record` para Entidades quebra tanto a semântica do Java quanto os princípios de consistência do DDD. No entanto, records são a implementação perfeita e nativa para Value Objects."

### 3. Artigos de Referência Rápida (Martin Fowler)
* **Artigo sobre Value Object:** ([martinfowler.com/bliki/ValueObject.html](https://martinfowler.com/bliki/ValueObject.html))
    * Fowler explica explicitamente a diferença entre *Reference Objects* (Entidades) e *Value Objects*, cravando que a chave do Value Object é ser imutável e comparado estruturalmente.
* **Artigo sobre Evans Classification:** ([martinfowler.com/bliki/EvansClassification.html](https://martinfowler.com/bliki/EvansClassification.html))
    * Um resumo excelente de como o próprio Fowler enxerga os blocos de construção definidos por Eric Evans.
