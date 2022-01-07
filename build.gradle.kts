import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.compose") version "0.4.0"
    id("io.gitlab.arturbosch.detekt").version("1.18.0-RC3")
}

group = "ma.dalfre"
version = "1.3.1"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.amazonaws:aws-java-sdk:1.11.163")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")

}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "15"
}
tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "sr-sqs"
            packageVersion = "1.3.1"
            windows {
                console = false
                menuGroup = "Sr Sqs"
                modules(
                    "java.instrument",
                    "java.management",
                    "java.naming",
                    "java.security.jgss",
                    "java.sql",
                    "jdk.unsupported"
                )
                iconFile.set(project.file("src/main/resources/icon.ico"))
            }
            linux {
                modules(
                    "java.instrument",
                    "java.management",
                    "java.naming",
                    "java.security.jgss",
                    "java.sql",
                    "jdk.unsupported"
                )
                iconFile.set(project.file("src/main/resources/sr-sqs-icon.png"))
            }
            macOS{
                modules(
                    "java.instrument",
                    "java.management",
                    "java.naming",
                    "java.security.jgss",
                    "java.sql",
                    "jdk.unsupported"
                )
                iconFile.set(project.file("src/main/resources/sr-sqs-icon.png"))
            }
        }
    }
}