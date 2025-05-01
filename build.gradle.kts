plugins {
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

val fabricApiVersion = when (project.platform.mcVersionStr) {
    "1.21.1" -> project.property("fabricApiVersion12101") as String
    "1.20.1" -> project.property("fabricApiVersion12001") as String
    else -> throw IllegalArgumentException("Unsupported Minecraft version: ${project.platform.mcVersionStr}")
}

val javaVersion = when {
    project.platform.mcMinor >= 21 -> JavaVersion.VERSION_21
    else -> JavaVersion.VERSION_17
}

val finalJarsDir = "${project.rootDir}/jars"

base {
    val archiveBase: String by project
    archivesName.set("$archiveBase-${platform.loaderStr}-${getMinecraftVersionsForFileName()}")
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
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks {
    processResources {
        inputs.property("java", javaVersion.majorVersion)
        inputs.property("version", project.version)
        inputs.property("mcVersionStr", project.platform.mcVersionStr)
        filesMatching(listOf("fabric.mod.json")) {
            expand(
                mapOf(
                    "version" to project.version,
                    "minecraftVersions" to getMinecraftVersionsForFabric(),
                    "mcVersionStr" to project.platform.mcVersionStr,
                    "java" to javaVersion.majorVersion,
                )
            )
        }
    }

    withType<Jar> {
        from(rootProject.file("LICENSE"))
    }

    register<Copy>("copyJars") {
        File(finalJarsDir).mkdir()
        from(remapJar.get().archiveFile)
        into(finalJarsDir)
        from(remapSourcesJar.get().archiveFile)
        into(finalJarsDir)
    }

    build {
        dependsOn("copyJars")
    }

    clean {
        delete(finalJarsDir)
    }
}


fun getMinecraftVersionsForFileName(): String {
    return when (project.platform.mcVersionStr) {
        "1.21.1" -> "1.21.0-1.21.3"
        "1.20.1" -> "1.20.x"
        else -> project.platform.mcVersionStr
    }
}

fun getMinecraftVersionsForFabric(): String {
    return when (project.platform.mcVersionStr) {
        "1.21.1" -> ">=1.21 <1.21.4"
        "1.20.1" -> "~1.20"
        else -> "~${project.platform.mcVersionStr}"
    }
}
