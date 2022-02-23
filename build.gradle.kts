plugins {
	id("com.soywiz.korge")
	kotlin("kapt") version "1.5.21"
//	kotlin("plugin.serialization") version "1.5.31"
}

korge {
	targetJvm()
    targetJs()
	targetDesktop()
	serializationJson()
}

repositories {
	maven("https://jitpack.io")
}

val kiwiVersion: String by project
val kloggerVersion = "2.2.0"
val kotlinxSerializationVersion = "1.3.1"

kotlin {
	sourceSets {
		val commonMain by getting {
			kotlin.srcDir("$buildDir/generated/source/kaptKotlin/main")
			dependencies {
				implementation("com.soywiz.korlibs.klogger:klogger:$kloggerVersion")
				implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.21")
			}
		}

		val jvmMain by getting {
			dependencies {
				configurations.all { // kapt has an issue with determining the correct KMM library, so we need to help it
					if (name.contains("kapt")) {
						attributes.attribute(
							org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.attribute,
							org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm // pass in the JVM
						)
					}
				}
			}
		}
	}
}

tasks.getByName("compileKotlinMetadata").dependsOn("kaptKotlinJvm")

tasks {
}
