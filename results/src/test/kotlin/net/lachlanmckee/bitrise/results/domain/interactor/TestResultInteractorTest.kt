package net.lachlanmckee.bitrise.results.domain.interactor

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.core.data.datasource.remote.CIDataSource
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
  private val ciDataSource: CIDataSource = mockk()
  private val testSuiteModelMapper: TestSuiteModelMapper = mockk()
  private val firebaseUrlMapper: FirebaseUrlMapper = mockk()

  @Test
  fun givenAllResponsesSucceed_whenExecute_thenExpectTestResultDetailModel() = runBlocking {
    val artifactsResponse = BitriseArtifactsListResponse(listOf())
    coEvery { ciDataSource.getArtifactDetails("buildSlug") } returns Result.success(artifactsResponse)
    mockArtifactText(artifactsResponse, "flank.yml", "flank")
    mockArtifactText(artifactsResponse, "CostReport.txt", "cost")
    coEvery { ciDataSource.getBuildLog("buildSlug") } returns Result.success("buildLog")

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
    coEvery { ciDataSource.getTestResults("buildSlug") } returns Result.success(testResults)
    coEvery { testSuiteModelMapper.mapToTestSuiteModelList(testResults) } returns testSuiteModelList
    coEvery { firebaseUrlMapper.mapBuildLogToFirebaseUrl("buildLog") } returns "firebaseUrl"

    val result = TestResultInteractor(ciDataSource, testSuiteModelMapper, firebaseUrlMapper)
      .execute("buildSlug")

    coVerify {
      ciDataSource.getArtifactDetails("buildSlug")
      ciDataSource.getTestResults("buildSlug")
      ciDataSource.getArtifactText(artifactsResponse, "buildSlug", "CostReport.txt")
      ciDataSource.getBuildLog("buildSlug")
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
      ciDataSource.getArtifactText(artifactsResponse, "buildSlug", fileName)
    } returns Result.success(content)
  }
}
