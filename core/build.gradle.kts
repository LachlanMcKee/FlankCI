plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Dependencies.Kotlin.version}")
    api(Dependencies.Ktor.serverCore)

    // Json parsing
    implementation("net.lachlanmckee:gsonpath:4.0.0")
    implementation("net.lachlanmckee:gsonpath-kt:4.0.0")
    kapt("net.lachlanmckee:gsonpath-compiler:4.0.0")

    // XML parsing
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.2")

    implementation("com.google.dagger:dagger:2.28.3")
    kapt("com.google.dagger:dagger-compiler:2.28.3")
}

kapt {
    arguments {
        arg("gsonpath.incremental", "true")
    }
}
