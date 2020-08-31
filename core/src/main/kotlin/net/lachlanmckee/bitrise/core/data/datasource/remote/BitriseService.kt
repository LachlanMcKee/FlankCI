package net.lachlanmckee.bitrise.core.data.datasource.remote

import gsonpath.GsonResultList
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.lachlanmckee.bitrise.core.data.api.withTempFile
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.entity.*
import java.io.File

interface BitriseService {
    suspend fun getBuilds(workflow: String): Result<GsonResultList<BuildsResponse.BuildData>>

    suspend fun getArtifactDetails(buildSlug: String): Result<BitriseArtifactsListResponse>

    suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<BitriseArtifactResponse>

    suspend fun getArtifactText(url: String): Result<String>

    suspend fun <T> getUsingTempFile(url: String, callback: suspend (file: File) -> T): T

    suspend fun triggerWorkflow(
        branch: String,
        commitHash: String,
        jobName: String,
        workflowId: String,
        flankConfigBase64: String
    ): Result<BitriseTriggerResponse>
}

internal class BitriseServiceImpl(
    private val client: HttpClient,
    private val configDataSource: ConfigDataSource
) : BitriseService {

    private suspend fun createAppUrl(): String {
        return "https://api.bitrise.io/v0.1/apps/${configDataSource.getConfig().bitrise.appId}"
    }

    override suspend fun getBuilds(workflow: String): Result<GsonResultList<BuildsResponse.BuildData>> =
        kotlin.runCatching {
            client
                .get<BuildsResponse>("${createAppUrl()}/builds?workflow=$workflow&sort_by=created_at") {
                    auth()
                }
                .data
        }

    override suspend fun getArtifactDetails(buildSlug: String): Result<BitriseArtifactsListResponse> =
        kotlin.runCatching {
            client
                .get<BitriseArtifactsListResponse>("${createAppUrl()}/builds/$buildSlug/artifacts") {
                    auth()
                }
        }

    override suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<BitriseArtifactResponse> =
        kotlin.runCatching {
            client
                .get<BitriseArtifactResponse>("${createAppUrl()}/builds/$buildSlug/artifacts/$artifactSlug") {
                    auth()
                }
        }

    override suspend fun <T> getUsingTempFile(url: String, callback: suspend (file: File) -> T): T {
        var startTime = System.currentTimeMillis()
        println("Download started")
        return client.withTempFile(url) {
            println("Download finished. Time: ${System.currentTimeMillis() - startTime}")

            startTime = System.currentTimeMillis()
            callback(it).also {
                println("Parsing finished. Time: ${System.currentTimeMillis() - startTime}")
            }
        }
    }

    override suspend fun getArtifactText(url: String): Result<String> =
        kotlin.runCatching {
            client.get<String>(url)
        }

    override suspend fun triggerWorkflow(
        branch: String,
        commitHash: String,
        jobName: String,
        workflowId: String,
        flankConfigBase64: String
    ): Result<BitriseTriggerResponse> = kotlin.runCatching {
        client.post<BitriseTriggerResponse> {
            url("${createAppUrl()}/builds")
            contentType(ContentType.Application.Json)
            auth()
            body = BitriseTriggerRequest(
                buildParams = BitriseTriggerRequest.BuildParams(
                    environments = listOf(
                        BitriseTriggerRequest.BuildParams.EnvironmentValue(
                            mappedTo = "FLANK_CONFIG",
                            value = flankConfigBase64
                        ),
                        BitriseTriggerRequest.BuildParams.EnvironmentValue(
                            mappedTo = "JOB_NAME",
                            value = jobName
                        )
                    ),
                    branch = branch,
                    commitHash = commitHash,
                    workflowId = workflowId
                )
            )
        }
    }

    private suspend fun HttpRequestBuilder.auth() {
        header("Authorization", configDataSource.getConfig().bitriseToken)
    }
}
