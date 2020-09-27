buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:6.0.0")
    }
}

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("application")
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

val engineClass = "io.ktor.server.netty.EngineMain"
application {
    mainClassName = engineClass
}

repositories {
    maven(url = "https://dl.bintray.com/kotlin/ktor")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    maven(url = "https://kotlin.bintray.com/kotlin-js-wrappers")
}

dependencies {
    implementation(project(":core-impl"))
    implementation(project(":runner"))
    implementation(project(":results"))

    implementation("io.ktor:ktor-server-netty:${Dependencies.Ktor.version}")
    implementation("ch.qos.logback:logback-classic:1.2.1")
    implementation("io.ktor:ktor-server-core:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-html-builder:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-client-core:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-client-core-jvm:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-client-apache:${Dependencies.Ktor.version}")

    implementation("io.ktor:ktor-gson:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-client-json:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-client-gson:${Dependencies.Ktor.version}")

    implementation("com.google.dagger:dagger:2.28.3")
    kapt("com.google.dagger:dagger-compiler:2.28.3")
}

tasks.shadowJar {
    manifest {
        attributes(mapOf("Main-Class" to engineClass))
    }
}
