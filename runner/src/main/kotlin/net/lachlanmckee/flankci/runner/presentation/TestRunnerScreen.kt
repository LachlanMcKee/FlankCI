package net.lachlanmckee.flankci.runner.presentation

import io.ktor.application.*
import kotlinx.html.*
import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId

internal class TestRunnerScreen(private val configDataSource: ConfigDataSource) {
  suspend fun respondHtml(call: ApplicationCall, configurationId: ConfigurationId) {
    val configuration = configDataSource.getConfig().configuration(configurationId)
    TestRunnerScreenDelegate {
      configuration.testData.options.standard
    }.respondHtml(call, configurationId, "${configuration.displayName} Test Runner") {
      addTestRunOptions()
    }
  }

  private fun HtmlBlockTag.addTestRunOptions() {
    div {
      classes = setOf("test-runner-group")
      p {
        classes = setOf("heading")
      }
      p {
        classes = setOf("content")
        button {
          id = "load-test-data-button"
          type = ButtonType.button
          text("Load Test Data")
        }
      }
    }

    p {
      classes = setOf("heading")
      text("Annotations")
    }

    div {
      classes = setOf("test-runner-group", "annotations", "data-list", "content")
      fieldSet {
        id = "annotations-field-set"
      }
    }

    p {
      id = "packages-heading"
      classes = setOf("heading")
      text("Packages")
    }

    div {
      classes = setOf("test-runner-group", "packages", "data-list", "content")
      fieldSet {
        id = "packages-field-set"
      }
    }

    p {
      id = "classes-heading"
      classes = setOf("heading")
      text("Classes")
    }

    div {
      classes = setOf("test-runner-group", "classes", "data-list", "content")
      fieldSet {
        id = "classes-field-set"
      }
    }
  }
}
