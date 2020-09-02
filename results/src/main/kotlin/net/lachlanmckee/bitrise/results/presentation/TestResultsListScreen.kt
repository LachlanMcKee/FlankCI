package net.lachlanmckee.bitrise.results.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.results.domain.entity.TestResultModel
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
        testResultModelList: List<TestResultModel>
    ) {
        call.respondHtml {
            head {
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +"Bitrise Test Results" }
                div {
                    testResultModelList.forEach { build ->
                        span {
                            classes = setOf("heading")
                        }
                        span {
                            classes = when (build.status) {
                                "success" -> setOf("content", "test-success")
                                "in-progress" -> setOf("content", "test-in-progress")
                                else -> setOf("content", "test-failure")
                            }
                            b {
                                text("${build.branch} [${build.commitHash}]")
                            }
                            br()
                            br()

                            if (!build.jobName.isNullOrBlank()) {
                                text(build.jobName)
                                br()
                            }

                            text(build.triggeredAt)

                            if (build.finishedAt != null) {
                                text(" - ${build.finishedAt}")
                            }

                            br()
                            br()

                            a(href = build.bitriseUrl) {
                                target = "_blank"
                                text("Bitrise")
                            }

                            if (build.status != "in-progress") {
                                text(" | ")
                                a(href = "/test-results/${build.buildSlug}") {
                                    target = "_blank"
                                    text("Test Results")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
