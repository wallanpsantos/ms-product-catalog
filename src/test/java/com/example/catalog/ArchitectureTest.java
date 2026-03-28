package com.example.catalog;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.conditions.ArchConditions;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.equivalentTo;
import static com.tngtech.archunit.core.domain.JavaModifier.ABSTRACT;
import static com.tngtech.archunit.lang.conditions.ArchConditions.haveModifier;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Guia de Governança e Testes de Arquitetura com ArchUnit.
 * <p>
 * Esta classe é o "guardião" do design do sistema. Ela utiliza a biblioteca ArchUnit
 * para analisar o bytecode das classes e verificar se as regras de design da
 * <strong>Arquitetura Hexagonal</strong> e do <strong>CQRS-Lite</strong> estão sendo seguidas.
 * </p>
 * <p>
 * <strong>Conceitos Fundamentais Aplicados:</strong>
 * <ul>
 *   <li><strong>Inwards Dependency:</strong> As dependências devem apontar apenas para dentro (Infra -> App -> Domain).</li>
 *   <li><strong>Domain Purity:</strong> O coração do software (Domain) deve ser agnóstico a tecnologias externas.</li>
 *   <li><strong>Interface Segregation:</strong> Uso de Ports (Input/Output) para desacoplar a lógica da infraestrutura.</li>
 *   <li><strong>Screaming Architecture:</strong> A estrutura de pacotes e nomes deve "gritar" a intenção do sistema.</li>
 * </ul>
 * </p>
 */
@AnalyzeClasses(packages = "com.example.catalog", importOptions = {
        ImportOption.DoNotIncludeTests.class,
        ArchitectureTest.IgnoreGeneratedClasses.class
})
public class ArchitectureTest {

    /**
     * Filtro para ignorar classes geradas dinamicamente pelo ecossistema Spring (AOT, CGLIB, Proxies).
     * Isso evita "falsos positivos" onde classes técnicas de infraestrutura quebram regras de domínio.
     */
    public static class IgnoreGeneratedClasses implements ImportOption {
        @Override
        public boolean includes(Location location) {
            return !location.contains("__TestContext") &&
                    !location.contains("$$Spring") &&
                    !location.contains("CGLIB");
        }
    }

    // --- REGRAS DE CAMADAS (LAYERS) ---

    /**
     * Verifica a integridade macro da Arquitetura Hexagonal.
     * Regra: Domain é o núcleo; Application o envolve; Infrastructure é o mundo externo.
     * Nenhuma camada interna pode conhecer ou depender de uma camada mais externa.
     */
    @ArchTest
    static final ArchRule hexagonalArchitectureLayersShouldBeRespected = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Infrastructure").definedBy("..infrastructure..")

            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .as("As camadas da arquitetura hexagonal devem ser respeitadas");

    // --- REGRAS DE PUREZA (WHITELIST) ---

    /**
     * Garante que o Domínio seja 100% puro.
     * Abordagem Whitelist: Só permite Java padrão, o próprio pacote domain e anotações de contrato.
     * Proíbe Spring, JPA, Hibernate, JSON libs, etc., dentro do modelo de negócio rico.
     */
    @ArchTest
    static final ArchRule domainShouldBePureJava = classes()
            .that().resideInAPackage("..domain..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                    "java..",
                    "com.example.catalog.domain..",
                    "org.jspecify.."
            )
            .because("O domínio deve ser puro, permitindo apenas Java padrão, anotações de nulidade e referências internas")
            .as("O domínio deve ser puro Java e não depender de nada externo (Whitelist)");

    /**
     * Garante que a camada de Aplicação seja independente de implementações técnicas.
     * Ela pode orquestrar o Domínio, mas não deve conhecer detalhes de Banco de Dados ou APIs externas.
     */
    @ArchTest
    static final ArchRule applicationShouldNotDependOnInfrastructure = classes()
            .that().resideInAPackage("..application..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                    "java..",
                    "com.example.catalog.domain..",
                    "com.example.catalog.application..",
                    "org.jspecify..",
                    "org.slf4j.."
            )
            .because("A camada de aplicação deve ser independente de frameworks e implementações de infraestrutura")
            .as("A camada de aplicação não deve depender da infraestrutura (Whitelist)");

