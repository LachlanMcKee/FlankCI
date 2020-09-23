package net.lachlanmckee.bitrise.core.data.datasource.remote

import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.entity.*
import net.lachlanmckee.bitrise.core.data.mapper.TestSuitesMapper
import javax.inject.Inject

internal class BitriseDataSourceImpl @Inject constructor(
    private val bitriseService: BitriseService,
    private val configDataSource: ConfigDataSource,
    private val testSuitesMapper: TestSuitesMapper
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

    override suspend fun getArtifactText(
        artifactDetails: BitriseArtifactsListResponse,
        buildSlug: String,
        fileName: String
    ): Result<String> = runCatching {
        val artifactDetail = artifactDetails
            .data
            .firstOrNull { it.title == fileName }
            ?: throw IllegalStateException("Unable to find artifact with file name: $fileName")

        val artifact = getArtifact(buildSlug, artifactDetail.slug)
            .getOrThrow()

        bitriseService.getArtifactText(artifact.expiringDownloadUrl)
            .getOrThrow()
    }

    override suspend fun triggerWorkflow(triggerData: WorkflowTriggerData): Result<BitriseTriggerResponse> {
        val flankWorkflowId = configDataSource.getConfig().bitrise.testTriggerWorkflow
        return bitriseService.triggerWorkflow(
            triggerData = triggerData,
            workflowId = flankWorkflowId
        )
    }

    override suspend fun getTestResults(buildSlug: String): Result<TestSuites> {
        return getArtifactDetails(buildSlug)
            .map { artifactDetails ->
                getArtifactText(artifactDetails, buildSlug, "JUnitReport.xml")
                    .getOrThrow()
            }
            .mapCatching(testSuitesMapper::mapTestSuites)
    }
}
