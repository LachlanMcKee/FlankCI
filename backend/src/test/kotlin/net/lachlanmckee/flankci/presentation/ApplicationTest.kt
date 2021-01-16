package net.lachlanmckee.flankci.presentation

import io.ktor.http.*
import io.ktor.server.testing.*
import net.lachlanmckee.flankci.*
import net.lachlanmckee.flankci.core.data.CoreIoModule
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ApplicationTest {

  @Test
  fun testHomeRequestWithSingleConfiguration() = withTestApplication(
    createTestHttpClientFactory(),
    configFileName = "config.json"
  ) {
    with(handleRequest(HttpMethod.Get, "/")) {
      assertEquals(HttpStatusCode.OK, response.status())
      assertContentEquals(response, "output/home/expected-with-single-config.html")
    }
  }

  @Test
  fun testHomeRequestWithMultipleConfigurations() = withTestApplication(
    createTestHttpClientFactory(),
    configFileName = "config-with-multiple-configurations.json"
  ) {
    with(handleRequest(HttpMethod.Get, "/")) {
      assertEquals(HttpStatusCode.OK, response.status())
      assertContentEquals(response, "output/home/expected-with-multiple-configs.html")
    }
  }

  private fun <R> withTestApplication(
    testHttpClientFactory: TestHttpClientFactory,
    configFileName: String,
    test: TestApplicationEngine.() -> R
  ): R {
    val component: ApplicationComponent = DaggerApplicationComponent
      .builder()
      .coreIoModule(CoreIoModule(testHttpClientFactory, TestFileReader(mapOf("config.json" to configFileName))))
      .build()

    return withTestApplication(component.typeAdapterFactories(), component.routeProviders(), test)
  }
}
