package net.lachlanmckee.flankci.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.runner.data.datasource.remote.ApkDataSource
import net.lachlanmckee.flankci.runner.domain.mapper.TestApkMetadataMapper
import javax.inject.Inject

internal class TestApkMetadataInteractor @Inject constructor(
  private val ciDataSource: CIDataSource,
  private val apkDataSource: ApkDataSource,
  private val testApkMetadataMapper: TestApkMetadataMapper
) {
  suspend fun execute(
    call: ApplicationCall,
    configurationId: ConfigurationId,
    buildSlug: String,
    artifactSlug: String
  ) {
    println("Fetching test apk data for build[$buildSlug] and artifact[$artifactSlug]")

    ciDataSource
      .getArtifact(configurationId, buildSlug, artifactSlug)
      .onSuccess { artifactData -> fetchTestData(call, configurationId, artifactData.expiringDownloadUrl) }
      .onFailure { println("Failure: $it") }
  }

  private suspend fun fetchTestData(
    call: ApplicationCall,
    configurationId: ConfigurationId,
    testApkUrl: String
  ) {
    apkDataSource
      .getTestApkTestMethods(testApkUrl)
      .mapCatching {
        val startTime = System.currentTimeMillis()
        testApkMetadataMapper.mapTestApkMetadata(configurationId, it)
          .also {
            println("Mapping finished. Time: ${System.currentTimeMillis() - startTime}")
          }
      }
      .onSuccess { call.respond(it) }
      .onFailure { println("Failure: $it") }
  }
}
