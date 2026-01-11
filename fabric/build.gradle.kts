plugins {
	id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
	id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

val minecraftVersion = stonecutter.current.project
version = "${parent?.name}-${property("mod.version")}+$minecraftVersion"
group = project.property("maven_group").toString()

base {
	archivesName.set(property("archives_base_name").toString())
}

repositories {
	maven("https://maven.nucleoid.xyz")
	mavenLocal()
}

val configuredVersion = "0.3"
val linenVersion = "0.1"
val fguiVersion = "0.1"

dependencies {
	// Fabric
	minecraft("com.mojang:minecraft:$minecraftVersion")
	mappings("net.fabricmc:yarn:${property("deps.yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
	// GUI Library
	modImplementation(include("de.clickism:fgui-fabric:1.0+$minecraftVersion")!!)
	// Update checker
	implementation(include("de.clickism:modrinth-update-checker:1.0")!!)
	// Linen
	implementation(include("de.clickism:linen-core-api:${linenVersion}")!!)
	modImplementation(include("de.clickism:linen-core-fabric:${linenVersion}+$minecraftVersion")!!)
	// Linen Dependencies
	implementation(include("net.kyori:adventure-api:4.25.0")!!)
	implementation(include("net.kyori:adventure-text-minimessage:4.25.0")!!)
	implementation(include("net.kyori:adventure-text-serializer-legacy:4.25.0")!!)
	// Configured
	implementation(include("de.clickism:configured-core:${configuredVersion}")!!)
	implementation(include("de.clickism:configured-yaml:${configuredVersion}")!!)
	implementation(include("de.clickism:configured-json:${configuredVersion}")!!)
	modImplementation(include("de.clickism:configured-fabric-command-adapter:${configuredVersion}")!!)
	// Configured Dependency
	implementation(include("org.yaml:snakeyaml:2.0")!!)
}

val accessWidener = if (stonecutter.eval(stonecutter.current.version, ">=1.21.11")) {
	"1.21.11.clickvillagers.accesswidener"
} else {
	"1.21.10.clickvillagers.accesswidener"
}

tasks.processResources {
	val properties = mapOf(
		"version" to version,
		"targetVersion" to project.property("mod.mc_version"),
		"minecraftVersion" to stonecutter.current.version,
		"fabricVersion" to project.property("deps.fabric_loader"),
		"accessWidenerPath" to accessWidener
	)

	filesMatching("fabric.mod.json") {
		expand(properties)
	}
	inputs.properties(properties)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
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
	changelog.set(rootProject.file("fabric/CHANGELOG.md").readText())
	type.set(STABLE)
	modLoaders.add("fabric")
	val mcVersions = property("mod.target_mc_versions").toString().split(',')
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
    accessWidenerPath.set(rootProject.file("fabric/src/main/resources/${accessWidener}"))
	runConfigs.all {
		ideConfigGenerated(true)
		runDir = "../../run"
	}
}