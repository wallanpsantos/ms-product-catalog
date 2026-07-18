## 📝 Descrição

<!-- Descreva brevemente o propósito deste Pull Request e as alterações realizadas. -->

**Tipo de Mudança:**
- [ ] 🐛 Bugfix
- [ ] ✨ Nova Feature
- [ ] ♻️ Refatoração
- [ ] 📦 Dependência (adição, remoção ou atualização)
- [ ] 🔧 Configuração / Infraestrutura
- [ ] 📄 Documentação

---

## 🛡️ Segurança — AppSec (OWASP Top 10:2025)

> Marque **N/A** se a seção inteira não se aplica a este PR. Para itens individuais, marque apenas os que forem relevantes.

<details>
<summary><b>🔑 Criptografia & Segredos (A04:2025)</b></summary>

- [ ] Criptografia simétrica utiliza **AES/GCM** (modo `AES/ECB` é proibido)
- [ ] IVs, salts e chaves são gerados com `java.security.SecureRandom` (nunca `java.util.Random`)
- [ ] Nenhum segredo (senhas, API keys, tokens) está hardcoded no código-fonte
- [ ] Segredos são injetados via variáveis de ambiente ou Secrets Manager (Vault, AWS SM)
- [ ] N/A — este PR não envolve criptografia ou segredos

</details>

<details>
<summary><b>💉 Prevenção de Injeção (A05/A01:2025)</b></summary>

- [ ] Consultas SQL/JPQL utilizam parâmetros nomeados (`@Param`) ou Criteria API (sem concatenação de strings)
- [ ] Queries nativas utilizam `?1` ou parâmetros nomeados (sem interpolação de variáveis)
- [ ] Parsers XML (`DocumentBuilderFactory`) têm DTDs e entidades externas desabilitados (prevenção de XXE)
- [ ] Requisições HTTP de saída baseadas em input do usuário validam protocolo (HTTPS), resolvem IPs e bloqueiam redes internas (`InetAddressFilter` ou validação manual para SSRF)
- [ ] Input do usuário renderizado em HTML é sanitizado com **OWASP Java HTML Sanitizer** ou OWASP Encoder (prevenção de XSS)
- [ ] N/A — este PR não envolve queries, XML, requisições externas ou renderização HTML

</details>

<details>
<summary><b>📁 Manipulação de Arquivos (A05/A08:2025)</b></summary>

- [ ] Uploads de arquivos utilizam nomes gerados com UUID (nunca `getOriginalFilename()` diretamente)
- [ ] Caminhos de destino são validados com `normalize()` + `startsWith()` (prevenção de Path Traversal)
- [ ] Extração de `.zip`/`.tar` valida canonicamente se entradas permanecem dentro do diretório alvo (prevenção de Zip Slip)
- [ ] N/A — este PR não envolve upload ou extração de arquivos

</details>

<details>
<summary><b>🚦 Controle de Acesso & Resiliência (A01/A07:2025)</b></summary>

- [ ] Recursos acessados por ID têm validação de ownership no **Service Layer** (proteção contra BOLA/IDOR)
- [ ] Endpoints sensíveis (login, reset de senha) possuem **Rate Limiting** implementado
- [ ] Rate Limiting extrai o IP real via `X-Forwarded-For` (com proxy confiável) ou ID do JWT — não depende apenas de `getRemoteAddr()`
- [ ] Tokens JWT validam explicitamente `iss`, `aud`, `exp` e algoritmo permitido (nunca aceitar `alg: none`)
- [ ] N/A — este PR não envolve controle de acesso, autenticação ou rate limiting

</details>

<details>
<summary><b>🔒 Deserialização & Integridade de Dados (A08:2025)</b></summary>

- [ ] Nenhum uso de `ObjectInputStream` com dados não confiáveis
- [ ] Jackson configurado com `deactivateDefaultTyping()` ou `PolymorphicTypeValidator` com allowlist explícita
- [ ] N/A — este PR não envolve deserialização polimórfica ou dados não confiáveis

</details>

---

## ⚡ Performance — Code Smells (Java 21+)

> Marque **N/A** se a seção inteira não se aplica a este PR.

<details>
<summary><b>🔴 Alta Severidade (corrigir sempre)</b></summary>

