plugins {
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  api(project(":core"))
  api(Dependencies.Ktor.serverTestHost)
  api(Dependencies.Ktor.clientMock)
  api(Dependencies.Ktor.clientMockJvm)
}
