plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.detekt)
    alias(libs.plugins.flatpakGradleGenerator)
    alias(libs.plugins.shadow)
    alias(libs.plugins.versions)
    application
}

dependencies {
    implementation(project(":core"))

    implementation(libs.kotlinxCoroutines)
    implementation(libs.kotlinxSerialization)
    implementation(libs.log4jSlf4j2Impl)
    runtimeOnly(libs.log4jCore)

    implementation(libs.bundles.javaGiEcosystem)
    implementation(libs.stargate)
}

application {
    applicationName = "speedofsound"
    mainClass = "com.zugaldia.speedofsound.app.AppKt"
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED") // See: https://java-gi.org/usage/#linux
}

// Determine the version: CI release tag > VERSION file + dev suffix > fallback
val appVersion = project.findProperty("releaseVersion") as String?
    ?: rootProject.file("VERSION").let { if (it.exists()) "${it.readText().trim()}-dev" else null }
    ?: "0.0.0-dev"

// Feature flags from properties
val disableGioStore = (project.findProperty("speedofsound.disableGioStore") as String?)?.toBoolean() ?: false
val disableGStreamer = (project.findProperty("speedofsound.disableGStreamer") as String?)?.toBoolean() ?: false

buildConfig {
    packageName("com.zugaldia.speedofsound.app")
    buildConfigField("String", "VERSION", "\"$appVersion\"")
    buildConfigField("boolean", "DISABLE_GIO_STORE", disableGioStore.toString())
    buildConfigField("boolean", "DISABLE_GSTREAMER", disableGStreamer.toString())
}

tasks.shadowJar {
    archiveFileName.set("speedofsound.jar")
}

tasks.flatpakGradleGenerator {
    outputFile = file("flatpak-sources.json")
    downloadDirectory = "offline-repository"
    excludeConfigurations = listOf("testCompileClasspath", "testRuntimeClasspath")
}
