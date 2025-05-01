plugins {
    id("fabric-loom") version "1.10-SNAPSHOT"
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

val minecraftVersion = project.property("minecraft_version") as String
val yarnMappings = project.property("yarn_mappings") as String
val loaderVersion = project.property("loader_version") as String
val fabricVersion = project.property("fabric_version") as String
val mockitoVersion = project.property("mockito_version") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings("net.fabricmc:yarn:${yarnMappings}:v2")
    modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")

    testImplementation("net.fabricmc:fabric-loader-junit:${loaderVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoVersion}")
}

tasks.named<ProcessResources>("processResources") {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to inputs.properties["version"])
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    withType<Jar> {
        from(rootProject.file("LICENSE"))
    }
    test {
        useJUnitPlatform()
    }
}
