package net.lachlanmckee.bitrise.runner.domain.interactor

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import net.lachlanmckee.bitrise.core.awaitGetOrThrow
import net.lachlanmckee.bitrise.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.bitrise.core.data.entity.TestCase
import net.lachlanmckee.bitrise.core.data.entity.TestSuite
import net.lachlanmckee.bitrise.runner.domain.entity.RerunModel
import javax.inject.Inject

class TestRerunInteractor @Inject constructor(
  private val ciDataSource: CIDataSource
) {
  suspend fun execute(buildSlug: String): Result<RerunModel> = kotlin.runCatching {
    val branchName = getBranchNameAsync(buildSlug)
    val failedTests = getFailedTestsAsync(buildSlug)

    RerunModel(
      branch = branchName.awaitGetOrThrow(),
      failedTests = failedTests.awaitGetOrThrow()
    )
  }

  private fun getBranchNameAsync(buildSlug: String): Deferred<Result<String>> {
    return GlobalScope.async {
      ciDataSource
        .getBuildDetails(buildSlug)
        .map { it.branch }
    }
  }

  private fun getFailedTestsAsync(buildSlug: String): Deferred<Result<List<String>>> {
    return GlobalScope.async {
      ciDataSource
        .getTestResults(buildSlug)
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
