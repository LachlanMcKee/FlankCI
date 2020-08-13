package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respondRedirect
import io.ktor.response.respondTextWriter
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.domain.entity.ConfirmModel
import net.lachlanmckee.bitrise.domain.ktor.handleMultipart
import net.lachlanmckee.bitrise.domain.mapper.ConfirmDataMapper
import net.lachlanmckee.bitrise.presentation.ErrorScreen

class WorkflowTriggerInteractor(
    private val bitriseDataSource: BitriseDataSource,
    private val confirmDataMapper: ConfirmDataMapper
) {
    suspend fun execute(call: ApplicationCall) {
        call.handleMultipart { multipart ->
            confirmDataMapper
                .mapToConfirmModel(multipart)
                .onSuccess { confirmModel -> triggerWorkflow(call, confirmModel) }
                .onFailure {
                    ErrorScreen().respondHtml(
                        call = call,
                        title = "Error",
                        body = it.message!!
                    )
                }
        }
    }

    private suspend fun triggerWorkflow(call: ApplicationCall, confirmModel: ConfirmModel) {
        bitriseDataSource
            .triggerWorkflow(confirmModel.branch, confirmModel.flankConfigBase64)
            .onSuccess {
                if (it.status == "ok") {
                    call.respondRedirect(it.buildUrl)
                } else {
                    call.respondTextWriter {
                        appendln("Bitrise rejected build")
                    }
                }
            }
            .onFailure {
                call.respondTextWriter {
                    appendln("Failed to submit build. Message: ${it.message}")
                }
            }
    }
}
