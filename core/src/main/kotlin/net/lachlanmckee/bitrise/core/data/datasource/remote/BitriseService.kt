package net.lachlanmckee.bitrise.core.data.datasource.remote

import net.lachlanmckee.bitrise.core.data.entity.*
import java.io.File

interface BitriseService {
    suspend fun getBuilds(workflow: String): Result<List<BuildDataResponse>>

    suspend fun getBuildDetails(buildSlug: String): Result<BuildDataResponse>

    suspend fun getArtifactDetails(buildSlug: String): Result<BitriseArtifactsListResponse>

    suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<BitriseArtifactResponse>

    suspend fun getArtifactText(url: String): Result<String>

    suspend fun <T> getUsingTempFile(url: String, callback: suspend (file: File) -> T): T

    suspend fun triggerWorkflow(
        triggerData: WorkflowTriggerData,
        workflowId: String
    ): Result<BitriseTriggerResponse>
}
