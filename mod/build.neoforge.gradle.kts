plugins {
    id("net.neoforged.moddev") version "2.0.137"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}
val modVersion = property("mod.version").toString()
val minecraftVersion = stonecutter.current.project.substringBeforeLast('-')
val loader = stonecutter.current.project.substringAfterLast('-')

group = project.property("maven_group").toString()
version = "$modVersion+$minecraftVersion"

repositories {
    mavenCentral()
    mavenLocal()
}

val minConfiguredVersion = "0.3"
val configuredVersion = "0.3.1"

dependencies {
    // Configured
    fun configured(module: String) {
        jarJar(implementation("de.clickism:configured-$module:$configuredVersion") { isChanging = true }) {
            version { strictly("[$minConfiguredVersion,)") }
        }
    }
    configured("core")
    configured("yaml")
    configured("json")
    configured("neoforge-command-adapter")
    jarJar(implementation("de.clickism:fgui-neoforge:1.0+$minecraftVersion") { isChanging = true }) {
        version { strictly("1.0+$minecraftVersion") }
    }
    // Configured Dependency
    jarJar(implementation("org.yaml:snakeyaml:2.0")!!)
    // Update Checker
    jarJar(implementation("de.clickism:modrinth-update-checker:1.0")!!)
}

val atPath = "META-INF/accesstransformer.cfg"

neoForge {
    version = property("deps.neoforge").toString()
    accessTransformers.from(rootProject.file("mod/src/main/resources/$atPath"))
    runs {
        configureEach {
            dependencies {
                listOf(
                    "de.clickism:configured-core:${configuredVersion}",
                    "de.clickism:configured-yaml:${configuredVersion}",
                    "de.clickism:configured-json:${configuredVersion}",
                    "de.clickism:fgui-neoforge:1.0+${minecraftVersion}",
                    "org.yaml:snakeyaml:2.0",
                    "de.clickism:modrinth-update-checker:1.0"
                ).forEach {
                    implementation(it)
                    if (neoForge.versionCapabilities.legacyClasspath()) {
                        add("additionalRuntimeClasspath", it)
                    }
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

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
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
        "access_transformer_path" to atPath
    )
    filesMatching(listOf("META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(properties)
    }
    inputs.properties(properties)
}

tasks.named("createMinecraftArtifacts") {
    // This tells NeoForge to wait until Stonecutter has generated the files
    mustRunAfter(tasks.named("stonecutterGenerate"))
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
