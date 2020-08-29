package net.lachlanmckee.bitrise.data.datasource.remote

import com.linkedin.dex.parser.TestMethod
import gsonpath.GsonResult
import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.data.entity.BitriseArtifactResponse
import net.lachlanmckee.bitrise.data.entity.BitriseArtifactsListResponse
import net.lachlanmckee.bitrise.data.entity.BitriseTriggerResponse
import net.lachlanmckee.bitrise.data.entity.BuildsResponse

interface BitriseDataSource {
    suspend fun getBuilds(workflow: String): Result<List<BuildsResponse.BuildData>>

    suspend fun getArtifactDetails(buildSlug: String): Result<BitriseArtifactsListResponse>

    suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<BitriseArtifactResponse>

    suspend fun getTestApkTestMethods(testApkUrl: String): Result<List<TestMethod>>

    suspend fun getArtifactText(url: String): Result<String>

    suspend fun triggerWorkflow(
        branch: String,
        commitHash: String,
        jobName: String,
        flankConfigBase64: String
    ): Result<BitriseTriggerResponse>
}

class BitriseDataSourceImpl(
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

    override suspend fun getTestApkTestMethods(testApkUrl: String): Result<List<TestMethod>> {
        return bitriseService.getTestApkTestMethods(testApkUrl)
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
