package net.lachlanmckee.bitrise.core.data.datasource.remote

import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.entity.*
import javax.inject.Inject

internal class BitriseDataSourceImpl @Inject constructor(
    private val bitriseService: BitriseService,
    private val configDataSource: ConfigDataSource
) : BitriseDataSource {

    override suspend fun getBuilds(workflow: String): Result<List<BuildsResponse.BuildData>> {
        return bitriseService.getBuilds(workflow)
    }

    override suspend fun getArtifactDetails(buildSlug: String): Result<BitriseArtifactsListResponse> {
        return bitriseService.getArtifactDetails(buildSlug)
    }

    override suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<BitriseArtifactResponse> {
        return bitriseService.getArtifact(buildSlug, artifactSlug)
    }

    override suspend fun getArtifactText(url: String): Result<String> {
        return bitriseService.getArtifactText(url)
    }

    override suspend fun triggerWorkflow(triggerData: WorkflowTriggerData): Result<BitriseTriggerResponse> {
        val flankWorkflowId = configDataSource.getConfig().bitrise.testTriggerWorkflow
        return bitriseService.triggerWorkflow(
            triggerData = triggerData,
            workflowId = flankWorkflowId
        )
    }
}
