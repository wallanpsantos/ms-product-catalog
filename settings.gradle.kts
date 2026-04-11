plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Ativa validações estritas do Configuration Cache (recomendado no Gradle 9+)
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "ms-product-catalog"
