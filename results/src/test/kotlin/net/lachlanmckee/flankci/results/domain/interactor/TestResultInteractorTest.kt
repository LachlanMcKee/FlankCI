package net.lachlanmckee.flankci.results.domain.interactor

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.core.data.entity.generic.ArtifactsListResponse
import net.lachlanmckee.flankci.core.data.entity.junit.TestSuite
import net.lachlanmckee.flankci.results.domain.entity.TestResultDetailModel
import net.lachlanmckee.flankci.results.domain.entity.TestResultDetailModel.WithResults.TestResultType
import net.lachlanmckee.flankci.results.domain.entity.TestResultDetailModel.WithResults.TestSuiteModel
import net.lachlanmckee.flankci.results.domain.mapper.FirebaseUrlMapper
import net.lachlanmckee.flankci.results.domain.mapper.TestSuiteModelMapper
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TestResultInteractorTest {
  private val ciDataSource: CIDataSource = mockk()
  private val testSuiteModelMapper: TestSuiteModelMapper = mockk()
  private val firebaseUrlMapper: FirebaseUrlMapper = mockk()

  @Test
  fun givenAllResponsesSucceed_whenExecute_thenExpectTestResultDetailModel() = runBlocking {
    val artifactsResponse = ArtifactsListResponse(listOf())
    coEvery { ciDataSource.getArtifactDetails(ConfigurationId("config-id"), "buildSlug") } returns Result.success(artifactsResponse)
    mockArtifactText(artifactsResponse, "flank.yml", "flank")
    mockArtifactText(artifactsResponse, "CostReport.txt", "cost")
    coEvery { ciDataSource.getBuildLog(ConfigurationId("config-id"), "buildSlug") } returns Result.success("buildLog")

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
    coEvery { ciDataSource.getTestResults(ConfigurationId("config-id"), "buildSlug") } returns Result.success(testResults)
    coEvery { testSuiteModelMapper.mapToTestSuiteModelList(testResults) } returns testSuiteModelList
    coEvery { firebaseUrlMapper.mapBuildLogToFirebaseUrl("buildLog") } returns "firebaseUrl"

    val result = TestResultInteractor(ciDataSource, testSuiteModelMapper, firebaseUrlMapper)
      .execute(ConfigurationId("config-id"), "buildSlug")

    coVerify {
      ciDataSource.getArtifactDetails(ConfigurationId("config-id"), "buildSlug")
      ciDataSource.getTestResults(ConfigurationId("config-id"), "buildSlug")
      ciDataSource.getArtifactText(ConfigurationId("config-id"), artifactsResponse, "buildSlug", "CostReport.txt")
      ciDataSource.getBuildLog(ConfigurationId("config-id"), "buildSlug")
      testSuiteModelMapper.mapToTestSuiteModelList(testResults)
    }

    assertEquals(
      TestResultDetailModel.WithResults(
        buildSlug = "buildSlug",
        ciUrl = "https://app.bitrise.io/build/buildSlug",
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
    artifactsResponse: ArtifactsListResponse,
    fileName: String,
    content: String
  ) {
    coEvery {
      ciDataSource.getArtifactText(ConfigurationId("config-id"), artifactsResponse, "buildSlug", fileName)
    } returns Result.success(content)
  }
}
