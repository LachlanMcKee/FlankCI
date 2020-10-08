plugins {
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  implementation(Dependencies.Kotlin.stdlib)
  api(Dependencies.Ktor.serverCore)

  // Json parsing
  implementation(Dependencies.GsonPath.gsonpath)
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
