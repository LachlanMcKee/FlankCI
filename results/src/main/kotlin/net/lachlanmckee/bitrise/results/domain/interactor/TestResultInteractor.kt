package net.lachlanmckee.bitrise.results.domain.interactor

import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel
import net.lachlanmckee.bitrise.results.domain.mapper.TestSuiteModelMapper
import javax.inject.Inject

internal class TestResultInteractor @Inject constructor(
  private val bitriseDataSource: BitriseDataSource,
  private val testSuiteModelMapper: TestSuiteModelMapper
) {
  suspend fun execute(buildSlug: String): Result<TestResultDetailModel> {
    return bitriseDataSource
      .getArtifactDetails(buildSlug)
      .mapCatching { artifactDetails ->
        println(artifactDetails)

        if (artifactDetails.data.isEmpty()) {
          throw IllegalStateException("No artifacts found. Perhaps the tests did not run?")
        }

        TestResultDetailModel(
          buildSlug = buildSlug,
          bitriseUrl = "https://app.bitrise.io/build/$buildSlug",
          cost = bitriseDataSource
            .getArtifactText(artifactDetails, buildSlug, "CostReport.txt")
            .getOrThrow(),

          testSuiteModelList = bitriseDataSource
            .getTestResults(buildSlug)
            .getOrThrow()
            .map(testSuiteModelMapper::mapToTestSuiteModel)
        )
      }
  }
}
