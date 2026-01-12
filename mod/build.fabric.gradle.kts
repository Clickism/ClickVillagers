plugins {
	id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
	id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

val modVersion = property("mod.version").toString()
val minecraftVersion = stonecutter.current.project.substringBeforeLast('-')
val loader = stonecutter.current.project.substringAfterLast('-')

version = "$modVersion+$minecraftVersion-$loader"
group = project.property("maven_group").toString()

base {
	archivesName.set(property("archives_base_name").toString())
}

repositories {
	maven("https://maven.nucleoid.xyz")
	mavenLocal()
}

val configuredVersion = "0.3.1"

dependencies {
	// Fabric
	minecraft("com.mojang:minecraft:$minecraftVersion")
	mappings(loom.officialMojangMappings())
	modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
	// GUI Library
	modImplementation(include("de.clickism:fgui-fabric:1.0+$minecraftVersion")!!)
	// Update Checker
	implementation(include("de.clickism:modrinth-update-checker:1.0")!!)
	// Configured
	implementation(include("de.clickism:configured-core:${configuredVersion}")!!)
	implementation(include("de.clickism:configured-yaml:${configuredVersion}")!!)
	implementation(include("de.clickism:configured-json:${configuredVersion}")!!)
	modImplementation(include("de.clickism:configured-fabric-command-adapter:${configuredVersion}")!!)
	// Configured Dependency
	implementation(include("org.yaml:snakeyaml:2.0")!!)
}

val awPath = if (stonecutter.eval(stonecutter.current.version, ">=1.21.11")) {
	"1.21.11.clickvillagers.accesswidener"
} else {
	"1.21.10.clickvillagers.accesswidener"
}

tasks.processResources {
	val props = mapOf(
		"mod_version" to version,
		"minecraft_version" to project.property("mod.minecraft_version"),
		"fabric_loader_version" to project.property("deps.fabric_loader"),
		"access_widener_path" to awPath
	)
	filesMatching("fabric.mod.json") {
		expand(props)
	}
	inputs.properties(props)
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

java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(21))
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${project.base.archivesName.get()}" }
	}
}

publishMods {
	displayName.set("ClickVillagers ${property("mod.version")} for Fabric")
	file.set(tasks.remapJar.get().archiveFile)
	version.set(project.version.toString())
	changelog.set(rootProject.file("mod/CHANGELOG.md").readText())
	type.set(STABLE)
	modLoaders.add("fabric")
	val mcVersions = property("mod.publishing_target_minecraft_versions").toString().split(',')
	modrinth {
		accessToken.set(System.getenv("MODRINTH_TOKEN"))
		projectId.set("BITzwT7B")
		requires("fabric-api")
		minecraftVersions.addAll(mcVersions)
	}
	curseforge {
		accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
		projectId.set("1162587")
		clientRequired.set(false)
		serverRequired.set(true)
		requires("fabric-api")
		minecraftVersions.addAll(mcVersions)
	}
}

loom {
    this.accessWidenerPath.set(rootProject.file("mod/src/main/resources/${awPath}"))
	runConfigs.all {
		ideConfigGenerated(true)
		runDir = "../../run"
	}
}