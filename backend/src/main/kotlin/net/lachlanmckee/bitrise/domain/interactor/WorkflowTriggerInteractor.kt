package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respondRedirect
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.domain.entity.ConfirmModel
import net.lachlanmckee.bitrise.domain.ktor.MultipartCallFactory
import net.lachlanmckee.bitrise.domain.mapper.ConfirmDataMapper
import net.lachlanmckee.bitrise.presentation.ErrorScreenFactory

class WorkflowTriggerInteractor(
    private val multipartCallFactory: MultipartCallFactory,
    private val errorScreenFactory: ErrorScreenFactory,
    private val bitriseDataSource: BitriseDataSource,
    private val confirmDataMapper: ConfirmDataMapper
) {
    suspend fun execute(call: ApplicationCall) {
        multipartCallFactory.handleMultipart(call) { multipart ->
            confirmDataMapper
                .mapToConfirmModel(multipart)
                .onSuccess { confirmModel -> triggerWorkflow(call, confirmModel) }
                .onFailure {
                    errorScreenFactory.respondHtml(
                        call = call,
                        title = "Error",
                        body = it.message!!
                    )
                }
        }
    }

    private suspend fun triggerWorkflow(call: ApplicationCall, confirmModel: ConfirmModel) {
        bitriseDataSource
            .triggerWorkflow(
                branch = confirmModel.branch,
                commitHash = confirmModel.commitHash,
                jobName = confirmModel.jobName,
                flankConfigBase64 = confirmModel.flankConfigBase64
            )
            .onSuccess {
                if (it.status == "ok") {
                    call.respondRedirect(it.buildUrl)
                } else {
                    errorScreenFactory.respondHtml(
                        call = call,
                        title = "Error",
                        body = "Bitrise rejected build"
                    )
                }
            }
            .onFailure {
                errorScreenFactory.respondHtml(
                    call = call,
                    title = "Error",
                    body = "Failed to submit build. Message: ${it.message}"
                )
            }
    }
}
