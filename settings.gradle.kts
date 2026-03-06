pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("offline-repository") } // Used by Flatpak Builder
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        maven { url = uri("offline-repository") } // Used by Flatpak Builder
//        mavenLocal()
//        maven {
//            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
//        }
    }
}

include(":app")
include(":cli")
include(":core")

rootProject.name = "speedofsound"