- [ ] Nenhum `Pattern.compile()` ou `String.matches()` dentro de loops — regex pré-compilado como `static final`
- [ ] Queries sem paginação (`findAll()`) não retornam coleções potencialmente ilimitadas
- [ ] Concatenação de strings em loops utiliza `StringBuilder` ou `Collectors.joining()`
- [ ] `parallelStream()` não é usado com estado mutável compartilhado
- [ ] N/A — este PR não contém código performance-critical

</details>

<details>
<summary><b>🟡 Média Severidade (medir antes de otimizar)</b></summary>

- [ ] Hot paths evitam autoboxing desnecessário (`Long sum = 0L` → `long sum = 0L`)
- [ ] Lookups repetidos utilizam `Set` ou `Map` em vez de `List.contains()` (O(1) vs O(n))
- [ ] I/O blocking em alta concorrência considera Virtual Threads (`Executors.newVirtualThreadPerTaskExecutor()`)
- [ ] Coleções com tamanho previsível são inicializadas com capacidade (`HashMap.newHashMap(expectedSize)`)
- [ ] N/A

</details>

---

## 📦 Dependências & Supply Chain (A03:2025)

> Preencha esta seção se o PR adiciona, remove ou atualiza dependências no `pom.xml` / `build.gradle`.

<details>
<summary><b>Checklist de Dependências</b></summary>

- [ ] Versões estão fixas e exatas (sem ranges como `[1.0,)`, sem `LATEST` ou `RELEASE`)
- [ ] Nenhuma dependência `SNAPSHOT` em código destinado a release
- [ ] Nova dependência foi auditada: mantenedor confiável, repositório ativo, histórico de CVEs verificado
- [ ] `mvn dependency-check:check` executado sem vulnerabilidades com CVSS ≥ 7
- [ ] Conflitos de versão transitiva resolvidos via `<dependencyManagement>` (não silenciados pelo Maven)
- [ ] Versões de plugins do Maven estão fixas em `<pluginManagement>`
- [ ] SBOM será gerado no pipeline de release (`cyclonedx-maven-plugin`)
- [ ] N/A — este PR não altera dependências

</details>

---

## 🏗️ Design & Arquitetura (SOLID)

> Marque os itens relevantes para as classes criadas ou modificadas neste PR.

<details>
<summary><b>Checklist SOLID</b></summary>

- [ ] **SRP:** Cada classe/serviço tem uma única responsabilidade (descritível em uma frase sem usar "e")
- [ ] **OCP:** Nova funcionalidade foi adicionada por extensão (nova classe/record), não por modificação de classes existentes com `if/switch` por String
- [ ] **LSP:** Nenhuma implementação de interface retorna valor inesperado, lança `UnsupportedOperationException` ou ignora o contrato do tipo pai
- [ ] **ISP:** Interfaces não forçam implementadores a depender de métodos que não utilizam (interfaces com 10+ métodos são um sinal de alerta)
- [ ] **DIP:** Código de negócio depende de abstrações (interfaces), não de implementações concretas; dependências injetadas via construtor
- [ ] N/A — este PR não introduz ou modifica classes de domínio/serviço

</details>

---

## 📊 Logging, Rastreabilidade & Observabilidade (A09:2025)

> Marque **N/A** se a seção inteira não se aplica a este PR.

<details>
<summary><b>📝 Logs Estruturados & Segurança</b></summary>

- [ ] Eventos de segurança são logados explicitamente: login (sucesso/falha), acesso negado, escalonamento de privilégios
- [ ] Dados sensíveis **nunca** aparecem em logs: senhas, tokens, cartões de crédito, CPF em texto claro
- [ ] PII é mascarado via `MaskingConverter` (Logback) ou Jackson serializer customizado antes de logar objetos completos
- [ ] Logging utiliza **parâmetros SLF4J** (`log.info("User {}", id)`) — nunca `String.format()` ou concatenação
- [ ] Nível de log adequado: `ERROR` (falhas não tratadas), `WARN` (degradação recuperada), `INFO` (eventos de domínio), `DEBUG` (diagnóstico, desabilitado em produção)
- [ ] N/A — este PR não introduz ou modifica lógica de logging

</details>

<details>
<summary><b>🔭 Tracing & Métricas (OpenTelemetry)</b></summary>

