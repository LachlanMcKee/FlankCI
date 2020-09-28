plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("testing")
}

dependencies {
    api(project(":core"))
    implementation(Dependencies.Ktor.htmlBuilder)
    implementation(Dependencies.Ktor.clientCore)
    implementation(Dependencies.Ktor.clientCoreJvm)
    implementation(Dependencies.Ktor.clientApache)

    implementation(Dependencies.Ktor.gson)
    implementation(Dependencies.Ktor.clientJson)
    implementation(Dependencies.Ktor.clientGson)

    implementation(Dependencies.Ktor.clientLogging)
    implementation(Dependencies.Ktor.clientLoggingJvm)

    // XML parsing
    implementation(Dependencies.Jackson.xml)

    implementation(Dependencies.Dagger.dagger)
    kapt(Dependencies.Dagger.daggerCompiler)

    implementation(Dependencies.GsonPath.gsonpathKt)
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

    sourceDirectories.setFrom(files("${project.projectDir}/src/main/kotlin"))
    classDirectories.setFrom(files(fileTree("$buildDir/classes/kotlin") {
        exclude("**/*Component*.*", "**/*Module*.*")
    }))
    executionData.setFrom(fileTree("$buildDir") {
        include("jacoco/test.exec")
    })
}
