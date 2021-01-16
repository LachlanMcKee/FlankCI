package net.lachlanmckee.flankci.results.presentation

import io.ktor.application.*
import io.ktor.html.*
import kotlinx.html.*
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.core.presentation.*
import net.lachlanmckee.flankci.results.domain.entity.TestResultListModel
import net.lachlanmckee.flankci.results.domain.entity.TestResultModel
import net.lachlanmckee.flankci.results.domain.interactor.TestResultsListInteractor

internal class TestResultsListScreen(
  private val testResultsListInteractor: TestResultsListInteractor,
  private val errorScreenFactory: ErrorScreenFactory
) {
  suspend fun respondHtml(call: ApplicationCall, configurationId: ConfigurationId) {
    testResultsListInteractor
      .execute(configurationId)
      .onSuccess { render(call, it) }
      .onFailure { errorScreenFactory.respondHtml(call, "Failed to parse content", it.message!!) }
  }

  private suspend fun render(
    call: ApplicationCall,
    testResultListModel: TestResultListModel
  ) {
    val configurationId = testResultListModel.configurationId
    call.respondHtml {
      materialHeader()
      materialBody(
        title = "${testResultListModel.configurationDisplayName} Test Results",
        linksFunc = {
          materialStandardLink(
            text = "Home",
            href = "/",
            icon = "home",
            newWindow = false
          )
          materialStandardLink(
            text = "Test Runner",
            href = "/$configurationId/test-runner",
            icon = "directions_run",
            newWindow = false
          )
        },
        contentFunc = {
          testResultListModel.results.forEach { build -> testResult(build, configurationId) }
        }
      )
    }
  }

  private fun HtmlBlockTag.testResult(build: TestResultModel, configurationId: ConfigurationId) {
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
          linkButton("Test Results", "/$configurationId/test-results/${build.buildSlug}")
        }
        linkButton("CI", build.ciUrl)
        if (build.status == "error") {
          linkButton("Rerun Failures", "/$configurationId/test-rerun?build-slug=${build.buildSlug}")
        }
      }
    }
  }
}
