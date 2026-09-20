pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.8"
}

rootProject.name = "ClickVillagers"

include("paper", "mod")

stonecutter {
    kotlinController = true
    create("mod") {
        fun version(version: String, vararg loaders: String) {
            loaders.forEach {
                this.version("$version-$it", version)
                    .buildscript = "build.$it.gradle.kts"
            }
        }
        version("1.21.1", "fabric", "neoforge")
        version("1.21.11", "fabric", "neoforge")
        version("26.1", "fabric+noremap")
        version("26.2", "fabric+noremap")
        version("26.3", "fabric+noremap")
        vcsVersion = "26.3-fabric+noremap"
    }
}