plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("testing")
}

dependencies {
    api(project(":core"))

    implementation(Dependencies.Ktor.htmlBuilder)

    implementation(Dependencies.Dagger.dagger)
    kapt(Dependencies.Dagger.daggerCompiler)

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
