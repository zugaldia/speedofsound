plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.detekt)
    application
}

dependencies {
    implementation(project(":core"))
    implementation(libs.clikt)
    implementation(libs.kotlinxCoroutines)
    implementation(libs.log4jSlf4j2Impl)
    runtimeOnly(libs.log4jCore)
    implementation(libs.bundles.onnxEcosystem)
}

application {
    mainClass = "com.zugaldia.speedofsound.cli.CliKt"
}
