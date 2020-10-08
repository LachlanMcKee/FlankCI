package net.lachlanmckee.bitrise.presentation

import io.ktor.server.testing.*

fun <R> withTestApplication(testHttpClientFactory: TestHttpClientFactory, test: TestApplicationEngine.() -> R): R {
  return withApplication(createTestEnvironment()) {
    application.main(testHttpClientFactory)
    test()
  }
}
