plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("testing")
}

dependencies {
  api(project(":core"))

  implementation(Dependencies.androidDexParser)
  implementation(Dependencies.yamlParser)

  implementation(Dependencies.Dagger.dagger)
  kapt(Dependencies.Dagger.daggerCompiler)

  testImplementation(project(":core-test"))
  testImplementation(project(":core-impl"))
  testImplementation(project(":integration-bitrise"))
  testImplementation(Dependencies.Dagger.dagger)
  kaptTest(Dependencies.Dagger.daggerCompiler)
}
