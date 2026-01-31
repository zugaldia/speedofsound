plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(project(":generator"))
    implementation(libs.bundles.kotlinxEcosystem)
    implementation(libs.log4jApi)
    implementation(libs.commonsCompress)

    // Sherpa is not published to Maven Central, we need to add the libraries manually.
    // https://k2-fsa.github.io/sherpa/onnx/java-api/non-android-java.html
    api(files("libs/sherpa-onnx-v1.12.23.jar"))
    api(files("libs/sherpa-onnx-native-lib-linux-x64-v1.12.23.jar"))
    api(files("libs/sherpa-onnx-native-lib-linux-aarch64-v1.12.23.jar"))

    testImplementation(kotlin("test"))
}
