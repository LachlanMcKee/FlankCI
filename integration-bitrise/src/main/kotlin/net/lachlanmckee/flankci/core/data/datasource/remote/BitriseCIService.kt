package net.lachlanmckee.flankci.core.data.datasource.remote

import com.google.gson.Gson
import gsonpath.GsonResult
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.*
import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.data.entity.*
import net.lachlanmckee.flankci.core.data.entity.BuildType.*
import net.lachlanmckee.flankci.core.data.entity.bitrise.*
import net.lachlanmckee.flankci.core.data.entity.generic.*

internal class BitriseCIService(
  private val client: HttpClient,
  private val gson: Gson,
  private val configDataSource: ConfigDataSource
) : CIService {

  private suspend fun getBitriseConfigModel(): BitriseConfigModel {
    return gson.fromJson(configDataSource.getConfig().ci, BitriseConfigModel::class.java)
  }

  private suspend fun createAppUrl(): String {
    return "https://api.bitrise.io/v0.1/apps/${getBitriseConfigModel().appId}"
  }

  override suspend fun getBuilds(buildType: BuildType): Result<List<BuildDataResponse>> =
    kotlin.runCatching {
      val workflowId = when (buildType) {
        APK_SOURCE -> getBitriseConfigModel().testApkSourceWorkflow
        TEST_TRIGGER -> getBitriseConfigModel().testTriggerWorkflow
      }
      client
        .get<BitriseMultipleBuildsResponse>("${createAppUrl()}/builds?workflow=$workflowId&sort_by=created_at") {
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

  override suspend fun getArtifactText(url: String): Result<String> =
    kotlin.runCatching {
      client.get<String>(url)
    }

  override suspend fun triggerWorkflow(triggerData: WorkflowTriggerData): Result<TriggerResponse> = kotlin.runCatching {
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
            workflowId = getBitriseConfigModel().testTriggerWorkflow
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
