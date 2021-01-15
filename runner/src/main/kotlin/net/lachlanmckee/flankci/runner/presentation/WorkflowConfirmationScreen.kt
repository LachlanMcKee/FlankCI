package net.lachlanmckee.flankci.runner.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.core.presentation.*
import net.lachlanmckee.flankci.runner.domain.entity.FlankDataModel
import java.util.*

internal class WorkflowConfirmationScreen {
  suspend fun respondHtml(
    call: ApplicationCall,
    configurationId: ConfigurationId,
    flankDataModel: FlankDataModel,
    jobName: String,
    yaml: String
  ) {
    call.respondHtml {
      materialHeader()
      materialBody(
        title = "Confirm Test Details",
        linksFunc = { mode: MaterialLinkMode ->
          if (mode == MaterialLinkMode.DRAWER) {
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
            materialStandardLink(
              text = "Test Results",
              href = "/$configurationId/test-results",
              icon = "poll",
              newWindow = false
            )
          }
          materialJavascriptLink(
            text = "Trigger",
            onClick = "document.forms[0].submit()",
            icon = "done"
          )
        },
        contentFunc = { content(flankDataModel, jobName, yaml) }
      )
    }
  }

  private fun HtmlBlockTag.content(flankDataModel: FlankDataModel, jobName: String, yaml: String) {
    h4 { +"Branch" }
    p { +flankDataModel.branch }
    h4 { +"Job Name" }
    p { +jobName }
    h4 { +"YAML" }
    p {
      yaml
        .split("\n")
        .forEach { body ->
          text(body)
          br()
        }
    }
    form("/confirm-test-trigger", encType = FormEncType.multipartFormData, method = FormMethod.post) {
      hiddenInput {
        id = "branch"
        name = "branch"
        value = flankDataModel.branch
      }
      hiddenInput {
        id = "build-slug"
        name = "build-slug"
        value = flankDataModel.buildSlug
      }
      hiddenInput {
        id = "commit-hash"
        name = "commit-hash"
        value = flankDataModel.commitHash
      }
      hiddenInput {
        id = "job-name"
        name = "job-name"
        value = jobName
      }
      hiddenInput {
        id = "yaml-base64"
        name = "yaml-base64"
        value = Base64.getEncoder().encodeToString(yaml.toByteArray())
      }
    }
  }
}
