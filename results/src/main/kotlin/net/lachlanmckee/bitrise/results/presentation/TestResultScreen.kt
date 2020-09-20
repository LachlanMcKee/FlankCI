package net.lachlanmckee.bitrise.results.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
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
                link(rel = "stylesheet", href = "https://fonts.googleapis.com/icon?family=Material+Icons")
                link(rel = "stylesheet", href = "https://code.getmdl.io/1.3.0/material.indigo-pink.min.css")
                script {
                    src = "https://code.getmdl.io/1.3.0/material.min.js"
                }
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +"Bitrise Test Result" }
                div {
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
                    table {
                        classes = setOf("mdl-data-table", "mdl-js-data-table", "mdl-data-table", "mdl-shadow--2dp")
                        thead {
                            tr {
                                th {
                                    classes = setOf("mdl-data-table__cell--non-numeric")
                                    text("Result")
                                }
                                th {
                                    classes = setOf("mdl-data-table__cell--non-numeric")
                                    text("Test")
                                }
                            }
                        }
                        tbody {
                            testSuite.testcase.forEach { testCase ->
                                tr {
                                    classes = when {
                                        testCase.failure != null -> {
                                            setOf("test-failure")
                                        }
                                        testCase.webLink == null -> {
                                            setOf("test-in-progress")
                                        }
                                        else -> {
                                            setOf("test-success")
                                        }
                                    }
                                    td {
                                        if (testCase.failure != null) {
                                            text("Failure")
                                        } else {
                                            text("Success")
                                        }
                                    }
                                    td {
                                        text("${testCase.classname}#${testCase.name}")
                                        br()
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
}
