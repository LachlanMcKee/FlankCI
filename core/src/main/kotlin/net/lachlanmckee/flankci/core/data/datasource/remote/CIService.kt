package net.lachlanmckee.flankci.core.data.datasource.remote

import net.lachlanmckee.flankci.core.data.entity.BuildType
import net.lachlanmckee.flankci.core.data.entity.WorkflowTriggerData
import net.lachlanmckee.flankci.core.data.entity.generic.*

interface CIService {
  suspend fun getBuilds(buildType: BuildType): Result<List<BuildDataResponse>>

  suspend fun getBuildDetails(buildSlug: String): Result<BuildDataResponse>

  suspend fun getBuildLog(buildSlug: String): Result<BuildLogResponse>

  suspend fun getArtifactDetails(buildSlug: String): Result<ArtifactsListResponse>

  suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<ArtifactResponse>

  suspend fun getArtifactText(url: String): Result<String>

  suspend fun triggerWorkflow(triggerData: WorkflowTriggerData): Result<TriggerResponse>
}
