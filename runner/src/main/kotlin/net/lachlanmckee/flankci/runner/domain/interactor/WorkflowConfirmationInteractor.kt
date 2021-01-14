package net.lachlanmckee.flankci.runner.domain.interactor

import io.ktor.application.ApplicationCall
import net.lachlanmckee.flankci.core.domain.ktor.MultipartCallFactory
import net.lachlanmckee.flankci.core.presentation.ErrorScreenFactory
import net.lachlanmckee.flankci.runner.domain.entity.FlankDataModel
import net.lachlanmckee.flankci.runner.domain.entity.GeneratedFlankConfig
import net.lachlanmckee.flankci.runner.domain.mapper.FlankConfigMapper
import net.lachlanmckee.flankci.runner.domain.mapper.FlankDataMapper
import net.lachlanmckee.flankci.runner.domain.validation.GeneratedFlankConfigValidator
import net.lachlanmckee.flankci.runner.presentation.WorkflowConfirmationScreen
import javax.inject.Inject

internal class WorkflowConfirmationInteractor @Inject constructor(
  private val multipartCallFactory: MultipartCallFactory,
  private val errorScreenFactory: ErrorScreenFactory,
  private val generatedFlankConfigValidator: GeneratedFlankConfigValidator,
  private val flankDataMapper: FlankDataMapper,
  private val flankConfigMapper: FlankConfigMapper
) {
  suspend fun execute(call: ApplicationCall) {
    multipartCallFactory.handleMultipart(call) { multipart ->
      val flankDataModel = flankDataMapper.mapToFlankData(multipart)
      flankConfigMapper
        .mapToFlankYaml(flankDataModel)
        .onSuccess { generatedConfig ->
          if (!validationErrorHandled(call, generatedConfig)) {
            triggerWorkflowConfirmation(call, flankDataModel, generatedConfig)
          }
        }
        .onFailure {
          errorScreenFactory.respondHtml(
            call = call,
            title = "Error",
            body = it.message!!
          )
        }
    }
  }

  private suspend fun validationErrorHandled(call: ApplicationCall, generatedConfig: GeneratedFlankConfig): Boolean {
    val error = generatedFlankConfigValidator.getValidationErrorMessage(generatedConfig)
    return if (error != null) {
      errorScreenFactory.respondHtml(
        call = call,
        title = error,
        body = generatedConfig.contentAsString
      )
      true
    } else {
      false
    }
  }

  private suspend fun triggerWorkflowConfirmation(
    call: ApplicationCall,
    flankDataModel: FlankDataModel,
    generatedConfig: GeneratedFlankConfig
  ) {
    WorkflowConfirmationScreen().respondHtml(
      call = call,
      flankDataModel = flankDataModel,
      jobName = generatedConfig.jobName,
      yaml = generatedConfig.contentAsString
    )
  }
}