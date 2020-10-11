package net.lachlanmckee.bitrise.results.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.TestCase
import net.lachlanmckee.bitrise.core.data.entity.TestSuite
import net.lachlanmckee.bitrise.results.domain.entity.TestModel
import net.lachlanmckee.bitrise.results.domain.entity.TestResultType
import net.lachlanmckee.bitrise.results.domain.entity.TestSuiteModel
import javax.inject.Inject

internal class TestSuiteModelMapper @Inject constructor() {
  fun mapToTestSuiteModel(testSuite: TestSuite): TestSuiteModel {
    return TestSuiteModel(
      name = testSuite.name,
      totalTests = testSuite.tests,
      successfulTestCount = testSuite.tests - testSuite.failures,
      time = testSuite.time,
      testCases = testSuite
        .testcase
        ?.map(::mapToTestModel)
        ?: emptyList()
    )
  }

  private fun mapToTestModel(testCase: TestCase): TestModel {
    return TestModel(
      path = "${testCase.classname}#${testCase.name}",
      webLink = testCase.webLink?.trim(),
      resultType = when {
        testCase.failure != null -> TestResultType.FAILURE
        testCase.webLink == null -> TestResultType.SKIPPED
        else -> TestResultType.SUCCESS
      }
    )
  }
}
