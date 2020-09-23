package net.lachlanmckee.bitrise.results.presentation

import io.ktor.application.*
import io.ktor.html.*
import kotlinx.html.*
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel
import net.lachlanmckee.bitrise.results.domain.interactor.TestResultInteractor

internal class TestResultScreen(
    private val testResultInteractor: TestResultInteractor,
    private val errorScreenFactory: ErrorScreenFactory
) {
    suspend fun respondHtml(call: ApplicationCall, buildSlug: String) {
        testResultInteractor
            .execute(buildSlug)
            .onSuccess { render(call, it) }
            .onFailure { errorScreenFactory.respondHtml(call, "Failed to parse content", it.message!!) }
    }

    private suspend fun render(
        call: ApplicationCall,
        resultDetailModel: TestResultDetailModel
    ) {
        call.respondHtml {
            head {
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +"Bitrise Test Result" }

                val totalFailures = resultDetailModel.testSuites.testsuite
                    .sumBy { it.failures }

                if (totalFailures > 0) {
                    div {
                        span {
                            classes = setOf("heading")
                            text("Total Failures")
                        }
                        span {
                            classes = setOf("content")
                            text("$totalFailures (")
                            a(href = "/test-rerun?build-slug=a&branch=b") {
                                target = "_blank"
                                text("Rerun")
                            }
                            text(")")
                        }
                    }
                }
                div {
                    span {
                        classes = setOf("heading")
                    }
                    span {
                        classes = setOf("content")
                        a(href = resultDetailModel.bitriseUrl) {
                            target = "_blank"
                            text("Bitrise")
                        }
                    }
                }
                br()
                div {
                    span {
                        classes = setOf("heading")
                    }
                    span {
                        classes = setOf("content")
                        b {
                            text(resultDetailModel.cost)
                        }
                    }
                }
                resultDetailModel.testSuites.testsuite.forEach { testSuite ->
                    div {
                        p {
                            classes = setOf("heading")
                        }
                        p {
                            classes = setOf("content")
                            b {
                                text("${testSuite.name}. Success: ${testSuite.tests - testSuite.failures}/${testSuite.tests}, Time: ${testSuite.time}")
                            }
                        }
                    }
                    testSuite.testcase?.forEach { testCase ->
                        div {
                            span {
                                classes = setOf("heading")
                                if (testCase.failure != null) {
                                    text("Failure")
                                } else {
                                    text("Success")
                                }
                            }
                            span {
                                classes = when {
                                    testCase.failure != null -> {
                                        setOf("content", "test-failure")
                                    }
                                    testCase.webLink == null -> {
                                        setOf("content", "test-in-progress")
                                    }
                                    else -> {
                                        setOf("content", "test-success")
                                    }
                                }
                                text("${testCase.classname}#${testCase.name}")
                                br()
                                if (testCase.webLink != null) {
                                    a(href = testCase.webLink) {
                                        target = "_blank"
                                        text("Open in Firebase")
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
