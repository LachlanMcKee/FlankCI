plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("testing")
}

dependencies {
    api(project(":core"))

    implementation("io.ktor:ktor-html-builder:${Dependencies.Ktor.version}")

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
