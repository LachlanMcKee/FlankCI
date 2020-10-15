package net.lachlanmckee.bitrise.results.presentation

import io.ktor.application.*
import io.ktor.html.*
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
          testResultModelList.forEach { build -> this@body.testResult(build) }
        }
      }
    }
  }

  private fun BODY.testResult(build: TestResultModel) {
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
      div {
        if (build.status != "in-progress") {
          classes = setOf("mdl-card__actions mdl-card--border")
          this@testResult.button("Test Results", "/test-results/${build.buildSlug}")
          text(" ")
        }
        this@testResult.button("Bitrise", build.bitriseUrl)
        if (build.status == "error") {
          text(" ")
          this@testResult.button("Rerun Failures", "/test-rerun?build-slug=${build.buildSlug}")
        }
      }
    }
  }

  private fun BODY.button(label: String, url: String) {
    a(href = url) {
      classes = setOf("mdl-button mdl-button--colored", "mdl-js-button", "mdl-js-ripple-effect", "gray-button")
      target = "_blank"
      text(label)
    }
  }
}
