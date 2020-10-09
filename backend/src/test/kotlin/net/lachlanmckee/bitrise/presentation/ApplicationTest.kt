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
            <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
            <link href="https://code.getmdl.io/1.3.0/material.indigo-pink.min.css" rel="stylesheet">
            <script src="https://code.getmdl.io/1.3.0/material.min.js"></script>
            <link href="/static/styles.css" rel="stylesheet" type="text/css">
          </head>
          <body>
            <h1>Bitrise Test Home</h1>
            <div><a href="/test-runner" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect green-button">Test Runner</a></div>
        <br>
            <div><a href="/test-results" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect green-button">Test Results</a></div>
          </body>
        </html>
        
        """.trimIndent(),
        response.content
      )
    }
  }
}
