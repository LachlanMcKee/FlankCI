plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("testing")
}

dependencies {
    api(project(":core"))
    implementation("com.linkedin.dextestparser:parser:2.2.1")

    implementation("io.ktor:ktor-html-builder:${Dependencies.Ktor.version}")

    // Yaml parsing
    implementation("org.yaml:snakeyaml:1.26")

    implementation("com.google.dagger:dagger:2.28.3")
    kapt("com.google.dagger:dagger-compiler:2.28.3")

    testImplementation(project(":core-test"))
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
