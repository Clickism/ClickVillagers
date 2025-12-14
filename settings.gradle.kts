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


stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"
    create("fabric") {
        fun version(version: String, vararg loaders: String) {
            loaders.forEach {
                this.version("$version-$it", version)
                    .buildscript = "build.$it.gradle.kts"
            }
        }
        version("1.21.11", "fabric")
        listOf("1.21.10", "1.21.8", "1.21.5", "1.21.4", "1.21.1", "1.20.1").forEach {
            version(it, "fabric")
        }
        vcsVersion = "1.21.11-fabric"
    }
}