package net.lachlanmckee.bitrise.core.data.datasource.remote

import net.lachlanmckee.bitrise.core.data.entity.*

interface BitriseDataSource {
    suspend fun getBuilds(workflow: String): Result<List<BuildsResponse.BuildData>>

    suspend fun getArtifactDetails(buildSlug: String): Result<BitriseArtifactsListResponse>

    suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<BitriseArtifactResponse>

    suspend fun getArtifactText(url: String): Result<String>

    suspend fun triggerWorkflow(triggerData: WorkflowTriggerData): Result<BitriseTriggerResponse>
}
