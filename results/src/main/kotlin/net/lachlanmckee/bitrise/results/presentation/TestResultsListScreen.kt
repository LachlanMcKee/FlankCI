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
                    buildsData.branchBuilds.entries.forEach { (branch, builds) ->
                        builds.forEach { build ->
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
                                    text("$branch [${build.commitHash}]")
                                }
                                br()
                                br()

                                val jobName: String? = build.originalEnvironmentValueList
                                    .find { it.name == "JOB_NAME" }
                                    ?.value

                                if (!jobName.isNullOrBlank()) {
                                    text(jobName)
                                    br()
                                }

                                text("${build.triggeredAt} - ${build.finishedAt}")

                                br()
                                br()

                                if (build.status != "in-progress") {
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
}
