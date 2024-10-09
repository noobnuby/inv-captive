import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.20"
	id("com.gradleup.shadow") version "8.3.0"
	id("xyz.jpenilla.run-paper") version "2.3.1"
	id("io.papermc.paperweight.userdev") version "1.7.3"
}

val relocate = (findProperty("relocate") as? String)?.toBoolean() ?: true
val paper_version: String by project

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly(kotlin("stdlib"))
	implementation(kotlin("reflect"))
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
	compileOnly("io.papermc.paper:paper-api:${paper_version}-R0.1-SNAPSHOT")
	paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21

	toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
	jvmToolchain(21)
}

tasks {
	withType<JavaCompile> {
		options.encoding = "UTF-8"
	}

	withType<KotlinCompile> {
		compilerOptions {
			noJdk = false
			jvmTarget.set(JvmTarget.JVM_21)
		}
	}

	processResources {
		filesMatching("plugin.yml") {
			expand(project.properties)
		}
	}

	create<Jar>("paperJar") {
		archiveBaseName.set(rootProject.name)
		archiveClassifier.set("")

		from(sourceSets["main"].output)

		doLast {
			copy {
				from(archiveFile)
				into(File(rootDir, "run/plugins/"))
			}
		}
	}

	runServer {
		minecraftVersion("1.21.1")
	}
}