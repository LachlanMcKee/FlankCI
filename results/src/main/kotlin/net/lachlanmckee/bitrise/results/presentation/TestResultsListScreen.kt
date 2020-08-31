package net.lachlanmckee.bitrise.results.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*
import net.lachlanmckee.bitrise.core.data.entity.BuildsData
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.results.domain.interactor.TestResultsListInteractor

internal class TestResultsListScreen(
    private val testResultsListInteractor: TestResultsListInteractor,
    private val errorScreenFactory: ErrorScreenFactory
) {
    suspend fun respondHtml(call: ApplicationCall) {
        testResultsListInteractor
            .execute()
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
                    buildsData.branchBuilds.entries.forEach { (branch, builds) ->
                        builds.forEach { build ->
                            p {
                                classes = if (build.status == "error") {
                                    setOf("content", "test-failure")
                                } else {
                                    setOf("content", "test-success")
                                }
                                text("Branch: $branch")
                                br()
                                text("Result: $build")
                                br()
                                a(href = "/test-results/${build.buildSlug}") {
                                    target = "_blank"
                                    text("Details")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
