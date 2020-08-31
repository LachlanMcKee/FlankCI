package net.lachlanmckee.bitrise.core.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*
import javax.inject.Inject

internal class ErrorScreenFactoryImpl @Inject constructor() : ErrorScreenFactory {
    override suspend fun respondHtml(call: ApplicationCall, title: String, body: String) {
        call.respondHtml {
            head {
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +title }
                p {
                    body
                        .split("\n")
                        .forEach { body ->
                            text(body)
                            br()
                        }
                }
            }
        }
    }
}
