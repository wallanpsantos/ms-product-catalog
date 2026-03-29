# IMPORTANT

## Security policy:

- Do not read or expose secrets, tokens, credentials, `.env` files or private keys.
- Always ask before running Git commands that can rewrite history, discard changes, or affect remote branches, including
  `git reset --hard`, `git clean -fd`, `git push --force`, `rebase`, `cherry-pick`, and branch deletion.
- Never execute remote scripts or pipe internet content into a shell without explicit approval, including patterns like
  `curl ... | bash`, `wget ... | sh`, or bootstrap installers.
- If a command may affect files outside the repository, user-level configuration, SSH keys, shell profiles, or global
  package managers, stop and ask first.
- When sending messages on my behalf, always draft it first and get my approval. Always ask before deleting files.
  Always ask before making network requests.
- If a task fails three times, stop. Don't let any task run indefinitely. Limit runtime to 20 minutes unless I say
  otherwise.

# Development Principles

## Always apply:

- **Architecture (Strict Clean/Hexagonal Architecture)**:
    - **Dependency Rule**: Source code dependencies MUST strictly point inwards:
      `infrastructure -> application -> domain`.
    - **domain layer**: Pure Java/business logic only.
        - Contains business entities extending base `Entity` / `ValueObject` / `Identifier`.
        - Contains `Validator` and `Notification` patterns for domain rule validation (`AssertionConcern`).
        - ZERO framework dependencies (no Spring, no `@Entity`, no `@Document`).
    - **application layer**: Use cases and Ports.
        - `port/input`: Interfaces for UseCases (e.g., `CreateProductUseCase`).
        - `port/output`: Gateway interfaces separated by Command and Query (e.g., `CommandGateway`, `QueryGateway`).
        - `usecase`: Default implementations of the input ports (e.g., `DefaultCreateProductUseCase`). No `@Service`
          annotations here.
    - **infrastructure layer**: The outermost layer (Spring Boot territory).
        - `adapter/input`: Driving adapters (e.g., `rest/controller`, `rest/dto`, `GlobalExceptionHandler`).
            - **Controller Segregation (CQRS-Lite)**: Strictly separate read (Queries) and write (Commands)
              operations into distinct controllers to adhere to SRP, ISP, and avoid Sonar/Checkstyle warnings for
              too many constructor parameters (max 7). Example: use `ProductCommandController` (for POST/PUT/DELETE,
              injecting only mutation use cases) and `ProductQueryController` (for GET/Search, injecting only read
              use cases). Both can share the same base HTTP path.
        - `adapter/output`: Driven adapters (e.g., `gateway/JpaCommandAdapter`, `persistence/JpaRepository`,
          `persistence/JpaEntity`).
        - `config`: Manual bean wiring for UseCases (e.g., `UseCaseConfig.java`) to keep application layer pure.

- **SOLID in Practice**:
    - **S (Single Responsibility)**: each class/method has one reason to change; split when a class handles more than
      one concern (e.g., validation + persistence + HTTP mapping in the same place).
    - **O (Open/Closed)**: extend behavior via new classes, composition, or strategies, never by modifying existing
      ones; use interfaces + implementations, not if/switch chains on type.
    - **L (Liskov Substitution)**: subtypes must be fully substitutable for their base type; never override a method to
      throw `UnsupportedOperationException` or to do nothing.
    - **I (Interface Segregation)**: prefer small, focused interfaces over large general-purpose ones; a client should
      never be forced to depend on methods it does not use.
    - **D (Dependency Inversion)**: depend on abstractions (interfaces), never on concrete implementations; inject
      dependencies via constructor, never instantiate them internally with `new` outside of factories or config classes.

- **Design Patterns & Stack**:
    - Java 21+: Model domain types with records (immutable) + sealed interfaces/classes; use switch pattern matching to
      exhaustively handle type hierarchies; never use instanceof chains; apply Decorators instead of inheritance for
      cross-cutting concerns
    - Core: Prefer Factory, Builder, Strategy, and Observer
    - Resilience: Retry + Circuit Breaker are mandatory around any external boundary (HTTP calls,
      queues, databases, external services)
    - Constraint: Never use manual Singletons, open class hierarchies for type dispatch, or static global state; prefer
      constructor Dependency Injection and explicit composition in all languages

