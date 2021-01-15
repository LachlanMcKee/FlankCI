package net.lachlanmckee.flankci.core.data.datasource.remote

import net.lachlanmckee.flankci.core.data.entity.BuildType
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.core.data.entity.WorkflowTriggerData
import net.lachlanmckee.flankci.core.data.entity.generic.*
import net.lachlanmckee.flankci.core.data.entity.junit.TestSuite
import net.lachlanmckee.flankci.core.data.mapper.TestSuitesMapper
import javax.inject.Inject

internal class CIDataSourceImpl @Inject constructor(
  private val ciService: CIService,
  private val testSuitesMapper: TestSuitesMapper
) : CIDataSource {

  override suspend fun getBuilds(
    configurationId: ConfigurationId,
    buildType: BuildType
  ): Result<List<BuildDataResponse>> {
    return ciService.getBuilds(configurationId, buildType)
  }

  override suspend fun getBuildDetails(configurationId: ConfigurationId, buildSlug: String): Result<BuildDataResponse> {
    return ciService.getBuildDetails(configurationId, buildSlug)
  }

  override suspend fun getBuildLog(configurationId: ConfigurationId, buildSlug: String): Result<String> {
    return ciService.getBuildLog(configurationId, buildSlug)
      .mapCatching { ciService.getArtifactText(it.expiringRawLogUrl).getOrThrow() }
  }

  override suspend fun getArtifactDetails(
    configurationId: ConfigurationId,
    buildSlug: String
  ): Result<ArtifactsListResponse> {
    return ciService.getArtifactDetails(configurationId, buildSlug)
  }

  override suspend fun getArtifact(
    configurationId: ConfigurationId,
    buildSlug: String,
    artifactSlug: String
  ): Result<ArtifactResponse> {
    return ciService.getArtifact(configurationId, buildSlug, artifactSlug)
  }

  override suspend fun getArtifactText(
    configurationId: ConfigurationId,
    artifactDetails: ArtifactsListResponse,
    buildSlug: String,
    fileName: String
  ): Result<String> = runCatching {
    val artifactDetail = artifactDetails
      .data
      .firstOrNull { it.title == fileName }
      ?: throw IllegalStateException("Unable to find artifact with file name: $fileName")

    val artifact = getArtifact(configurationId, buildSlug, artifactDetail.slug)
      .getOrThrow()

    ciService.getArtifactText(artifact.expiringDownloadUrl)
      .getOrThrow()
  }

  override suspend fun triggerWorkflow(
    configurationId: ConfigurationId,
    triggerData: WorkflowTriggerData
  ): Result<TriggerResponse> {
    return ciService.triggerWorkflow(configurationId, triggerData)
  }

  override suspend fun getTestResults(configurationId: ConfigurationId, buildSlug: String): Result<List<TestSuite>> {
    return getArtifactDetails(configurationId, buildSlug)
      .mapCatching { artifactDetails ->
        getArtifactText(configurationId, artifactDetails, buildSlug, "JUnitReport.xml")
          .getOrThrow()
      }
      .mapCatching { testSuitesMapper.mapTestSuites(it).testsuite }
  }
}
