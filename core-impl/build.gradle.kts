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
