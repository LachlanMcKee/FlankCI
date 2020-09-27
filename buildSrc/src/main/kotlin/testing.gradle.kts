plugins {
    jacoco
}

jacoco {
    toolVersion = "0.8.5"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val testImplementation by configurations
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testImplementation("io.mockk:mockk:1.10.0")
}
