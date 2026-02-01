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
    implementation(libs.log4jApi)
    runtimeOnly(libs.log4jCore)
}

application {
    mainClass = "com.zugaldia.speedofsound.cli.CliKt"
}
