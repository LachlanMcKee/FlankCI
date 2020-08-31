package net.lachlanmckee.bitrise.runner.domain.interactor

import io.ktor.application.ApplicationCall
import net.lachlanmckee.bitrise.core.domain.ktor.MultipartCallFactory
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.runner.domain.entity.FlankDataModel
import net.lachlanmckee.bitrise.runner.domain.entity.GeneratedFlankConfig
import net.lachlanmckee.bitrise.runner.domain.mapper.FlankConfigMapper
import net.lachlanmckee.bitrise.runner.domain.mapper.FlankDataMapper
import net.lachlanmckee.bitrise.runner.domain.validation.GeneratedFlankConfigValidator
import net.lachlanmckee.bitrise.runner.presentation.WorkflowConfirmationScreen
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
            branch = flankDataModel.branch,
            commitHash = flankDataModel.commitHash,
            jobName = generatedConfig.jobName,
            yaml = generatedConfig.contentAsString
        )
    }
}
