package net.lachlanmckee.bitrise.core.data.datasource.remote

import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.entity.BitriseArtifactResponse
import net.lachlanmckee.bitrise.core.data.entity.BitriseArtifactsListResponse
import net.lachlanmckee.bitrise.core.data.entity.BitriseTriggerResponse
import net.lachlanmckee.bitrise.core.data.entity.BuildsResponse
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

    override suspend fun triggerWorkflow(
        branch: String,
        commitHash: String,
        jobName: String,
        flankConfigBase64: String
    ): Result<BitriseTriggerResponse> {
        val flankWorkflowId = configDataSource.getConfig().bitrise.testTriggerWorkflow
        return bitriseService.triggerWorkflow(branch, commitHash, jobName, flankWorkflowId, flankConfigBase64)
    }
}
