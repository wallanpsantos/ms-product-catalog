plugins {
    java
    jacoco
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.11.4"
    id("org.sonarqube") version "7.2.2.6593"
}

group = "com.example"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    systemProperty("file.encoding", "UTF-8")
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<JavaExec> {
    systemProperty("file.encoding", "UTF-8")
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2025.1.0"
extra["springDocVersion"] = "3.0.1"
extra["archunitVersion"] = "1.4.1"
extra["slf4jVersion"] = "2.0.17"
extra["mapstructVersion"] = "1.7.0.Beta1"
extra["restAssuredVersion"] = "6.0.0"
extra["testcontainersVersion"] = "1.21.4"


dependencies {
    // Production
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.cloud:spring-cloud-starter")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("springDocVersion")}")
    implementation("org.slf4j:slf4j-api:${property("slf4jVersion")}")
    implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")

    runtimeOnly("org.postgresql:postgresql")

    // Test
    testImplementation("io.rest-assured:rest-assured:${property("restAssuredVersion")}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.tngtech.archunit:archunit-junit5:${property("archunitVersion")}")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

val coverageExcludes = listOf(
    "com/example/catalog/infrastructure/config/**",
    "com/example/catalog/infrastructure/adapter/input/rest/dto/**",
    "com/example/catalog/infrastructure/adapter/input/rest/mapper/**",
    "com/example/catalog/domain/exception/**",
    "com/example/catalog/application/port/output/dto/**"
)

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(coverageExcludes)
            }
        })
    )
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(coverageExcludes)
            }
        })
    )
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

sonarqube {
    properties {
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${layout.buildDirectory.get()}/reports/jacoco/test/jacocoTestReport.xml"
        )
        property(
            "sonar.coverage.exclusions",
            coverageExcludes.joinToString(",")
        )
    }
}

graalvmNative {
    binaries {
        named("main") {
            // buildArgs.add("--verbose")
        }
    }
}