import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = Dependencies.Kotlin.version))
    }
}

plugins {
    id("com.diffplug.spotless") version "5.6.1"
}

spotless {
    format("misc") {
        target("*.md", ".gitignore")
        trimTrailingWhitespace()
        indentWithSpaces(2)
        endWithNewline()
    }
    kotlin {
        ktlint(Dependencies.ktlintVersion).userData(
            mapOf(
                "indent_size" to "2",
                "disabled_rules" to "no-wildcard-imports"
            )
        )
        target("**/*.kt")
        trimTrailingWhitespace()
        endWithNewline()
        targetExclude("**/build/**")
    }
    kotlinGradle {
        ktlint(Dependencies.ktlintVersion).userData(mapOf("indent_size" to "2"))
        target("**/*.gradle.kts")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

subprojects {
    repositories {
        mavenCentral()
        jcenter()
    }

    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf(
                "-Xallow-result-return-type"
            )
        }
    }
}
