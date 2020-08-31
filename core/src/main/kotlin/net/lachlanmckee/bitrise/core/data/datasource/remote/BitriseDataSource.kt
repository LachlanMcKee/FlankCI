package net.lachlanmckee.bitrise.core.data.datasource.remote

import gsonpath.GsonResult
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.entity.BitriseArtifactResponse
import net.lachlanmckee.bitrise.core.data.entity.BitriseArtifactsListResponse
import net.lachlanmckee.bitrise.core.data.entity.BitriseTriggerResponse
import net.lachlanmckee.bitrise.core.data.entity.BuildsResponse

interface BitriseDataSource {
    suspend fun getBuilds(workflow: String): Result<List<BuildsResponse.BuildData>>

    suspend fun getArtifactDetails(buildSlug: String): Result<BitriseArtifactsListResponse>

    suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<BitriseArtifactResponse>

    suspend fun getArtifactText(url: String): Result<String>

    suspend fun triggerWorkflow(
        branch: String,
        commitHash: String,
        jobName: String,
        flankConfigBase64: String
    ): Result<BitriseTriggerResponse>
}

internal class BitriseDataSourceImpl(
    private val bitriseService: BitriseService,
    private val configDataSource: ConfigDataSource
) : BitriseDataSource {

    override suspend fun getBuilds(workflow: String): Result<List<BuildsResponse.BuildData>> {
        return bitriseService.getBuilds(workflow)
            .map { buildsList ->
                buildsList.flatMap {
                    when (it) {
                        is GsonResult.Success -> listOf(it.value)
                        is GsonResult.Failure -> {
                            println("Filtering out build due to: ${it.exception}")
                            emptyList()
                        }
                    }
                }
            }
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
