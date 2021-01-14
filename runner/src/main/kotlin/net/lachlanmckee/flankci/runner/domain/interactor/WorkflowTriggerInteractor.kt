package net.lachlanmckee.flankci.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respondRedirect
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.flankci.core.data.entity.WorkflowTriggerData
import net.lachlanmckee.flankci.core.domain.ktor.MultipartCallFactory
import net.lachlanmckee.flankci.core.presentation.ErrorScreenFactory
import net.lachlanmckee.flankci.runner.domain.entity.ConfirmModel
import net.lachlanmckee.flankci.runner.domain.mapper.ConfirmDataMapper
import javax.inject.Inject

internal class WorkflowTriggerInteractor @Inject constructor(
  private val multipartCallFactory: MultipartCallFactory,
  private val errorScreenFactory: ErrorScreenFactory,
  private val ciDataSource: CIDataSource,
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
    ciDataSource
      .triggerWorkflow(
        WorkflowTriggerData(
          branch = confirmModel.branch,
          buildSlug = confirmModel.buildSlug,
          commitHash = confirmModel.commitHash,
          jobName = confirmModel.jobName,
          flankConfigBase64 = confirmModel.flankConfigBase64
        )
      )
      .onSuccess {
        if (it.status == "ok") {
          call.respondRedirect(it.buildUrl)
        } else {
          errorScreenFactory.respondHtml(
            call = call,
            title = "Error",
            body = "CI rejected build"
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
