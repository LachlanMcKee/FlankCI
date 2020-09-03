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
                link(rel = "stylesheet", href = "https://fonts.googleapis.com/icon?family=Material+Icons")
                link(rel = "stylesheet", href = "https://code.getmdl.io/1.3.0/material.indigo-pink.min.css")
                script {
                    src = "https://code.getmdl.io/1.3.0/material.min.js"
                }
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +"Bitrise Test Results" }
                p {
                    classes = setOf("heading")
                }
                div {
                    testResultModelList.forEach { build ->
                        div {
                            classes = when (build.status) {
                                "success" -> setOf("test-result-card", "mdl-card", "mdl-shadow--2dp", "test-success")
                                "in-progress" -> setOf("test-result-card", "mdl-card", "mdl-shadow--2dp", "test-in-progress")
                                else -> setOf("test-result-card", "mdl-card", "mdl-shadow--2dp", "test-failure")
                            }
                            div {
                                classes = setOf("mdl-card__title")
                                h2 {
                                    classes = setOf("mdl-card__title-text")
                                    text("${build.branch} [${build.commitHash}]")
                                }
                            }
                            div {
                                classes = setOf("mdl-card__supporting-text")
                                if (!build.jobName.isNullOrBlank()) {
                                    text(build.jobName)
                                    br()
                                }

                                text(build.triggeredAt)

                                if (build.finishedAt != null) {
                                    text(" - ${build.finishedAt}")
                                }
                            }
                            if (build.status != "in-progress") {
                                div {
                                    classes = setOf("mdl-card__actions mdl-card--border")
                                    a(href = "/test-results/${build.buildSlug}") {
                                        classes = setOf("mdl-button mdl-button--colored", "mdl-js-button", "mdl-js-ripple-effect")
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
}
