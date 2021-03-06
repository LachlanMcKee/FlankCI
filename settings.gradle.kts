pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

rootProject.name = "test-launcher"

include(":backend")
include(":core")
include(":core-impl")
include(":core-test")
include(":runner")
include(":results")
include(":integration-bitrise")
