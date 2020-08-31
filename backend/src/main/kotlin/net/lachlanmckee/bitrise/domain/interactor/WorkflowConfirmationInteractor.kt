package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import net.lachlanmckee.bitrise.domain.entity.FlankDataModel
import net.lachlanmckee.bitrise.domain.entity.GeneratedFlankConfig
import net.lachlanmckee.bitrise.domain.ktor.MultipartCallFactory
import net.lachlanmckee.bitrise.domain.mapper.FlankConfigMapper
import net.lachlanmckee.bitrise.domain.mapper.FlankDataMapper
import net.lachlanmckee.bitrise.domain.validation.GeneratedFlankConfigValidator
import net.lachlanmckee.bitrise.presentation.runner.WorkflowConfirmationScreen
import net.lachlanmckee.bitrise.presentation.ErrorScreenFactory

class WorkflowConfirmationInteractor(
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
            branch = flankDataModel.branch,
            commitHash = flankDataModel.commitHash,
            jobName = generatedConfig.jobName,
            yaml = generatedConfig.contentAsString
        )
    }
}
