package net.lachlanmckee.bitrise.runner.integration

import io.ktor.http.*
import io.ktor.server.testing.*
import net.lachlanmckee.bitrise.*
import net.lachlanmckee.bitrise.core.data.CoreIoModule
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class RunnerApplicationTest {
  @Test
  fun testTestRunnerRequest() = withTestApplication(createTestHttpClientFactory()) {
    with(handleRequest(HttpMethod.Get, "/test-runner")) {
      assertEquals(HttpStatusCode.OK, response.status())
      assertContentEquals(response, "output/test-runner/expected.html")
    }
  }

  @Test
  fun testTestRerunRequest() = withTestApplication(
    createTestHttpClientFactory { request ->
      when (request.url.fullPath) {
        "/v0.1/apps/APP_ID/builds/BUILD_SLUG" -> {
          respondJson("input/api/build-response.json")
        }
        "/v0.1/apps/APP_ID/builds/BUILD_SLUG/artifacts" -> {
          respondJson("input/api/artifact-list.json")
        }
        "/v0.1/apps/APP_ID/builds/BUILD_SLUG/artifacts/ARTIFACT_SLUG_1" -> {
          respondJson("input/api/artifact-detail-junit.json")
        }
        "/builds/BUILD_SLUG/artifacts/ARTIFACT_SLUG_1/JUnitReport.xml" -> {
          respondJson("input/api/junit-report.xml")
        }
        else -> error("Unhandled ${request.url.fullPath}")
      }
    }
  ) {
    with(handleRequest(HttpMethod.Get, "/test-rerun?build-slug=BUILD_SLUG")) {
      assertEquals(HttpStatusCode.OK, response.status())
      assertContentEquals(response, "output/test-rerun/expected.html")
    }
  }

  private fun <R> withTestApplication(
    testHttpClientFactory: TestHttpClientFactory,
    test: TestApplicationEngine.() -> R
  ): R {
    val component: RunnerTestComponent = DaggerRunnerTestComponent
      .builder()
      .coreIoModule(CoreIoModule(testHttpClientFactory, TestFileReader))
      .build()

    return withTestApplication(component.typeAdapterFactories(), component.routeProviders(), test)
  }
}
