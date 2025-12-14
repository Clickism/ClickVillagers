plugins {
    kotlin("jvm") version "2.2.20"
}

group = project.property("maven_group").toString()
version = "${parent?.name}-${property("mod.version")}+${stonecutter.current.project}"

repositories {
    mavenCentral()
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

kotlin {
    jvmToolchain(21)
}

base {
    archivesName.set(property("archives_base_name").toString())
}

dependencies {
    implementation("thedarkcolour:kotlinforforge-neoforge:${property("deps.forge_kotlin")}")
}

tasks.processResources {
    val properties = mapOf(
        "mod_version" to version,
        "minecraft_version" to project.property("mod.minecraft_version"),
    )
    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(properties)
    }
    inputs.properties(properties)
}
//
//jsonlang {
//    languageDirectories = listOf("assets/${property("mod.id")}/lang")
//    prettyPrint = true
//}
//
//neoForge {
//    version = property("deps.neoforge") as String
//    validateAccessTransformers = true
//
//    if (hasProperty("deps.parchment")) parchment {
//        val (mc, ver) = (property("deps.parchment") as String).split(':')
//        mappingsVersion = ver
//        minecraftVersion = mc
//    }
//
//    runs {
//        register("client") {
//            gameDirectory = file("run/")
//            client()
//        }
//        register("server") {
//            gameDirectory = file("run/")
//            server()
//        }
//    }
//
//    mods {
//        register(property("mod.id") as String) {
//            sourceSet(sourceSets["main"])
//        }
//    }
//    sourceSets["main"].resources.srcDir("src/main/generated")
//}
//
//repositories {
//    mavenLocal()
//     { name = "KotlinForForge" }
//}
//
//
//
//tasks {
//    processResources {
//        exclude("**/fabric.mod.json", "**/*.accesswidener", "**/mods.toml")
//    }
//
//    named("createMinecraftArtifacts") {
//        dependsOn("stonecutterGenerate")
//    }
//
//    register<Copy>("buildAndCollect") {
//        group = "build"
//        from(jar.map { it.archiveFile })
//        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
//        dependsOn("build")
//    }
//}
//
//java {
//    withSourcesJar()
//    val javaCompat = if (stonecutter.eval(stonecutter.current.version, ">=1.20.5")) {
//        JavaVersion.VERSION_21
//    } else {
//        JavaVersion.VERSION_17
//    }
//    sourceCompatibility = javaCompat
//    targetCompatibility = javaCompat
//}
