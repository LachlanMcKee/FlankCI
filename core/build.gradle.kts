plugins {
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  implementation(Dependencies.Kotlin.stdlib)
  api(Dependencies.Ktor.serverCore)
  implementation(Dependencies.Ktor.serverCore)
  api(Dependencies.Ktor.gson)
  implementation(Dependencies.Ktor.clientJson)
  implementation(Dependencies.Ktor.clientGson)

  // Json parsing
  api(Dependencies.GsonPath.gsonpath)
  implementation(Dependencies.GsonPath.gsonpathKt)
  kapt(Dependencies.GsonPath.gsonpathCompiler)

  // XML parsing
  api(Dependencies.Jackson.kotlinModule)

  implementation(Dependencies.Dagger.dagger)
  kapt(Dependencies.Dagger.daggerCompiler)
}

kapt {
  arguments {
    arg("gsonpath.incremental", "true")
  }
}
