package net.lachlanmckee.flankci.results.integration

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.server.testing.*
import net.lachlanmckee.flankci.*
import net.lachlanmckee.flankci.core.data.CoreIoModule
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ResultsApplicationTest {

  @Test
  fun testResultsList() = withTestApplication(
    createTestHttpClientFactory { request ->
      when (request.url.fullPath) {
        "/v0.1/apps/APP_ID/builds?workflow=trigger_tests&sort_by=created_at" -> {
          respondJson("input/api/build-list-response.json")
        }
        else -> error("Unhandled ${request.url.fullPath}")
      }
    }
  ) {
    with(handleRequest(HttpMethod.Get, "/bitrise-sample/test-results")) {
      assertEquals(HttpStatusCode.OK, response.status())
      assertContentEquals(response, "output/test-results-list/expected.html")
    }
  }

  @Test
  fun testResultWithValidContent() = withTestApplication(
    createTestHttpClientFactory { request ->
      when (request.url.fullPath) {
        "/v0.1/apps/APP_ID/builds/BUILD_SLUG/artifacts" -> {
          respondJson("input/api/artifact-list.json")
        }
        "/v0.1/apps/APP_ID/builds/BUILD_SLUG/artifacts/ARTIFACT_SLUG_2" -> {
          respondJson("input/api/artifact-detail-cost-report.json")
        }
        "/builds/BUILD_SLUG/artifacts/ARTIFACT_SLUG_2/CostReport.txt" -> {
          respondJson("input/api/CostReport.txt")
        }
        "/v0.1/apps/APP_ID/builds/BUILD_SLUG/artifacts/ARTIFACT_SLUG_1" -> {
          respondJson("input/api/artifact-detail-junit.json")
        }
        "/builds/BUILD_SLUG/artifacts/ARTIFACT_SLUG_1/JUnitReport.xml" -> {
          respondJson("input/api/junit-report.xml")
        }
        "/v0.1/apps/APP_ID/builds/BUILD_SLUG/log" -> {
          respondJson("input/api/build-log-response.json")
        }
        "/build-logs-v2/BUILD_LOG_1" -> {
          respondJson("input/api/build-log.txt")
        }
        else -> error("Unhandled ${request.url.fullPath}")
      }
    }
  ) {
    with(handleRequest(HttpMethod.Get, "/bitrise-sample/test-results/BUILD_SLUG")) {
      assertEquals(HttpStatusCode.OK, response.status())
      assertContentEquals(response, "output/test-result/success.html")
    }
  }

  @Test
  fun testResultWithFailureToFetchTests() = withTestApplication(
    createTestHttpClientFactory { request ->
      when (request.url.fullPath) {
        "/v0.1/apps/APP_ID/builds/BUILD_SLUG/artifacts" -> {
          respondError(HttpStatusCode.InternalServerError)
        }
        "/v0.1/apps/APP_ID/builds/BUILD_SLUG/log" -> {
          respondJson("input/api/build-log-response.json")
        }
        "/build-logs-v2/BUILD_LOG_1" -> {
          respondJson("input/api/build-log.txt")
        }
        else -> error("Unhandled ${request.url.fullPath}")
      }
    }
  ) {
    with(handleRequest(HttpMethod.Get, "/bitrise-sample/test-results/BUILD_SLUG")) {
      assertEquals(HttpStatusCode.OK, response.status())
      assertContentEquals(response, "output/test-result/failure-no-tests-found.html")
    }
  }

  private fun <R> withTestApplication(
    testHttpClientFactory: TestHttpClientFactory,
    test: TestApplicationEngine.() -> R
  ): R {
    val component: ResultsTestComponent = DaggerResultsTestComponent
      .builder()
      .coreIoModule(CoreIoModule(testHttpClientFactory, TestFileReader()))
      .build()

    return withTestApplication(component.typeAdapterFactories(), component.routeProviders(), test)
  }
}
