package net.lachlanmckee.flankci.core.data.datasource.remote

import net.lachlanmckee.flankci.core.data.entity.BuildType
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.core.data.entity.WorkflowTriggerData
import net.lachlanmckee.flankci.core.data.entity.generic.*

interface CIService {
  suspend fun getBuilds(configurationId: ConfigurationId, buildType: BuildType): Result<List<BuildDataResponse>>

  suspend fun getBuildDetails(configurationId: ConfigurationId, buildSlug: String): Result<BuildDataResponse>

  suspend fun getBuildLog(configurationId: ConfigurationId, buildSlug: String): Result<BuildLogResponse>

  suspend fun getArtifactDetails(configurationId: ConfigurationId, buildSlug: String): Result<ArtifactsListResponse>

  suspend fun getArtifact(
    configurationId: ConfigurationId,
    buildSlug: String,
    artifactSlug: String
  ): Result<ArtifactResponse>

  suspend fun getArtifactText(url: String): Result<String>

  suspend fun triggerWorkflow(
    configurationId: ConfigurationId,
    triggerData: WorkflowTriggerData
  ): Result<TriggerResponse>
}
