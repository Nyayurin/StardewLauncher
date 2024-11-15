import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") version "2.0.20"
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.0.20"
}

group = "cn.yurin"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin {
    jvmToolchain(17)
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.desktop.currentOs)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            implementation("io.github.vinceglb:filekit-compose:0.8.7")
            implementation("com.kichik.pecoff4j:pecoff4j:0.4.1")
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            val os = System.getProperty("os.name")
            when {
                os.contains("Windows") -> targetFormats(TargetFormat.Msi, TargetFormat.Exe, TargetFormat.AppImage)
                os.contains("Linux") -> targetFormats(TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.AppImage)
                os.contains("Mac OS") -> targetFormats(TargetFormat.Dmg, TargetFormat.Pkg)
                else -> error("Unsupported OS: $os")
            }
            packageName = "StardewLauncher"
            packageVersion = "1.0.0"
            jvmArgs("-Dfile.encoding=UTF-8")

            linux {
                modules("jdk.security.auth")
            }

            buildTypes.release.proguard {
                obfuscate = true
                configurationFiles.from(project.file("compose-desktop.pro"))
            }
        }
    }
}