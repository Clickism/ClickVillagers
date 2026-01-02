plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

val pluginVersion = property("plugin_version").toString()

group = "de.clickism"
version = "$name-$pluginVersion"

base {
    archivesName.set(property("archives_base_name").toString())
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
//    maven("https://repo.bstats.org/content/repositories/releases/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

val configuredVersion = "0.3"

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    // GUI
    implementation("de.clickism:click-gui:1.1")
    // Configuration & Localization
    implementation("de.clickism:configured-core:$configuredVersion")
    implementation("de.clickism:configured-yaml:$configuredVersion")
    implementation("de.clickism:configured-json:$configuredVersion")
    implementation("de.clickism:configured-localization:$configuredVersion")
    // Update Checker
    implementation("de.clickism:modrinth-update-checker:1.0")
    // Metrics
    implementation("org.bstats:bstats-bukkit:3.1.0")
    // Other
    compileOnly("org.jetbrains:annotations:22.0.0")
}

tasks.runServer {
    dependsOn(tasks.build)
    minecraftVersion("1.21.10")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.shadowJar {
    archiveClassifier.set("")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles()
    enableAutoRelocation = true
    relocationPrefix = "shadow.de.clickism.clickvillagers"
    // Exclude Gson and Snakeyaml since it is already provided in Spigot
    dependencies {
        exclude(dependency("com.google.code.gson:gson"))
        exclude(dependency("org.yaml:snakeyaml"))
    }
    // Stop Gson and Snakeyaml from being relocated
    relocate("com.google.gson", "com.google.gson")
    relocate("org.yaml.snakeyaml", "org.yaml.snakeyaml")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.processResources {
    val props = mapOf("version" to pluginVersion)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

publishMods {
    displayName.set("ClickVillagers $pluginVersion for Paper")
    file.set(tasks.shadowJar.get().archiveFile)
    version.set(project.version.toString())
    changelog.set(rootProject.file("paper/CHANGELOG.md").readText())
    type.set(STABLE)
    modLoaders.add("paper")
    modLoaders.add("purpur")
    val mcVersionStart = "1.21"
    val mcVersionEnd = "1.21.10"
    modrinth {
        accessToken.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("BITzwT7B")
        minecraftVersionRange {
            start = mcVersionStart
            end = mcVersionEnd
        }
    }
}