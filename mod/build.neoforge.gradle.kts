plugins {
    id("net.neoforged.moddev") version "2.0.137"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}
val modVersion = property("mod.version").toString()
val loader = stonecutter.current.project.substringAfterLast('-')

group = project.property("maven_group").toString()
version = "${modVersion}+${stonecutter.current.project}"

repositories {
    mavenCentral()
    mavenLocal()
}

val minConfiguredVersion = "0.3"
val configuredVersion = "0.3.1"

dependencies {
    // Configured
    listOf(
        "de.clickism:configured-core:${configuredVersion}",
        "de.clickism:configured-yaml:${configuredVersion}",
        "de.clickism:configured-json:${configuredVersion}",
        "de.clickism:configured-neoforge-command-adapter:${configuredVersion}"
    ).forEach {
        jarJar(implementation(it) { isChanging = true }) {
            version {
                strictly("[$minConfiguredVersion,)")
            }
        }
    }
    // Configured Dependency
    jarJar(implementation("org.yaml:snakeyaml:2.0")!!)
}

val atPath = if (stonecutter.eval(stonecutter.current.version, ">=1.21.11")) {
    "META-INF/1.21.11.accesstransformer.cfg"
} else {
    "META-INF/1.21.10.accesstransformer.cfg"
}

neoForge {
    version = property("deps.neoforge").toString()
    accessTransformers.from(project.file("src/main/resources/$atPath"))
    runs {
        configureEach {
            dependencies {
                listOf(
                    "de.clickism:configured-core:${configuredVersion}",
                    "de.clickism:configured-yaml:${configuredVersion}",
                    "de.clickism:configured-json:${configuredVersion}",
                    "org.yaml:snakeyaml:2.0"
                ).forEach {
                    implementation(it)
                    val config = if (neoForge.versionCapabilities.legacyClasspath()) {
                        "additionalRuntimeClasspath"
                    } else {
                        "implementation"
                    }
                    add(config, it)
                }
            }
        }
        val runDir = "../../run"
        register("client") {
            client()
            gameDirectory = file(runDir)
            ideName = "NeoForge Client (${stonecutter.active?.version})"
            programArgument("--username=ClickToPlay")
        }
        register("server") {
            server()
            gameDirectory = file(runDir)
            ideName = "NeoForge Server (${stonecutter.active?.version})"
        }
    }

    mods {
        register(property("mod.id").toString()) {
            sourceSet(sourceSets["main"])
        }
    }
}

base {
    archivesName.set(property("archives_base_name").toString())
}

stonecutter {
    replacements {
        string(current.parsed < "1.21.11") {
            replace("Identifier", "ResourceLocation")
            replace("net.minecraft.world.entity.npc.villager", "net.minecraft.world.entity.npc")
            replace("net.minecraft.world.entity.monster.zombie", "net.minecraft.world.entity.monster")
        }
    }
}

tasks.processResources {
    val properties = mapOf(
        "mod_version" to modVersion,
        "minecraft_version_range" to project.property("mod.minecraft_version_range"),
        "minecraft_version" to project.property("mod.minecraft_version"),
    )
    filesMatching(listOf("META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(properties)
    }
    inputs.properties(properties)
}


publishMods {
    displayName.set("ClickVillagers ${property("mod.version")} for NeoForge")
    file.set(tasks.jar.get().archiveFile)
    version.set(project.version.toString())
    changelog.set(rootProject.file("mod/CHANGELOG.md").readText())
    type.set(STABLE)
    modLoaders.add("neoforge")
    val mcVersions = property("mod.publishing_target_minecraft_versions").toString().split(',')
    modrinth {
        accessToken.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("BITzwT7B")
        minecraftVersions.addAll(mcVersions)
    }
    curseforge {
        accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
        projectId.set("1162587")
        clientRequired.set(false)
        serverRequired.set(true)
        minecraftVersions.addAll(mcVersions)
    }
}
