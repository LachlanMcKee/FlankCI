package net.lachlanmckee.bitrise

import com.google.gson.TypeAdapterFactory
import io.ktor.server.testing.*
import net.lachlanmckee.bitrise.core.presentation.RouteProvider
import net.lachlanmckee.bitrise.core.startApplication
import kotlin.test.assertEquals

fun <R> withTestApplication(
  typeAdapterFactories: Set<@JvmSuppressWildcards TypeAdapterFactory>,
  routerProviders: Set<RouteProvider>,
  test: TestApplicationEngine.() -> R
): R {
  return withApplication(createTestEnvironment()) {
    application.startApplication(
      typeAdapterFactories,
      routerProviders
    )
    test()
  }
}

fun readFile(contentFilePath: String): String {
  return ClassLoader.getSystemClassLoader().getResourceAsStream(contentFilePath)!!
    .bufferedReader().readText()
}

fun assertContentEquals(response: TestApplicationResponse, contentFilePath: String) {
  assertEquals(
    readFile(contentFilePath).removeWhiteSpaces(),
    response.content!!.removeWhiteSpaces()
  )
}

private fun String.removeWhiteSpaces(): String {
  return lineSequence().joinToString("\n") { line ->
    line
      .replace("^\\s+".toRegex(), "")
      .replace("\\s+$".toRegex(), "")
  }
}
