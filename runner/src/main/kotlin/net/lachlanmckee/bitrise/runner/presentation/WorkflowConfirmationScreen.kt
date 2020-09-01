package net.lachlanmckee.bitrise.runner.presentation

import io.ktor.application.ApplicationCall
import io.ktor.html.respondHtml
import kotlinx.html.*
import net.lachlanmckee.bitrise.runner.domain.entity.FlankDataModel
import java.util.*

internal class WorkflowConfirmationScreen {
    suspend fun respondHtml(
        call: ApplicationCall,
        flankDataModel: FlankDataModel,
        jobName: String,
        yaml: String
    ) {
        call.respondHtml {
            head {
                link(rel = "stylesheet", href = "/static/styles.css", type = "text/css")
            }
            body {
                h1 { +"Confirm Test Details" }
                h3 { +"Branch" }
                p { +flankDataModel.branch }
                h3 { +"Job Name" }
                p { +jobName }
                h3 { +"YAML" }
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
                    submitInput { value = "Trigger" }
                }
            }
        }
    }
}
