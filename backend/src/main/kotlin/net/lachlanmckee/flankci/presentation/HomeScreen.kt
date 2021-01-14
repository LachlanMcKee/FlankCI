package net.lachlanmckee.flankci.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import net.lachlanmckee.flankci.core.presentation.*

class HomeScreen {
  suspend fun respondHtml(call: ApplicationCall) {
    call.respondHtml {
      materialHeader()
      materialBody(
        title = "Flank CI Home",
        linksFunc = {
          materialStandardLink(
            text = "Test Runner",
            href = "/test-runner",
            icon = "directions_run",
            newWindow = false
          )
          materialStandardLink(
            text = "Test Results",
            href = "/test-results",
            icon = "poll",
            newWindow = false
          )
        },
        contentFunc = {}
      )
    }
  }
}
