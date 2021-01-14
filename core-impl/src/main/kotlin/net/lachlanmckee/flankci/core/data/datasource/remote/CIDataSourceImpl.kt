package net.lachlanmckee.flankci.core.data.datasource.remote

import net.lachlanmckee.flankci.core.data.entity.BuildType
import net.lachlanmckee.flankci.core.data.entity.WorkflowTriggerData
import net.lachlanmckee.flankci.core.data.entity.generic.*
import net.lachlanmckee.flankci.core.data.entity.junit.TestSuite
import net.lachlanmckee.flankci.core.data.mapper.TestSuitesMapper
import javax.inject.Inject

internal class CIDataSourceImpl @Inject constructor(
  private val ciService: CIService,
  private val testSuitesMapper: TestSuitesMapper
) : CIDataSource {

  override suspend fun getBuilds(buildType: BuildType): Result<List<BuildDataResponse>> {
    return ciService.getBuilds(buildType)
  }

  override suspend fun getBuildDetails(buildSlug: String): Result<BuildDataResponse> {
    return ciService.getBuildDetails(buildSlug)
  }

  override suspend fun getBuildLog(buildSlug: String): Result<String> {
    return ciService.getBuildLog(buildSlug)
      .mapCatching { ciService.getArtifactText(it.expiringRawLogUrl).getOrThrow() }
  }

  override suspend fun getArtifactDetails(buildSlug: String): Result<ArtifactsListResponse> {
    return ciService.getArtifactDetails(buildSlug)
  }

  override suspend fun getArtifact(buildSlug: String, artifactSlug: String): Result<ArtifactResponse> {
    return ciService.getArtifact(buildSlug, artifactSlug)
  }

  override suspend fun getArtifactText(
    artifactDetails: ArtifactsListResponse,
    buildSlug: String,
    fileName: String
  ): Result<String> = runCatching {
    val artifactDetail = artifactDetails
      .data
      .firstOrNull { it.title == fileName }
      ?: throw IllegalStateException("Unable to find artifact with file name: $fileName")

    val artifact = getArtifact(buildSlug, artifactDetail.slug)
      .getOrThrow()

    ciService.getArtifactText(artifact.expiringDownloadUrl)
      .getOrThrow()
  }

  override suspend fun triggerWorkflow(triggerData: WorkflowTriggerData): Result<TriggerResponse> {
    return ciService.triggerWorkflow(triggerData)
  }

  override suspend fun getTestResults(buildSlug: String): Result<List<TestSuite>> {
    return getArtifactDetails(buildSlug)
      .mapCatching { artifactDetails ->
        getArtifactText(artifactDetails, buildSlug, "JUnitReport.xml")
          .getOrThrow()
      }
      .mapCatching { testSuitesMapper.mapTestSuites(it).testsuite }
  }
}
