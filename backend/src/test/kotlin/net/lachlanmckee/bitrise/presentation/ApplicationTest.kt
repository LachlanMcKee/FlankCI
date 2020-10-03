package net.lachlanmckee.bitrise.presentation

import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ApplicationTest {

  @Test
  fun testRequests() = withTestApplication(createTestHttpClientFactory()) {
    with(handleRequest(HttpMethod.Get, "/")) {
      assertEquals(HttpStatusCode.OK, response.status())
      assertEquals(
        """
        <!DOCTYPE html>
        <html>
          <head>
            <link href="/static/styles.css" rel="stylesheet" type="text/css">
          </head>
          <body>
            <h1>Bitrise Test Home</h1>
            <div><a href="/test-runner">Test Runner</a></div>
            <div><a href="/test-results">Test Results</a></div>
          </body>
        </html>

        """.trimIndent(),
        response.content
      )
    }
  }
}
