plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinPluginSerialization)
    alias(libs.plugins.kover)
}

dependencies {
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.bundles.ktorEcosystem)
    implementation(libs.commonsCompress)
    implementation(libs.log4jSlf4j2Impl)
    runtimeOnly(libs.log4jCore)

    implementation(libs.stargate)

    // Sherpa is not published to Maven Central, we need to add the libraries manually.
    // https://k2-fsa.github.io/sherpa/onnx/java-api/non-android-java.html
    api(files("libs/sherpa-onnx-v1.12.24.jar"))
    api(files("libs/sherpa-onnx-native-lib-linux-x64-v1.12.24.jar"))
    api(files("libs/sherpa-onnx-native-lib-linux-aarch64-v1.12.24.jar"))

    implementation(libs.anthropic)
    implementation(libs.googleGenai)
    implementation(libs.openai)

    testImplementation(kotlin("test"))
}