- [ ] HTTP clients (`RestClient`, `WebClient`) são injetados via `Builder` autoconfigurado — nunca instanciados com `new` (preserva propagação W3C `traceparent`)
- [ ] Código assíncrono (`@Async`, Virtual Threads) registra `ContextPropagatingTaskDecorator` para manter o contexto de trace
- [ ] Labels de métricas utilizam valores de **baixa cardinalidade** (método HTTP, status, rota templada) — IDs dinâmicos, e-mails e tokens pertencem a atributos de span ou logs estruturados
- [ ] MDC é populado na borda de entrada (filtro HTTP / interceptor Kafka) e limpo em `finally`
- [ ] N/A — este PR não envolve métricas, traces ou contexto de observabilidade

</details>

<details>
<summary><b>🚨 Alerting (não apenas logging)</b></summary>

- [ ] Novos cenários de falha (ex: integração externa, timeout, circuit breaker) possuem alertas configurados ou planejados (SIEM, Prometheus Alertmanager, PagerDuty)
- [ ] Thresholds de alerta estão definidos: falhas de login > 5/min por IP, 5xx acima do baseline, CVEs com CVSS ≥ 9
- [ ] N/A — este PR não introduz novos caminhos de erro monitoráveis

</details>

---

## 🧹 Clean Code

> Marque os itens relevantes para o código criado ou modificado neste PR.

<details>
<summary><b>Princípios Fundamentais</b></summary>

- [ ] **DRY:** Nenhuma lógica duplicada (copy-paste) — blocos similares foram extraídos para métodos ou classes reutilizáveis
- [ ] **KISS:** A solução é a mais simples possível; sem over-engineering ou abstrações prematuras
- [ ] **YAGNI:** Nenhuma funcionalidade especulativa adicionada ("e se precisarmos no futuro?")
- [ ] N/A

</details>

<details>
<summary><b>Nomes & Legibilidade</b></summary>

- [ ] Variáveis, métodos e classes possuem nomes descritivos e pronunciáveis que revelam a intenção
- [ ] Booleanos seguem convenção `is`/`has`/`can`/`should` (ex: `isActive`, nunca `flag` ou `status`)
- [ ] Nenhum magic number ou string literal inline — valores extraídos para constantes nomeadas (`static final`)
- [ ] Comentários explicam o **porquê**, não o **quê** — código auto-documentado é preferido
- [ ] N/A

</details>

<details>
<summary><b>Estrutura & Fluxo de Código</b></summary>

- [ ] Métodos são curtos (~20 linhas máx) com um único nível de abstração consistente
- [ ] Condicionais aninhados foram substituídos por guard clauses / early returns
- [ ] Sem dupla negação em booleanos (preferir `isActive` sobre `!isInactive`)
- [ ] Parâmetros de método ≤ 3 — se mais, utilizar Parameter Object ou Builder
- [ ] Construtores são fail-fast: validam argumentos com `Objects.requireNonNull()` e lançam exceção para estados inválidos
- [ ] `Optional` é utilizado para representar ausência de valor (nunca retornar ou passar `null`)
- [ ] N/A

</details>

<details>
<summary><b>Java Moderno (16+)</b></summary>

- [ ] DTOs imutáveis utilizam `record` em vez de POJOs com boilerplate ou Lombok
- [ ] `instanceof` com casting utiliza pattern matching (`if (obj instanceof Order order)`)
- [ ] Strings multilinhas (SQL, JSON, YAML) utilizam Text Blocks (`"""`)
- [ ] Imports explícitos no topo do arquivo — nenhum FQN inline poluindo o corpo do código
- [ ] N/A

</details>

---

## ✅ Validação Final

- [ ] Testes unitários adicionados ou atualizados para as mudanças
- [ ] Pipeline de CI passou (SAST, SCA, testes)
- [ ] Documentação atualizada (se aplicável)
- [ ] **Scout Rule:** o código modificado está mais limpo do que antes do PR

---

> [!TIP]
> **Referências Rápidas para o Revisor:**
> - [Playbook de Segurança (OWASP Top 10:2025)](../../SKILL_1.md)
> - Rodar auditoria de dependências: `mvn versions:display-dependency-updates && mvn dependency-check:check`
> - Buscar smells de performance: `grep -rn "\.matches(\|Pattern\.compile" --include="*.java"`
> - Buscar logs inseguros: `grep -rn "password\|secret\|token\|creditCard" --include="*.java" | grep -i "log\.\|logger\."` 
> - Buscar magic numbers: `grep -rn "== [0-9]\|> [0-9]\|< [0-9]" --include="*.java" | grep -v "static final\|test"`
