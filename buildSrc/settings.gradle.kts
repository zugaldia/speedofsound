pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("../offline-repository") } // Used by Flatpak Builder
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        maven { url = uri("../offline-repository") } // Used by Flatpak Builder
    }

    // Reuse the version catalog from the main build.
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "buildSrc"
