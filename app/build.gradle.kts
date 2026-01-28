plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.detekt)
    alias(libs.plugins.shadow)
    application
}

dependencies {
    implementation(project(":core"))
    implementation(libs.bundles.javaGiEcosystem)
    implementation(libs.log4jApi)
    runtimeOnly(libs.log4jCore)
}

application {
    // See: https://java-gi.org/usage/#linux
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
    mainClass = "com.zugaldia.speedofsound.app.AppKt"
}

tasks.shadowJar {
    archiveFileName.set("speedofsound.jar")
}
