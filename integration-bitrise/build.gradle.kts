plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("testing")
}

dependencies {
  api(project(":core"))
  implementation(Dependencies.Ktor.clientCore)
  implementation(Dependencies.Ktor.clientCoreJvm)

  implementation(Dependencies.Dagger.dagger)
  kapt(Dependencies.Dagger.daggerCompiler)

  implementation(Dependencies.GsonPath.gsonpathKt)
  kapt(Dependencies.GsonPath.gsonpathCompiler)
}

kapt {
  arguments {
    arg("gsonpath.incremental", "true")
  }
}