    /**
     * Protege as Portas de Saída (Output Ports/Gateways).
     * Garante que os contratos de persistência usem apenas tipos de Domínio ou Java,
     * impedindo que tipos do JPA/Hibernate (Infrastructure) vazem para a assinatura dos métodos.
     */
    @ArchTest
    static final ArchRule outputPortsShouldNotLeakInfrastructureTypes = classes()
            .that().resideInAPackage("..application.port.output..")
            .should().onlyDependOnClassesThat().resideInAnyPackage(
                    "java..",
                    "com.example.catalog.domain..",
                    "com.example.catalog.application..",
                    "org.jspecify.."
            )
            .because("As portas de saída definem contratos de domínio e não devem conhecer tipos de persistência ou mensageria")
            .as("As portas de saída (Output Ports) não devem vazar tipos de infraestrutura");

    // --- REGRAS DE USE CASES E CQRS ---

    /**
     * Valida o local de residência de Use Cases.
     * Devem estar em 'port.input' (contrato abstrato) ou 'usecase' (implementação concreta).
     */
    @ArchTest
    static final ArchRule useCasesShouldBeInCorrectPackages = classes()
            .that().haveSimpleNameEndingWith("UseCase")
            .and(DescribedPredicate.not(equivalentTo(com.example.catalog.application.UseCase.class)))
            .and(DescribedPredicate.not(equivalentTo(com.example.catalog.application.NullaryUseCase.class)))
            .should().resideInAnyPackage(
                    "..application.port.input..",
                    "..application.usecase.."
            )
            .as("Use cases devem estar nos pacotes corretos (port.input ou usecase)");

    /**
     * Garante que as Portas de Entrada (Input Ports) sigam o padrão de Command/UseCase.
     * Devem ser abstratas para forçar a herança da base UseCase e o uso de records de Input/Output.
     */
    @ArchTest
    static final ArchRule inputPortsShouldBeAbstractClasses = classes()
            .that().resideInAPackage("..application.port.input..")
            .and().haveSimpleNameEndingWith("UseCase")
            .should(haveModifier(ABSTRACT))
            .andShould().beAssignableTo(com.example.catalog.application.UseCase.class)
            .as("As portas de entrada (Input Ports) devem ser classes abstratas");

    /**
     * Garante que as implementações de Use Case sejam nomeadas com o prefixo 'Default'.
     * Isso facilita a identificação da implementação principal em relação aos seus contratos.
     */
    @ArchTest
    static final ArchRule defaultImplementationsShouldBeInUsecasePackage = classes()
            .that().haveSimpleNameStartingWith("Default")
            .and().haveSimpleNameEndingWith("UseCase")
            .should().resideInAPackage("..application.usecase..")
            .andShould(ArchConditions.not(haveModifier(ABSTRACT)))
            .as("As implementações padrão de use cases devem estar no pacote usecase");

    /**
     * Garante consistência de nomenclatura nos pacotes de Use Case.
     * Toda classe de negócio nesses pacotes deve explicitar que é um 'UseCase'.
     */
    @ArchTest
    static final ArchRule classesInUsecasePackageShouldHaveUseCaseSuffix = classes()
            .that().resideInAnyPackage("..application.usecase..", "..application.port.input..")
            .and().areNotNestedClasses()
            .should().haveSimpleNameEndingWith("UseCase")
            .as("Todas as classes de nível superior nos pacotes de Use Case devem terminar com o sufixo 'UseCase'");

    // --- REGRAS DE ADAPTADORES E PERSISTÊNCIA ---

    /**
     * Garante que os Controllers REST estejam isolados no seu adaptador de entrada.
     */
    @ArchTest
    static final ArchRule controllersShouldBeInInfrastructureAdapterInputRest = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().resideInAPackage("..infrastructure.adapter.input.rest.controller..")
            .as("Controllers devem residir no adaptador de entrada REST da infraestrutura");

    /**
     * Valida que Gateways (Portas de Saída) sejam interfaces puras.
     */
    @ArchTest
    static final ArchRule gatewaysShouldBeInterfacesAndInCorrectPackage = classes()
            .that().haveSimpleNameEndingWith("Gateway")
            .should().beInterfaces()
            .andShould().resideInAPackage("..application.port.output..")
            .as("As portas de saída (Gateways) devem ser interfaces e residir no pacote port.output");

