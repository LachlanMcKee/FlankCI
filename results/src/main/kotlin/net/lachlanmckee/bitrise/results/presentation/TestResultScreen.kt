package net.lachlanmckee.bitrise.results.presentation

import io.ktor.application.*
import io.ktor.html.*
import kotlinx.html.*
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel
import net.lachlanmckee.bitrise.results.domain.entity.TestResultType
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

        val totalFailures = resultDetailModel.testSuiteModelList
          .sumBy { it.totalTests - it.successfulTestCount }

        if (totalFailures > 0) {
          div {
            span {
              classes = setOf("heading")
              text("Total Failures")
            }
            span {
              classes = setOf("content")
              text("$totalFailures (")
              a(href = "/test-rerun?build-slug=${resultDetailModel.buildSlug}") {
                target = "_blank"
                text("Rerun Failures")
              }
              text(")")
            }
          }
        }
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
            classes = setOf("content")
            b {
              text(resultDetailModel.cost)
            }
          }
        }
        resultDetailModel.testSuiteModelList.forEach { testSuite ->
          div {
            p {
              classes = setOf("heading")
            }
            p {
              classes = setOf("content")
              b {
                text("${testSuite.name}. Tests: ${testSuite.totalTests}. Total Duration: ${testSuite.time}")
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
                  text("Duration")
                }
                th {
                  classes = setOf("mdl-data-table__cell--non-numeric")
                  text("Test")
                }
              }
            }
            tbody {
              testSuite.testCases.forEach { testCase ->
                tr {
                  classes = when (testSuite.resultType) {
                    TestResultType.FAILURE -> {
                      setOf("test-failure")
                    }
                    TestResultType.SKIPPED -> {
                      setOf("test-in-progress")
                    }
                    TestResultType.SUCCESS -> {
                      setOf("test-success")
                    }
                  }
                  td {
                    text(
                      when (testSuite.resultType) {
                        TestResultType.FAILURE -> {
                          "Failure"
                        }
                        TestResultType.SKIPPED -> {
                          "Skipped"
                        }
                        TestResultType.SUCCESS -> {
                          "Success"
                        }
                      }
                    )
                  }
                  td {
                    text(testCase.time)
                  }
                  td {
                    text(testCase.path)
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
  }
}
