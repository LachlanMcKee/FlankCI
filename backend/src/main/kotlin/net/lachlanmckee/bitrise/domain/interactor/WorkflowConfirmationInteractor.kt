package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.domain.entity.FlankDataModel
import net.lachlanmckee.bitrise.domain.entity.GeneratedFlankConfig
import net.lachlanmckee.bitrise.domain.ktor.handleMultipart
import net.lachlanmckee.bitrise.domain.mapper.FlankConfigMapper
import net.lachlanmckee.bitrise.domain.mapper.FlankDataMapper
import net.lachlanmckee.bitrise.presentation.WorkflowConfirmationScreen
import net.lachlanmckee.bitrise.presentation.ErrorScreen

class WorkflowConfirmationInteractor(
    private val configDataSource: ConfigDataSource,
    private val flankDataMapper: FlankDataMapper,
    private val flankConfigMapper: FlankConfigMapper
) {
    suspend fun execute(call: ApplicationCall) {
        call.handleMultipart { multipart ->
            val flankDataModel = flankDataMapper.mapToFlankData(multipart)
            flankConfigMapper
                .mapToFlankYaml(flankDataModel)
                .onSuccess { generatedConfig ->
                    if (!validationErrorHandled(call, generatedConfig)) {
                        triggerWorkflowConfirmation(call, flankDataModel, generatedConfig)
                    }
                }
                .onFailure {
                    ErrorScreen().respondHtml(
                        call = call,
                        title = "Error",
                        body = it.message!!
                    )
                }
        }
    }

    private suspend fun validationErrorHandled(call: ApplicationCall, generatedConfig: GeneratedFlankConfig): Boolean {
        return if (!configDataSource.getConfig().testData.allowTestingWithoutFilters) {
            when {
                ((generatedConfig.contentAsMap["gcloud"] as? Map<String, Any>?)?.get("test-targets") as? List<String>?).isNullOrEmpty() -> {
                    ErrorScreen().respondHtml(
                        call = call,
                        title = "You cannot execute the tests without specifying at least one annotation/package/class filter",
                        body = generatedConfig.contentAsString
                    )
                    true
                }
                ((generatedConfig.contentAsMap["gcloud"] as? Map<String, Any>?)?.get("device") as? List<String>?).isNullOrEmpty() -> {
                    ErrorScreen().respondHtml(
                        call = call,
                        title = "You cannot execute the tests without specifying at least one device",
                        body = generatedConfig.contentAsString
                    )
                    true
                }
                else -> {
                    false
                }
            }
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
            yaml = generatedConfig.contentAsString
        )
    }
}
