package net.lachlanmckee.bitrise.results.presentation

import io.ktor.application.*
import io.ktor.html.*
import kotlinx.html.*
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel.WithResults.TestModel
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel.WithResults.TestResultType
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel.WithResults.TestSuiteModel
import net.lachlanmckee.bitrise.results.domain.interactor.TestResultInteractor

internal class TestResultScreen(
  private val testResultInteractor: TestResultInteractor,
  private val errorScreenFactory: ErrorScreenFactory
) {
  suspend fun respondHtml(call: ApplicationCall, buildSlug: String) {
    testResultInteractor
      .execute(buildSlug)
      .onSuccess { testResultModel ->
        when (testResultModel) {
          is TestResultDetailModel.WithResults -> {
            renderWithResults(call, testResultModel)
          }
          is TestResultDetailModel.NoResults -> {
            renderNoResults(call, testResultModel)
          }
        }
      }
      .onFailure { errorScreenFactory.respondHtml(call, "Failed to parse content", it.message!!) }
  }

  private suspend fun renderWithResults(
    call: ApplicationCall,
    resultDetailModel: TestResultDetailModel.WithResults
  ) {
    renderTemplate(call, resultDetailModel) {
      div {
        span {
          classes = setOf("content")
          b {
            if (resultDetailModel.cost != null) {
              text(resultDetailModel.cost)
            } else {
              text("Failed to fetch cost")
            }
          }
        }
      }
      resultDetailModel.testSuiteModelList.forEach { testSuite -> testSuiteElement(testSuite) }
    }
  }

  private suspend fun renderNoResults(
    call: ApplicationCall,
    resultDetailModel: TestResultDetailModel.NoResults
  ) {
    renderTemplate(call, resultDetailModel) {
      div {
        span {
          classes = setOf("content")
          b {
            text("Failed to fetch test data. Perhaps Flank failed?")
          }
        }
      }
    }
  }

  private suspend fun renderTemplate(
    call: ApplicationCall,
    resultDetailModel: TestResultDetailModel,
    extraContentFunc: BODY.() -> Unit
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
          this@body.button("Bitrise", resultDetailModel.bitriseUrl)
          this@body.button("Firebase", resultDetailModel.firebaseUrl)

          if (resultDetailModel.totalFailures > 0) {
            this@body.button(
              "Rerun ${resultDetailModel.totalFailures} failures",
              "/test-rerun?build-slug=${resultDetailModel.buildSlug}"
            )
          }
        }
        br()
        extraContentFunc()
      }
    }
  }

  private fun BODY.testSuiteElement(testSuite: TestSuiteModel) {
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
        testSuite.testCases.forEach { testCase -> this@table.testCaseElement(testSuite, testCase) }
      }
    }
  }

  private fun TABLE.testCaseElement(testSuite: TestSuiteModel, testCase: TestModel) {
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
