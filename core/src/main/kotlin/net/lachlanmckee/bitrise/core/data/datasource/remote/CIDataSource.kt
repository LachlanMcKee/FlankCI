package net.lachlanmckee.bitrise.core.data.datasource.remote

import net.lachlanmckee.bitrise.core.data.entity.*

interface CIDataSource {
  suspend fun getBuilds(workflow: String): Result<List<BuildDataResponse>>

  suspend fun getBuildDetails(buildSlug: String): Result<BuildDataResponse>

  suspend fun getBuildLog(buildSlug: String): Result<String>

  suspend fun getArtifactDetails(buildSlug: String): Result<BitriseArtifactsListResponse>

  suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<BitriseArtifactResponse>

  suspend fun getArtifactText(
    artifactDetails: BitriseArtifactsListResponse,
    buildSlug: String,
    fileName: String
  ): Result<String>

  suspend fun triggerWorkflow(triggerData: WorkflowTriggerData): Result<BitriseTriggerResponse>

  suspend fun getTestResults(buildSlug: String): Result<List<TestSuite>>
}
