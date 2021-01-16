package net.lachlanmckee.flankci.runner.presentation

import io.ktor.application.*
import io.ktor.html.*
import kotlinx.html.*
import net.lachlanmckee.flankci.core.data.entity.ConfigModel
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.core.presentation.*

internal class TestRunnerScreenDelegate(private val optionsProviderFunc: suspend () -> List<ConfigModel.Option>) {
  suspend fun respondHtml(
    call: ApplicationCall,
    configurationId: ConfigurationId,
    screenName: String,
    extraContentProvider: HtmlBlockTag.() -> Unit
  ) {
    val options: List<ConfigModel.Option> = optionsProviderFunc()
    call.respondHtml {
      materialHeader()
      materialBody(
        title = screenName,
        linksFunc = {
          materialStandardLink(
            text = "Home",
            href = "/",
            icon = "home",
            newWindow = false
          )
          materialStandardLink(
            text = "Test Results",
            href = "/$configurationId/test-results",
            icon = "poll",
            newWindow = false
          )
        },
        contentFunc = { content(configurationId, options, extraContentProvider) }
      )
    }
  }

  private fun HtmlBlockTag.content(
    configurationId: ConfigurationId,
    options: List<ConfigModel.Option>,
    extraContentProvider: HtmlBlockTag.() -> Unit
  ) {
    form("/$configurationId/trigger-tests", encType = FormEncType.multipartFormData, method = FormMethod.post) {
      acceptCharset = "utf-8"
      target = "_blank"

      div {
        classes = setOf("test-runner-group")
        label {
          classes = setOf("inline-label")
          htmlFor = "branch-select"
          text("Branch: ")
        }
        select {
          id = "branch-select"
          name = "branch-select"
        }
      }

      div {
        classes = setOf("test-runner-group")
        label {
          classes = setOf("inline-label")
          htmlFor = "build-select"
          text("Build: ")
        }
        select {
          id = "build-select"
          name = "build-select"
        }
      }

      div {
        classes = setOf("test-runner-group")
        p {
          classes = setOf("heading")
          text("Artifacts:")
        }
        p {
          classes = setOf("content")
          id = "artifact-details"
        }
      }

      addOptions(options)

      input {
        id = "configurationId"
        name = "configurationId"
        type = InputType.hidden
        value = configurationId.id
      }

      input {
        id = "root-package"
        name = "rootPackage"
        type = InputType.hidden
      }

      input {
        id = "build-slug"
        name = "buildSlug"
        type = InputType.hidden
      }

      input {
        id = "commit-hash"
        name = "commitHash"
        type = InputType.hidden
      }

      extraContentProvider(this)

      div {
        p {
          classes = setOf("test-runner-group", "heading")
        }
        p {
          classes = setOf("content")
          submitInput { value = "Trigger Tests" }
        }
      }
    }
    script(src = "/static/test-runner-script.js") {
    }
  }

  private fun FORM.addOptions(options: List<ConfigModel.Option>) {
    options.forEachIndexed { index, option ->
      when (option) {
        is ConfigModel.Option.Checkbox -> {
          div {
            classes = setOf("test-runner-group")
            label {
              classes = setOf("inline-label")
              htmlFor = "option-checkbox-$index"
              text("${option.label}:")
            }
            hiddenInput {
              id = "option-checkbox-$index-hidden"
              name = "option-checkbox-$index-hidden"
              value = "off"
            }
            checkBoxInput {
              id = "option-checkbox-$index"
              name = "option-checkbox-$index"
            }
          }
        }
        is ConfigModel.Option.DropDown -> {
          div {
            classes = setOf("test-runner-group")
            label {
              classes = setOf("inline-label")
              htmlFor = "option-drop-down-$index"
              text("${option.label}:")
            }
            select {
              id = "option-drop-down-$index"
              name = "option-drop-down-$index"

              option.values.forEachIndexed { index, optionValue ->
                option {
                  value = "$index"
                  text(optionValue.label)
                }
              }
            }
          }
        }
      }
    }
  }
}
