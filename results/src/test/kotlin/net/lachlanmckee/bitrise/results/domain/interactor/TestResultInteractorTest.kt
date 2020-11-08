package net.lachlanmckee.bitrise.results.domain.interactor

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.core.data.entity.BitriseArtifactsListResponse
import net.lachlanmckee.bitrise.core.data.entity.TestSuite
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel.WithResults.TestResultType
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel.WithResults.TestSuiteModel
import net.lachlanmckee.bitrise.results.domain.mapper.FirebaseUrlMapper
import net.lachlanmckee.bitrise.results.domain.mapper.TestSuiteModelMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TestResultInteractorTest {
  private val bitriseDataSource: BitriseDataSource = mockk()
  private val testSuiteModelMapper: TestSuiteModelMapper = mockk()
  private val firebaseUrlMapper: FirebaseUrlMapper = mockk()

  @Test
  fun givenAllResponsesSucceed_whenExecute_thenExpectTestResultDetailModel() = runBlocking {
    val artifactsResponse = BitriseArtifactsListResponse(listOf())
    coEvery { bitriseDataSource.getArtifactDetails("buildSlug") } returns Result.success(artifactsResponse)
    mockArtifactText(artifactsResponse, "flank.yml", "flank")
    mockArtifactText(artifactsResponse, "CostReport.txt", "cost")
    coEvery { bitriseDataSource.getBuildLog("buildSlug") } returns Result.success("buildLog")

    val testResults = listOf<TestSuite>()
    val testSuiteModelList = listOf(
      TestSuiteModel(
        name = "suite",
        totalTests = 1,
        time = "5.00",
        resultType = TestResultType.FAILURE,
        testCases = listOf(mockk())
      )
    )
    coEvery { bitriseDataSource.getTestResults("buildSlug") } returns Result.success(testResults)
    coEvery { testSuiteModelMapper.mapToTestSuiteModelList(testResults) } returns testSuiteModelList
    coEvery { firebaseUrlMapper.mapBuildLogToFirebaseUrl("buildLog") } returns "firebaseUrl"

    val result = TestResultInteractor(bitriseDataSource, testSuiteModelMapper, firebaseUrlMapper)
      .execute("buildSlug")

    coVerify {
      bitriseDataSource.getArtifactDetails("buildSlug")
      bitriseDataSource.getTestResults("buildSlug")
      bitriseDataSource.getArtifactText(artifactsResponse, "buildSlug", "CostReport.txt")
      bitriseDataSource.getBuildLog("buildSlug")
      testSuiteModelMapper.mapToTestSuiteModelList(testResults)
    }

    assertEquals(
      TestResultDetailModel.WithResults(
        buildSlug = "buildSlug",
        bitriseUrl = "https://app.bitrise.io/build/buildSlug",
        yaml = "flank",
        cost = "cost",
        firebaseUrl = "firebaseUrl",
        totalFailures = 1,
        testSuiteModelList = testSuiteModelList
      ),
      result.getOrThrow()
    )
  }

  private fun mockArtifactText(
    artifactsResponse: BitriseArtifactsListResponse,
    fileName: String,
    content: String
  ) {
    coEvery {
      bitriseDataSource.getArtifactText(artifactsResponse, "buildSlug", fileName)
    } returns Result.success(content)
  }
}
