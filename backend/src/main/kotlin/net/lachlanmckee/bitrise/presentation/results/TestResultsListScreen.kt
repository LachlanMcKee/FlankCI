package net.lachlanmckee.bitrise.presentation.results

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*
import net.lachlanmckee.bitrise.data.entity.BuildsData
import net.lachlanmckee.bitrise.domain.interactor.TestResultsListInteractor
import net.lachlanmckee.bitrise.presentation.ErrorScreenFactory

class TestResultsListScreen(
    private val testResultsListInteractor: TestResultsListInteractor,
    private val errorScreenFactory: ErrorScreenFactory
) {
    suspend fun respondHtml(call: ApplicationCall) {
        testResultsListInteractor
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
                                a(href = "/test-results/${build.buildSlug}") {
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