    /**
     * Valida que os adaptadores de persistência implementem os Gateways e sigam o sufixo 'Adapter'.
     */
    @ArchTest
    static final ArchRule adaptersShouldHaveAdapterSuffixAndBeInCorrectPackage = classes()
            .that().resideInAPackage("..infrastructure.adapter.output.gateway..")
            .should().haveSimpleNameEndingWith("Adapter")
            .as("Os adaptadores de infraestrutura devem residir no pacote correto e terminar com 'Adapter'");

    /**
     * Garante que as entidades JPA não "vazem" e fiquem restritas ao seu pacote de persistência.
     */
    @ArchTest
    static final ArchRule entitiesShouldHaveEntitySuffixAndBeAnnotated = classes()
            .that().resideInAPackage("..infrastructure.adapter.output.persistence..")
            .and().haveSimpleNameEndingWith("Entity")
            .should().beAnnotatedWith("jakarta.persistence.Entity")
            .as("Entidades de persistência devem terminar com 'Entity' e ter a anotação @Entity");

    /**
     * Valida a localização e contrato dos Repositórios do Spring Data.
     */
    @ArchTest
    static final ArchRule repositoriesShouldBeInterfacesAndInCorrectPackage = classes()
            .that().haveSimpleNameEndingWith("Repository")
            .should().beInterfaces()
            .andShould().resideInAPackage("..infrastructure.adapter.output.persistence..")
            .as("Repositórios Spring Data devem ser interfaces e residir no pacote de persistência");

    // --- REGRAS DE COMPONENTES TRANSVERSAIS (CROSS-CUTTING) ---

    /**
     * Garante que exceções de domínio sigam o padrão de nomenclatura e residam no pacote correto.
     */
    @ArchTest
    static final ArchRule domainExceptionsShouldHaveExceptionSuffixAndBeInCorrectPackage = classes()
            .that().resideInAPackage("..domain.exception..")
            .should().haveSimpleNameEndingWith("Exception")
            .andShould().beAssignableTo(RuntimeException.class)
            .as("Exceções de domínio devem terminar com 'Exception' e residir no pacote domain.exception");

    /**
     * Padroniza os DTOs de Request para a API REST.
     */
    @ArchTest
    static final ArchRule restRequestDtosShouldHaveRequestSuffixAndBeInCorrectPackage = classes()
            .that().resideInAPackage("..infrastructure.adapter.input.rest.dto.request..")
            .and().areNotNestedClasses()
            .should().haveSimpleNameEndingWith("Request")
            .as("DTOs de entrada REST devem terminar com 'Request' e residir no pacote correto");

    /**
     * Padroniza os DTOs de Response para a API REST.
     */
    @ArchTest
    static final ArchRule restResponseDtosShouldHaveResponseSuffixAndBeInCorrectPackage = classes()
            .that().resideInAPackage("..infrastructure.adapter.input.rest.dto.response..")
            .and().areNotNestedClasses()
            .should().haveSimpleNameEndingWith("Response")
            .as("DTOs de saída REST devem terminar com 'Response' e residir no pacote correto");

    /**
     * Garante que as classes de configuração do Spring fiquem organizadas e nomeadas corretamente.
     */
    @ArchTest
    static final ArchRule configClassesShouldHaveConfigSuffixAndBeInCorrectPackage = classes()
            .that().resideInAPackage("..infrastructure.config..")
            .and().areNotNestedClasses()
            .should().haveSimpleNameEndingWith("Config")
            .as("Classes de configuração devem terminar com 'Config' e residir no pacote correto");

    /**
     * Padroniza classes utilitárias.
     */
    @ArchTest
    static final ArchRule utilsClassesShouldHaveUtilsSuffixAndBeInCorrectPackage = classes()
            .that().resideInAPackage("..infrastructure.utils..")
            .should().haveSimpleNameEndingWith("Utils")
            .as("Classes utilitárias devem terminar com 'Utils' e residir no pacote correto");

    /**
     * Padroniza os Mappers (MapStruct ou manuais) da camada REST.
     */
    @ArchTest
    static final ArchRule mappersShouldHaveMapperSuffixAndBeInCorrectPackage = classes()
            .that().resideInAPackage("..infrastructure.adapter.input.rest.mapper..")
            .should().haveSimpleNameEndingWith("Mapper")
            .as("Classes de mapeamento devem terminar com 'Mapper' e residir no pacote correto");
}
