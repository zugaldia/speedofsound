plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.detekt)
    application
}

dependencies {
    implementation(project(":core"))
    implementation(libs.bundles.javaGiEcosystem)
}

application {
    mainClass = "com.zugaldia.speedofsound.app.AppKt"

    // See: https://java-gi.org/usage/#linux
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}
