package net.lachlanmckee.bitrise.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.runner.data.datasource.remote.ApkDataSource
import net.lachlanmckee.bitrise.runner.domain.mapper.TestApkMetadataMapper
import javax.inject.Inject

internal class TestApkMetadataInteractor @Inject constructor(
  private val bitriseDataSource: BitriseDataSource,
  private val apkDataSource: ApkDataSource,
  private val testApkMetadataMapper: TestApkMetadataMapper
) {
  suspend fun execute(
    call: ApplicationCall,
    buildSlug: String,
    artifactSlug: String
  ) {
    println("Fetching test apk data for build[$buildSlug] and artifact[$artifactSlug]")

    bitriseDataSource
      .getArtifact(buildSlug, artifactSlug)
      .onSuccess { artifactData -> fetchTestData(call, artifactData.expiringDownloadUrl) }
      .onFailure { println("Failure: $it") }
  }

  private suspend fun fetchTestData(
    call: ApplicationCall,
    testApkUrl: String
  ) {
    apkDataSource
      .getTestApkTestMethods(testApkUrl)
      .mapCatching {
        val startTime = System.currentTimeMillis()
        testApkMetadataMapper.mapTestApkMetadata(it)
          .also {
            println("Mapping finished. Time: ${System.currentTimeMillis() - startTime}")
          }
      }
      .onSuccess { call.respond(it) }
      .onFailure { println("Failure: $it") }
  }
}
