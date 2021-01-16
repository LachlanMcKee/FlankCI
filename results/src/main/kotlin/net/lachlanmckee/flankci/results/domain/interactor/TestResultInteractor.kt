package net.lachlanmckee.flankci.results.domain.interactor

import kotlinx.coroutines.*
import net.lachlanmckee.flankci.core.awaitGetOrThrow
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.results.domain.entity.TestResultDetailModel
import net.lachlanmckee.flankci.results.domain.entity.TestResultDetailModel.WithResults.TestResultType
import net.lachlanmckee.flankci.results.domain.entity.TestResultDetailModel.WithResults.TestSuiteModel
import net.lachlanmckee.flankci.results.domain.mapper.FirebaseUrlMapper
import net.lachlanmckee.flankci.results.domain.mapper.TestSuiteModelMapper
import javax.inject.Inject

internal class TestResultInteractor @Inject constructor(
  private val ciDataSource: CIDataSource,
  private val testSuiteModelMapper: TestSuiteModelMapper,
  private val firebaseUrlMapper: FirebaseUrlMapper
) {
  suspend fun execute(configurationId: ConfigurationId, buildSlug: String): Result<TestResultDetailModel> = kotlin.runCatching {
    createTestResultModel(configurationId, buildSlug)
  }

  private suspend fun createTestResultModel(
    configurationId: ConfigurationId,
    buildSlug: String
  ): TestResultDetailModel {
    val yamlResponse = getArtifactTextAsync(configurationId, buildSlug, "flank.yml")
    val costResponse = getArtifactTextAsync(configurationId, buildSlug, "CostReport.txt")
    val testSuiteModelListResponse = getTestSuiteModelListAsync(configurationId, buildSlug)
    val firebaseUrlResponse = getFirebaseUrlAsync(configurationId, buildSlug)

    val yamlResult = yamlResponse.await()
    val costResult = costResponse.await()
    val testSuiteModelListResult = testSuiteModelListResponse.await()
    val firebaseUrl = firebaseUrlResponse.awaitGetOrThrow()
    val ciUrl = "https://app.bitrise.io/build/$buildSlug"

    return if (testSuiteModelListResult.isSuccess) {
      val testSuiteModelList = testSuiteModelListResult.getOrThrow()

      TestResultDetailModel.WithResults(
        buildSlug = buildSlug,
        ciUrl = ciUrl,
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
        ciUrl = ciUrl,
        firebaseUrl = firebaseUrl
      )
    }
  }

  private fun getArtifactTextAsync(
    configurationId: ConfigurationId,
    buildSlug: String,
    fileName: String
  ): Deferred<Result<String>> {
    return GlobalScope.async {
      ciDataSource
        .getArtifactDetails(configurationId, buildSlug)
        .mapCatching { artifactDetails ->
          ciDataSource
            .getArtifactText(configurationId, artifactDetails, buildSlug, fileName)
            .getOrThrow()
        }
    }
  }

  private fun getTestSuiteModelListAsync(
    configurationId: ConfigurationId,
    buildSlug: String
  ): Deferred<Result<List<TestSuiteModel>>> {
    return GlobalScope.async {
      ciDataSource.getTestResults(configurationId, buildSlug)
        .mapCatching(testSuiteModelMapper::mapToTestSuiteModelList)
    }
  }

  private fun getFirebaseUrlAsync(
    configurationId: ConfigurationId,
    buildSlug: String
  ): Deferred<Result<String>> {
    return GlobalScope.async {
      ciDataSource.getBuildLog(configurationId, buildSlug)
        .mapCatching(firebaseUrlMapper::mapBuildLogToFirebaseUrl)
    }
  }
}
