package net.lachlanmckee.flankci.runner.domain.interactor

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.lachlanmckee.flankci.core.awaitGetOrThrow
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.core.data.entity.junit.TestCase
import net.lachlanmckee.flankci.core.data.entity.junit.TestSuite
import net.lachlanmckee.flankci.runner.domain.entity.RerunModel
import javax.inject.Inject

class TestRerunInteractor @Inject constructor(
  private val ciDataSource: CIDataSource
) {
  suspend fun execute(configurationId: ConfigurationId, buildSlug: String): Result<RerunModel> = kotlin.runCatching {
    val branchName = getBranchNameAsync(configurationId, buildSlug)
    val failedTests = getFailedTestsAsync(configurationId, buildSlug)

    RerunModel(
      branch = branchName.awaitGetOrThrow(),
      failedTests = failedTests.awaitGetOrThrow()
    )
  }

  private fun getBranchNameAsync(configurationId: ConfigurationId, buildSlug: String): Deferred<Result<String>> {
    return GlobalScope.async {
      ciDataSource
        .getBuildDetails(configurationId, buildSlug)
        .map { it.branch }
    }
  }

  private fun getFailedTestsAsync(configurationId: ConfigurationId, buildSlug: String): Deferred<Result<List<String>>> {
    return GlobalScope.async {
      ciDataSource
        .getTestResults(configurationId, buildSlug)
        .mapCatching(::getFailedTestClassNames)
    }
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
          "${it.classname}#${it.name}"
        } else {
          null
        }
      }
      .distinct()
  }
}
