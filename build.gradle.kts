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
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.amazonaws:aws-java-sdk:1.11.163")
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.4")
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
            packageVersion = "1.0.0"
            windows {
                console = false
                menuGroup = "Sr Sqs"
                iconFile.set(project.file("src/main/resources/icon.ico"))
            }
            linux {
                iconFile.set(project.file("src/main/resources/sr-sqs-icon.png"))
            }
        }
    }
}