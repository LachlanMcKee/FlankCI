package net.lachlanmckee.bitrise.runner.domain.interactor

import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.core.data.entity.TestCase
import net.lachlanmckee.bitrise.core.data.entity.TestSuite
import net.lachlanmckee.bitrise.runner.domain.entity.RerunModel
import javax.inject.Inject

class TestRerunInteractor @Inject constructor(
  private val bitriseDataSource: BitriseDataSource
) {
  suspend fun execute(buildSlug: String): Result<RerunModel> = kotlin.runCatching {
    val buildDetails = bitriseDataSource.getBuildDetails(buildSlug)
      .getOrThrow()

    bitriseDataSource
      .getTestResults(buildSlug)
      .map { testSuite ->
        RerunModel(
          branch = buildDetails.branch,
          failedTests = getFailedTestClassNames(testSuite)
        )
      }
      .getOrThrow()
  }

  private fun getFailedTestClassNames(testSuiteList: List<TestSuite>): List<String> {
    return testSuiteList
      .asSequence()
      .flatMap { test ->
        mapFailedTestClassNames(test.testcase ?: emptyList())
      }
      .distinct()
      .sorted()
      .toList()
  }

  private fun mapFailedTestClassNames(testCases: List<TestCase>): Sequence<String> {
    return testCases
      .asSequence()
      .mapNotNull {
        if (it.failure != null) {
          it.classname
        } else {
          null
        }
      }
      .distinct()
  }
}
