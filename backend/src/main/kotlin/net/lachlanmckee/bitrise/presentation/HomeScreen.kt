package net.lachlanmckee.bitrise.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*

class HomeScreen {
    suspend fun respondHtml(call: ApplicationCall) {
        call.respondHtml {
            head {
                link(rel = "stylesheet", href = "https://fonts.googleapis.com/icon?family=Material+Icons")
                link(rel = "stylesheet", href = "https://code.getmdl.io/1.3.0/material.indigo-pink.min.css")
                script {
                    src = "https://code.getmdl.io/1.3.0/material.min.js"
                }
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +"Bitrise Test Home" }

                div {
                    a(href = "/test-runner") {
                        classes = setOf("mdl-button mdl-button--colored", "mdl-js-button", "mdl-js-ripple-effect")
                        +"Test Runner"
                    }
                }
                br
                div {
                    a(href = "/test-results") {
                        classes = setOf("mdl-button mdl-button--colored", "mdl-js-button", "mdl-js-ripple-effect")
                        +"Test Results"
                    }
                }
            }
        }
    }
}
