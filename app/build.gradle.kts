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

    testImplementation(kotlin("test"))
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

//
// We use the built-in `jpackage` for DEB, RPM, and app-image generation (straightforward but bundles the JRE).
// Potential alternative (from Netflix?): https://github.com/nebula-plugins/gradle-ospackage-plugin
//

val jpackageInputDir = layout.buildDirectory.dir("libs")
val jpackageOutputDir = layout.buildDirectory.dir("jpackage")
val jpackageCommonArgs = listOf(
    "--input", jpackageInputDir.get().asFile.absolutePath,
    "--dest", jpackageOutputDir.get().asFile.absolutePath,
    "--main-class", "com.zugaldia.speedofsound.app.AppKt",
    "--main-jar", "speedofsound.jar",
    "--java-options", "--enable-native-access=ALL-UNNAMED",
    "--name", "speedofsound",
    "--description", "Voice typing for the Linux desktop",
    "--app-version", appVersion.substringBefore("-"),
    "--vendor", "Speed of Sound",
    "--copyright", "Copyright 2026 Antonio Zugaldia",
    "--icon", rootProject.file("assets/logo/logo-square-512.png").absolutePath,
)

// These flags are only valid for installer package types (deb, rpm), not app-image
val jpackageLinuxInstallerArgs = listOf(
    "--license-file", rootProject.file("LICENSE").absolutePath,
    "--about-url", "https://www.speedofsound.io",
    "--linux-app-category", "Utility",
    "--linux-menu-group", "Utility",
    "--linux-shortcut",
)

listOf("deb", "rpm", "app-image").forEach { packageType ->
    tasks.register<Exec>("jpackage-$packageType") {
        group = "distribution"
        description = "Package the app as a $packageType using jpackage"
        dependsOn(tasks.shadowJar)
        doFirst { jpackageOutputDir.get().asFile.mkdirs() }
        commandLine(buildList {
            add("jpackage")
            addAll(jpackageCommonArgs)
            if (packageType != "app-image") {
                addAll(jpackageLinuxInstallerArgs)
            }
            if (packageType == "deb") {
                add("--linux-deb-maintainer"); add("antonio@zugaldia.com")
            }
            add("--type"); add(packageType)
        })
    }
}
