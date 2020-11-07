package net.lachlanmckee.bitrise.presentation

import io.ktor.http.*
import io.ktor.server.testing.*
import net.lachlanmckee.bitrise.*
import net.lachlanmckee.bitrise.core.data.CoreIoModule
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ApplicationTest {

  @Test
  fun testHomeRequest() = withTestApplication(createTestHttpClientFactory()) {
    with(handleRequest(HttpMethod.Get, "/")) {
      assertEquals(HttpStatusCode.OK, response.status())
      assertContentEquals(response, "output/home/expected.html")
    }
  }

  private fun <R> withTestApplication(
    testHttpClientFactory: TestHttpClientFactory,
    test: TestApplicationEngine.() -> R
  ): R {
    val component: ApplicationComponent = DaggerApplicationComponent
      .builder()
      .coreIoModule(CoreIoModule(testHttpClientFactory, TestFileReader()))
      .build()

    return withTestApplication(component.typeAdapterFactories(), component.routeProviders(), test)
  }
}
