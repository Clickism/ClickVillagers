plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    id("xyz.jpenilla.run-paper") version "2.3.1"
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
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://libraries.minecraft.net/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.bstats.org/content/repositories/releases/")
    maven("https://jitpack.io")
}

val configuredVersion = "0.2.4"

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:22.0.0")
    // ClickGUI
    implementation("com.github.Clickism:ClickGUI:master-SNAPSHOT")
    // Configuration & Localization
    implementation("de.clickism:configured-core:$configuredVersion")
    implementation("de.clickism:configured-yaml:$configuredVersion")
    implementation("de.clickism:configured-json:$configuredVersion")
    implementation("de.clickism:configured-localization:$configuredVersion")
    // Update Checker
    implementation("de.clickism:modrinth-update-checker:1.0")
    // Metrics
    implementation("org.bstats:bstats-bukkit:3.1.0")
    // PaperLib
    implementation("io.papermc:paperlib:1.0.8")
}

tasks.runServer {
    dependsOn(tasks.build)
    minecraftVersion("1.21.10")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks.shadowJar {
    archiveClassifier.set("")
    mergeServiceFiles()
    isEnableRelocation = true
    relocationPrefix = "de.clickism.clickvillagers.shadow"
    // Exclude Gson and Snakeyaml since it is already provided in Spigot
    dependencies {
        exclude(dependency("com.google.code.gson:gson"))
        exclude(dependency("org.yaml:snakeyaml"))
    }
    // Stop Gson and Snakeyaml from being relocated
    relocate("com.google.gson", "com.google.gson")
    relocate("org.yaml.snakeyaml", "org.yaml.snakeyaml")
    relocate("io.papermc.lib", "$relocationPrefix.paperlib")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to pluginVersion)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}