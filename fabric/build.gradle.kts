plugins {
	id("fabric-loom") version "1.10-SNAPSHOT"
	id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

version = "${parent?.name}-${property("mod.version")}+${stonecutter.current.project}"
group = project.property("maven_group").toString()

base {
	archivesName.set(property("archives_base_name").toString())
}

repositories {
	maven {
		url = uri("https://maven.nucleoid.xyz")
	}
}

val configuredVersion = "0.2.4"

dependencies {
	minecraft("com.mojang:minecraft:${stonecutter.current.project}")
	mappings("net.fabricmc:yarn:${property("deps.yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
	modImplementation(include("eu.pb4:sgui:${property("deps.sgui")}")!!)
	// Configured
	implementation(include("de.clickism:configured-core:${configuredVersion}")!!)
	implementation(include("de.clickism:configured-yaml:${configuredVersion}")!!)
	implementation(include("de.clickism:configured-json:${configuredVersion}")!!)
	// Configured Dependency
	implementation(include("org.yaml:snakeyaml:2.0")!!)
}

tasks.processResources {
	val properties = mapOf(
		"version" to version,
		"targetVersion" to project.property("mod.mc_version"),
		"minecraftVersion" to stonecutter.current.version,
		"fabricVersion" to project.property("deps.fabric_loader")
	)

	filesMatching("fabric.mod.json") {
		expand(properties)
	}
	inputs.properties(properties)
}

java {
	val j21 = stonecutter.eval(stonecutter.current.version, ">=1.20.5")
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(if (j21) 21 else 17))
	}
	val javaVersion = if (j21) JavaVersion.VERSION_17 else JavaVersion.VERSION_17
	sourceCompatibility = javaVersion
	targetCompatibility = javaVersion
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
	changelog.set(rootProject.file("CHANGELOG.md").readText())
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
	runConfigs.all {
		ideConfigGenerated(true)
		runDir = "../../run"
	}
}