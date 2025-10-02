pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.8"
}

rootProject.name = "ClickVillagers"

include("fabric", "spigot")

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"
    create("fabric") {
        versions("1.21.9", "1.21.8", "1.21.5", "1.21.4", "1.21.1", "1.20.1")
        vcsVersion = "1.21.8"
    }
}