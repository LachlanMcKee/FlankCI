package net.lachlanmckee.bitrise.core.data.datasource.remote

import net.lachlanmckee.bitrise.core.data.entity.WorkflowTriggerData
import net.lachlanmckee.bitrise.core.data.entity.generic.ArtifactResponse
import net.lachlanmckee.bitrise.core.data.entity.generic.ArtifactsListResponse
import net.lachlanmckee.bitrise.core.data.entity.generic.BuildDataResponse
import net.lachlanmckee.bitrise.core.data.entity.generic.TriggerResponse
import net.lachlanmckee.bitrise.core.data.entity.junit.TestSuite

interface CIDataSource {
  suspend fun getBuilds(workflow: String): Result<List<BuildDataResponse>>

  suspend fun getBuildDetails(buildSlug: String): Result<BuildDataResponse>

  suspend fun getBuildLog(buildSlug: String): Result<String>

  suspend fun getArtifactDetails(buildSlug: String): Result<ArtifactsListResponse>

  suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<ArtifactResponse>

  suspend fun getArtifactText(
    artifactDetails: ArtifactsListResponse,
    buildSlug: String,
    fileName: String
  ): Result<String>

  suspend fun triggerWorkflow(triggerData: WorkflowTriggerData): Result<TriggerResponse>

  suspend fun getTestResults(buildSlug: String): Result<List<TestSuite>>
}