- **Idempotency**:
    - HTTP: All unsafe HTTP operations that can be retried (e.g., POST with client-generated IDs, PUT, DELETE) must
      tolerate duplicate requests by design, using idempotency keys, natural keys, or upserts.
    - Messaging: Event consumers (e.g., Kafka) must be idempotent, using processed-message stores, unique constraints,
      or Inbox/Outbox patterns to prevent double side effects.

- **ACID & Distributed Consistency**:
    - Local: Business invariants must be protected by ACID transactions at the Use Case/Service layer whenever
      operations touch a single database.
    - Distributed: When a workflow spans multiple services/databases, prefer Saga plus Transactional Outbox over 2PC or
      global distributed transactions, keeping each service responsible for its own local consistency.

- **Observability**:
    - Structured logs: All logs must be structured (JSON) with correlation IDs (trace/span ID), service name, and
      environment. Messages in PT-BR.
    - Tracing: Distributed tracing must be implemented for all external calls, async operations, and service
      boundaries (e.g., OpenTelemetry or Micrometer Tracing).
    - Metrics: Expose business and system metrics (latency, error rates, throughput) for every endpoint and service
      method.

- **Richardson Maturity (Level 2-3)**:
    - Level 2: Resource-oriented URIs + proper HTTP methods (GET, POST, PUT, DELETE, PATCH) + standard HTTP status
      codes (200, 201, 404, 409, etc.).
    - Level 3: Include hypermedia links (\_links, actions) in responses for discoverability and client navigation (
      HATEOAS).
    - Constraint: Never use RPC-style endpoints (e.g., /user/create) or custom HTTP verbs.

## Clean Code & Maintenance (Scout Rule):

- **DRY**: remove duplication when changing code
- **Names**: rename bad names you touch
- **Simplicity**: smaller methods, direct flow, no over-engineering
- **Maintainability**: readability, split responsibilities, tests, remove dead code
- **Methods**: single responsibility, max ~20 lines; extract logic with intention-revealing names
- **Conditionals**: avoid negation (`!isInactive` -> `isActive`); no nested ifs — use early return / guard clauses
- **Magic values**: no magic numbers or strings inline — extract to named constants
- **Nulls**: never return or pass `null` (not even in case of validation failure or if there are no errors). Modern Java
  encourages the use of `Optional` to represent the absence of a value and thus avoid the dreaded
  `NullPointerException`. Use fail-fast in constructors.

## Testing:

- Use the `@DisplayName` annotation in PT-BR to describe the expected behavior (e.g.,
  `@DisplayName("Deve buscar produto por ID com sucesso")`).
- Always structure tests with BDD comments in English: Given, When e Then
- Apply to all test types: unit, integration, external API and E2E
- AssertJ: Use assertion chaining and the powerful `.returns()` method to validate multiple properties of the same
  object within a single chain. This avoids multiple repeated `assertThat` calls, drastically reduces visual noise in
  the code, and keeps the validation clean and precise. Example:
  `assertThat(entity).returns("id", Entity::getId).returns("name", Entity::getName);`

## Codebase Rules:

- Comments, logs, exception messages: **PT-BR only**
- Code/variables/methods: **English only**
- Embed knowledge in code (types, interfaces, naming, patterns)

## Core Constraints - Zero-MD Policy:

Do not create, suggest, or maintain `.md` files, documentation sidecars, or READMEs for internal structures. All
knowledge must be embedded in the code itself through native language constructs (e.g., TypeScript types, Java
interfaces/records, Python type hints, Go structs), strict naming conventions, and optimized architectural patterns.
Exception: `.md` files used strictly as Gemini CLI configuration/context (e.g., this file and project-level GEMINI.md)
are allowed.