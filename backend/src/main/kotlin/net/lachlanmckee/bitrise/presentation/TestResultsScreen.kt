package net.lachlanmckee.bitrise.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*
import net.lachlanmckee.bitrise.domain.interactor.TestResultsInteractor

class TestResultsScreen(private val testResultsInteractor: TestResultsInteractor) {
    suspend fun respondHtml(call: ApplicationCall) {
        val buildsData = testResultsInteractor
            .execute(call)
            .getOrThrow()

        call.respondHtml {
            head {
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +"Bitrise Test Results" }
                div {
                    p {
                        classes = setOf("heading")
                        text("Jobs:")
                    }
                    p {
                        classes = setOf("content")
                        id = "artifact-details"
                        text(buildsData.branchBuilds.map { it.value.toString() }.joinToString(separator = "<br/>"))
                    }
                }
            }
        }
    }
}
