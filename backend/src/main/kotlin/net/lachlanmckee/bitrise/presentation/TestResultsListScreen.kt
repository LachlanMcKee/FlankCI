package net.lachlanmckee.bitrise.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*
import net.lachlanmckee.bitrise.data.entity.BuildsData
import net.lachlanmckee.bitrise.domain.interactor.TestResultsInteractor

class TestResultsListScreen(
    private val testResultsInteractor: TestResultsInteractor,
    private val errorScreenFactory: ErrorScreenFactory
) {
    suspend fun respondHtml(call: ApplicationCall) {
        testResultsInteractor
            .execute(call)
            .onSuccess { render(call, it) }
            .onFailure { errorScreenFactory.respondHtml(call, "Failed to parse content", it.message!!) }
    }

    private suspend fun render(
        call: ApplicationCall,
        buildsData: BuildsData
    ) {
        call.respondHtml {
            head {
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +"Bitrise Test Results" }
                div {
                    p {
                        classes = setOf("heading")
                    }
                    buildsData.branchBuilds.values.forEach { builds: List<BuildsData.Build> ->
                        builds.forEach { build ->
                            p {
                                classes = setOf("content")
                                id = "artifact-details"

                                a(href = "/test-result/${build.buildSlug}") {
                                    text(build.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
