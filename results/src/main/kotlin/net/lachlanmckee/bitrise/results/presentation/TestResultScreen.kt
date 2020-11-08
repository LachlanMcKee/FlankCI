package net.lachlanmckee.bitrise.results.presentation

import io.ktor.application.*
import io.ktor.html.*
import kotlinx.html.*
import net.lachlanmckee.bitrise.core.presentation.*
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel.WithResults.*
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

      script(src = "/static/test-result-script.js") {
      }
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
    extraContentFunc: HtmlBlockTag.() -> Unit
  ) {
    call.respondHtml {
      materialHeader()
      materialBody(
        title = "Bitrise Test Result",
        linksFunc = { mode: MaterialLinkMode ->
          if (mode == MaterialLinkMode.DRAWER) {
            materialStandardLink(
              text = "Home",
              href = "/",
              icon = "home",
              newWindow = false
            )
            materialStandardLink(
              text = "Test Results",
              href = "/test-results",
              icon = "poll",
              newWindow = false
            )
            materialStandardLink(
              text = "Test Runner",
              href = "/test-runner",
              icon = "directions_run",
              newWindow = false
            )
          }
          materialStandardLink(
            text = "Bitrise",
            href = resultDetailModel.bitriseUrl,
            icon = "build",
            newWindow = true
          )
          materialStandardLink(
            text = "Firebase",
            href = resultDetailModel.firebaseUrl,
            icon = "local_fire_department",
            newWindow = true
          )
          materialJavascriptLink(
            text = "YAML",
            onClick = "openDialog('Yaml')",
            icon = "text_snippet"
          )
          if (resultDetailModel.totalFailures > 0) {
            materialStandardLink(
              text = "Rerun ${resultDetailModel.totalFailures} failures",
              href = "/test-rerun?build-slug=${resultDetailModel.buildSlug}",
              icon = "replay",
              newWindow = true
            )
          }
        },
        contentFunc = {
          resultDetailModel.yaml?.also { yaml ->
            dialog("dialogYaml", "YAML", yaml.split("\n"))
          }
          extraContentFunc()
        }
      )
    }
  }

  private fun HtmlBlockTag.testSuiteElement(testSuite: TestSuiteModel) {
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
          if (testSuite.resultType != TestResultType.SKIPPED) {
            th {
              classes = setOf("mdl-data-table__cell--non-numeric")
              text("Links")
            }
          }
          th {
            classes = setOf("mdl-data-table__cell--non-numeric")
            text("Test")
          }
        }
      }
      tbody {
        testSuite.testCases.forEachIndexed { index, testCase ->
          this@table.testCaseElement(
            testSuite,
            testCase,
            index
          )
        }
      }
    }
  }

  private fun TABLE.testCaseElement(testSuite: TestSuiteModel, testCase: TestModel, index: Int) {
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
      if (testSuite.resultType != TestResultType.SKIPPED) {
        td {
          if (testCase.webLink != null) {
            linkButton("Firebase Report", testCase.webLink, gray = false)

            if (testCase.failure != null) {
              br()
            }
          }
          if (testCase.failure != null) {
            failureDialog(testCase.failure, index)
          }
        }
      }
      td {
        text(testCase.path)
      }
    }
  }

  private fun HtmlBlockTag.failureDialog(failure: String, index: Int) {
    jsButton("Failure Reason", "openDialog($index)", gray = false)
    dialog("dialog$index", "Test Failure", failure.split("\n"))
  }

  private fun HtmlBlockTag.dialog(id: String, title: String, text: List<String>) {
    dialog {
      this.id = id
      classes = setOf("mdl-dialog", "failure-dialog")

      h4 {
        classes = setOf("mdl-dialog__title", "failure-dialog-title")
        text(title)
      }

      div {
        classes = setOf("mdl-dialog__content", "failure-dialog-content")
        p {
          text
            .forEach { line ->
              text(line)
              br()
            }
        }
      }

      div {
        classes = setOf("mdl-dialog__actions")
        button {
          classes = setOf("mdl-button", "close")
          type = ButtonType.button
          text("Close")
        }
      }
    }
  }
}
