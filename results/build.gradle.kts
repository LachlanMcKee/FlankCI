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
        csv.isEnabled = false
        html.isEnabled = true
        xml.isEnabled = true
    }

    sourceDirectories.setFrom(files("${project.projectDir}/src/main/kotlin"))
    classDirectories.setFrom(files(fileTree("$buildDir/classes/kotlin") {
        exclude("**/*Component*.*", "**/*Module*.*")
    }))
    executionData.setFrom(fileTree("$buildDir") {
        include("jacoco/test.exec")
    })
}
