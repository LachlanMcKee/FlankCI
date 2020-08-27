package net.lachlanmckee.bitrise.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*

class TestResultsScreen {
    suspend fun respondHtml(call: ApplicationCall) {
        call.respondHtml {
            head {
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +"Bitrise Test Results" }
                p {
                    text("TODO")
                }
            }
        }
    }
}
