package net.lachlanmckee.flankci.presentation

import io.ktor.application.*
import io.ktor.html.*
import kotlinx.html.*
import net.lachlanmckee.flankci.core.presentation.linkButton
import net.lachlanmckee.flankci.core.presentation.materialBody
import net.lachlanmckee.flankci.core.presentation.materialHeader

class HomeScreen {
  suspend fun respondHtml(call: ApplicationCall, model: HomeModel) {
    call.respondHtml {
      materialHeader()
      materialBody(
        title = "Flank CI Home",
        linksFunc = null,
        contentFunc = {
          model.configurations.forEach { configuration ->
            configurationLinks(configuration)
          }
        }
      )
    }
  }

  private fun HtmlBlockTag.configurationLinks(configuration: HomeModel.CiConfiguration) {
    val configurationId = configuration.id
    div {
      classes = setOf("job-result-card", "mdl-card", "mdl-shadow--2dp", "job-success")
      div {
        classes = setOf("mdl-card__title")
        h2 {
          classes = setOf("mdl-card__title-text")
          text(configuration.displayName)
        }
      }
      div {
        classes = setOf("mdl-card__actions mdl-card--border")
        linkButton("Test Runner", "/$configurationId/test-runner", target = "_self")
        linkButton("Test Results", "/$configurationId/test-results", target = "_self")
      }
    }
  }
}
