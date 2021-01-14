package net.lachlanmckee.flankci.runner.presentation

import io.ktor.application.*
import kotlinx.html.*
import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.presentation.ErrorScreenFactory
import net.lachlanmckee.flankci.runner.domain.entity.RerunModel
import net.lachlanmckee.flankci.runner.domain.interactor.TestRerunInteractor

internal class TestRerunScreen(
  private val configDataSource: ConfigDataSource,
  private val errorScreenFactory: ErrorScreenFactory,
  private val testRerunInteractor: TestRerunInteractor
) {
  private val delegate by lazy {
    TestRunnerScreenDelegate {
      val options = configDataSource.getConfig().testData.options
      options.rerun ?: options.standard
    }
  }

  suspend fun respondHtml(call: ApplicationCall, buildSlug: String) {
    testRerunInteractor
      .execute(buildSlug)
      .onSuccess {
        delegate.respondHtml(call) {
          addTestRerunOptions(it)
        }
      }
      .onFailure { errorScreenFactory.respondHtml(call, "Failed to parse content", it.message!!) }
  }

  private fun HtmlBlockTag.addTestRerunOptions(rerunModel: RerunModel) {
    input {
      id = "defaultBranch"
      name = "defaultBranch"
      type = InputType.hidden
      value = rerunModel.branch
    }
    input {
      id = "isRerun"
      name = "isRerun"
      type = InputType.hidden
      value = "true"
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

        rerunModel.failedTests.forEachIndexed { index, test ->
          div {
            input {
              placeholder = "full_class$index"
              type = InputType.checkBox
              id = "full_class$index"
              name = "full_class"
              value = test
            }
            label {
              htmlFor = "full_class$index"
              text(test)
            }
          }
        }
      }
    }
  }
}
