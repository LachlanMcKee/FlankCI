package net.lachlanmckee.bitrise.results.domain.interactor

import kotlinx.coroutines.*
import net.lachlanmckee.bitrise.core.awaitGetOrThrow
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel.WithResults.TestResultType
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel.WithResults.TestSuiteModel
import net.lachlanmckee.bitrise.results.domain.mapper.FirebaseUrlMapper
import net.lachlanmckee.bitrise.results.domain.mapper.TestSuiteModelMapper
import javax.inject.Inject

internal class TestResultInteractor @Inject constructor(
  private val bitriseDataSource: BitriseDataSource,
  private val testSuiteModelMapper: TestSuiteModelMapper,
  private val firebaseUrlMapper: FirebaseUrlMapper
) {
  suspend fun execute(buildSlug: String): Result<TestResultDetailModel> = kotlin.runCatching {
    createTestResultModel(buildSlug)
  }

  private suspend fun createTestResultModel(
    buildSlug: String
  ): TestResultDetailModel {
    val yamlResponse = getArtifactTextAsync(buildSlug, "flank.yml")
    val costResponse = getArtifactTextAsync(buildSlug, "CostReport.txt")
    val testSuiteModelListResponse = getTestSuiteModelListAsync(buildSlug)
    val firebaseUrlResponse = getFirebaseUrlAsync(buildSlug)

    val yamlResult = yamlResponse.await()
    val costResult = costResponse.await()
    val testSuiteModelListResult = testSuiteModelListResponse.await()
    val firebaseUrl = firebaseUrlResponse.awaitGetOrThrow()
    val bitriseUrl = "https://app.bitrise.io/build/$buildSlug"

    return if (testSuiteModelListResult.isSuccess) {
      val testSuiteModelList = testSuiteModelListResult.getOrThrow()

      TestResultDetailModel.WithResults(
        buildSlug = buildSlug,
        bitriseUrl = bitriseUrl,
        firebaseUrl = firebaseUrl,
        totalFailures = testSuiteModelList.sumBy { suiteModel ->
          if (suiteModel.resultType == TestResultType.FAILURE) {
            suiteModel.totalTests
          } else {
            0
          }
        },
        yaml = yamlResult.getOrNull(),
        cost = costResult.getOrNull(),
        testSuiteModelList = testSuiteModelList
      )
    } else {
      TestResultDetailModel.NoResults(
        buildSlug = buildSlug,
        bitriseUrl = bitriseUrl,
        firebaseUrl = firebaseUrl
      )
    }
  }

  private fun getArtifactTextAsync(
    buildSlug: String,
    fileName: String
  ): Deferred<Result<String>> {
    return GlobalScope.async {
      bitriseDataSource
        .getArtifactDetails(buildSlug)
        .mapCatching { artifactDetails ->
          bitriseDataSource
            .getArtifactText(artifactDetails, buildSlug, fileName)
            .getOrThrow()
        }
    }
  }

  private fun getTestSuiteModelListAsync(
    buildSlug: String
  ): Deferred<Result<List<TestSuiteModel>>> {
    return GlobalScope.async {
      bitriseDataSource.getTestResults(buildSlug)
        .mapCatching(testSuiteModelMapper::mapToTestSuiteModelList)
    }
  }

  private fun getFirebaseUrlAsync(
    buildSlug: String
  ): Deferred<Result<String>> {
    return GlobalScope.async {
      bitriseDataSource.getBuildLog(buildSlug)
        .mapCatching(firebaseUrlMapper::mapBuildLogToFirebaseUrl)
    }
  }
}
