plugins {
    jacoco
}

jacoco {
    toolVersion = "0.8.5"
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.withType<JacocoReport>())
}

tasks.withType<JacocoReport> {
    dependsOn(tasks.withType<Test>())

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

val testImplementation by configurations
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("io.mockk:mockk:1.10.0")
}
