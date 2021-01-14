package net.lachlanmckee.flankci.core.data.datasource.remote

import gsonpath.GsonResult
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import net.lachlanmckee.flankci.core.data.api.withTempFile
import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.data.entity.*
import net.lachlanmckee.flankci.core.data.entity.bitrise.*
import net.lachlanmckee.flankci.core.data.entity.generic.*
import java.io.File

internal class BitriseCIService(
  private val client: HttpClient,
  private val configDataSource: ConfigDataSource
) : CIService {

  private suspend fun createAppUrl(): String {
    return "https://api.bitrise.io/v0.1/apps/${configDataSource.getConfig().bitrise.appId}"
  }

  override suspend fun getBuilds(workflow: String): Result<List<BuildDataResponse>> =
    kotlin.runCatching {
      client
        .get<BitriseMultipleBuildsResponse>("${createAppUrl()}/builds?workflow=$workflow&sort_by=created_at") {
          auth()
        }
        .data
        .flatMap {
          when (it) {
            is GsonResult.Success -> listOf(mapBitriseBuildDataResponse(it.value))
            is GsonResult.Failure -> {
              println("Filtering out build due to: ${it.exception}")
              emptyList()
            }
          }
        }
    }

  override suspend fun getBuildDetails(buildSlug: String): Result<BuildDataResponse> =
    kotlin.runCatching {
      client
        .get<BitriseSingleBuildResponse>("${createAppUrl()}/builds/$buildSlug") {
          auth()
        }
        .data
        .let(::mapBitriseBuildDataResponse)
    }

  private fun mapBitriseBuildDataResponse(response: BitriseBuildDataResponse): BuildDataResponse {
    return BuildDataResponse(
      branch = response.branch,
      statusText = response.statusText,
      commitHash = response.commitHash,
      commitMessage = response.commitMessage,
      buildNumber = response.buildNumber,
      slug = response.slug,
      triggeredAt = response.triggeredAt,
      finishedAt = response.finishedAt,
      originalEnvironmentValueList = response.originalEnvironmentValueList?.map { environment ->
        EnvironmentValueResponse(environment.key, environment.value)
      }
    )
  }

  override suspend fun getBuildLog(buildSlug: String): Result<BuildLogResponse> =
    kotlin.runCatching {
      client
        .get<BitriseBuildLogResponse>("${createAppUrl()}/builds/$buildSlug/log") {
          auth()
        }
        .let { BuildLogResponse(it.expiringRawLogUrl) }
    }

  override suspend fun getArtifactDetails(buildSlug: String): Result<ArtifactsListResponse> =
    kotlin.runCatching {
      client
        .get<BitriseArtifactsListResponse>("${createAppUrl()}/builds/$buildSlug/artifacts") {
          auth()
        }
        .let {
          ArtifactsListResponse(
            it.data.map { bitriseContent ->
              ArtifactsListResponse.ArtifactContent(
                title = bitriseContent.title,
                slug = bitriseContent.slug,
                artifactMeta = bitriseContent.artifactMeta?.let { bitriseMeta ->
                  ArtifactsListResponse.ArtifactContent.Meta(bitriseMeta.buildType)
                }
              )
            }
          )
        }
    }

  override suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<ArtifactResponse> =
    kotlin.runCatching {
      client
        .get<BitriseArtifactResponse>("${createAppUrl()}/builds/$buildSlug/artifacts/$artifactSlug") {
          auth()
        }
        .let { ArtifactResponse(it.expiringDownloadUrl) }
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
    triggerData: WorkflowTriggerData,
    workflowId: String
  ): Result<TriggerResponse> = kotlin.runCatching {
    client
      .post<BitriseTriggerResponse> {
        url("${createAppUrl()}/builds")
        contentType(ContentType.Application.Json)
        auth()
        val bitriseTriggerRequest = BitriseTriggerRequest(
          buildParams = BitriseTriggerRequest.BuildParams(
            environments = listOf(
              BitriseTriggerRequest.BuildParams.EnvironmentValue(
                mappedTo = "FLANK_CONFIG",
                value = triggerData.flankConfigBase64
              ),
              BitriseTriggerRequest.BuildParams.EnvironmentValue(
                mappedTo = "JOB_NAME",
                value = triggerData.jobName
              ),
              BitriseTriggerRequest.BuildParams.EnvironmentValue(
                mappedTo = "JOB_BUILD_SLUG",
                value = triggerData.buildSlug
              )
            ),
            branch = triggerData.branch,
            commitHash = triggerData.commitHash,
            workflowId = workflowId
          )
        )
        println("Triggering workflow with content: $bitriseTriggerRequest")
        body = bitriseTriggerRequest
      }
      .let {
        TriggerResponse(it.status, it.buildUrl)
      }
  }

  private suspend fun HttpRequestBuilder.auth() {
    header("Authorization", configDataSource.getConfig().bitriseToken)
  }
}
