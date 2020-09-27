plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("testing")
}

dependencies {
    api(project(":core"))
    implementation("io.ktor:ktor-html-builder:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-client-core:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-client-core-jvm:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-client-apache:${Dependencies.Ktor.version}")

    implementation("io.ktor:ktor-gson:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-client-json:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-client-gson:${Dependencies.Ktor.version}")

    implementation("io.ktor:ktor-client-logging:${Dependencies.Ktor.version}")
    implementation("io.ktor:ktor-client-logging-jvm:${Dependencies.Ktor.version}")

    // XML parsing
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.11.2")

    implementation("com.google.dagger:dagger:2.28.3")
    kapt("com.google.dagger:dagger-compiler:2.28.3")

    implementation("net.lachlanmckee:gsonpath-kt:4.0.0")
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.isEnabled = false
        csv.isEnabled = false
        html.destination = file("${buildDir}/jacocoHtml")
    }
}
