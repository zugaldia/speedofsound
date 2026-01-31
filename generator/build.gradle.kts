plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.openApi)
}

dependencies {
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.ktorClient)
    implementation(libs.log4jApi)
    implementation(libs.moshiKotlin)
    testImplementation(kotlin("test"))
}

openApiValidate {
    inputSpec.set("$projectDir/src/main/resources/openresponses.json")
}

openApiGenerate {
    // Unfortunately, "serializationLibrary": "kotlinx_serialization" does not work (hence the Moshi dependency)
    // https://github.com/OpenAPITools/openapi-generator/blob/master/modules/openapi-generator-gradle-plugin/README.adoc
    // https://openapi-generator.tech/docs/generators/kotlin
    generatorName.set("kotlin")
    library.set("jvm-ktor")
    inputSpec.set("$projectDir/src/main/resources/openresponses.json")
    outputDir.set("${layout.buildDirectory.get().asFile}/generated")
    packageName.set("com.zugaldia.speedofsound.generated.openresponses")
    generateApiDocumentation.set(false)
    generateModelDocumentation.set(false)
    configOptions.set(mapOf("dateLibrary" to "kotlinx-datetime"))
    typeMappings.set(mapOf("AnyOfLessThanGreaterThan" to "kotlin.Any"))
    importMappings.set(mapOf("AnyOfLessThanGreaterThan" to "kotlin.Any"))
}

// Add generated sources to the main source set
kotlin.sourceSets["main"].kotlin.srcDir("${layout.buildDirectory.get().asFile}/generated/src/main/kotlin")

// Ensure code generation runs before compilation
tasks.named("compileKotlin") {
    dependsOn("openApiGenerate")
}
