package net.lachlanmckee.bitrise.core.data.datasource.remote

import net.lachlanmckee.bitrise.core.data.entity.WorkflowTriggerData
import net.lachlanmckee.bitrise.core.data.entity.generic.*
import java.io.File

interface CIService {
  suspend fun getBuilds(workflow: String): Result<List<BuildDataResponse>>

  suspend fun getBuildDetails(buildSlug: String): Result<BuildDataResponse>

  suspend fun getBuildLog(buildSlug: String): Result<BuildLogResponse>

  suspend fun getArtifactDetails(buildSlug: String): Result<ArtifactsListResponse>

  suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<ArtifactResponse>

  suspend fun getArtifactText(url: String): Result<String>

  suspend fun <T> getUsingTempFile(url: String, callback: suspend (file: File) -> T): T

  suspend fun triggerWorkflow(
    triggerData: WorkflowTriggerData,
    workflowId: String
  ): Result<TriggerResponse>
}
