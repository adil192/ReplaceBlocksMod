pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://repo.essential.gg/repository/maven-public")
		maven("https://maven.architectury.dev")
		maven("https://maven.fabricmc.net/")
		maven("https://maven.minecraftforge.net")
	}
	plugins {
		val egtVersion = "0.6.7"
		id("gg.essential.multi-version.root") version egtVersion
		id("gg.essential.multi-version.api-validation") version egtVersion
	}
}

listOf(
	"1.21.1-fabric",
	"1.20.1-fabric",
).forEach { version ->
	include(":$version")
	project(":$version").apply {
		projectDir = file("versions/$version")
		buildFileName = "../../build.gradle.kts"
	}
}

rootProject.buildFileName = "root.gradle.kts"
