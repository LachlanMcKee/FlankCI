package net.lachlanmckee.bitrise.results.presentation

import io.ktor.application.*
import io.ktor.html.*
import kotlinx.html.*
import net.lachlanmckee.bitrise.core.presentation.*
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
      materialHeader()
      materialBody(
        title = "Bitrise Test Results",
        linksFunc = {
          materialStandardLink(
            text = "Home",
            href = "/",
            icon = "home",
            newWindow = false
          )
          materialStandardLink(
            text = "Test Runner",
            href = "/test-runner",
            icon = "directions_run",
            newWindow = false
          )
        },
        contentFunc = {
          testResultModelList.forEach { build -> testResult(build) }
        }
      )
    }
  }

  private fun HtmlBlockTag.testResult(build: TestResultModel) {
    div {
      classes = when (build.status) {
        "success" -> setOf("job-result-card", "mdl-card", "mdl-shadow--2dp", "job-success")
        "in-progress" -> setOf("mdl-card", "mdl-shadow--2dp", "job-result-card", "job-in-progress")
        else -> setOf("mdl-card", "mdl-shadow--2dp", "job-result-card", "job-failure")
      }
      div {
        classes = setOf("mdl-card__title")
        h2 {
          classes = setOf("mdl-card__title-text")
          text(build.branch)
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
      div {
        if (build.status != "in-progress") {
          classes = setOf("mdl-card__actions mdl-card--border")
          linkButton("Test Results", "/test-results/${build.buildSlug}")
        }
        linkButton("Bitrise", build.bitriseUrl)
        if (build.status == "error") {
          linkButton("Rerun Failures", "/test-rerun?build-slug=${build.buildSlug}")
        }
      }
    }
  }
}
