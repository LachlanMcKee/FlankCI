package net.lachlanmckee.bitrise.results.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.TestCase
import net.lachlanmckee.bitrise.core.data.entity.TestSuite
import net.lachlanmckee.bitrise.results.domain.entity.TestResultDetailModel.WithResults.*
import javax.inject.Inject

internal class TestSuiteModelMapper @Inject constructor() {
  fun mapToTestSuiteModelList(testSuiteList: List<TestSuite>): List<TestSuiteModel> {
    return testSuiteList
      .fold(emptyMap<NameAndResultType, List<TestCase>>()) { acc1, testSuite ->
        testSuite.testcase?.fold(acc1) { acc2, testCase ->
          val resultType = when {
            testCase.failure != null -> TestResultType.FAILURE
            testCase.webLink == null -> TestResultType.SKIPPED
            else -> TestResultType.SUCCESS
          }

          val groupKey = NameAndResultType(testSuite.name, resultType)

          acc2.plus(
            groupKey to acc2.getOrDefault(groupKey, emptyList())
              .plus(testCase)
          )
        } ?: acc1
      }
      .map { testGroup: Map.Entry<NameAndResultType, List<TestCase>> ->
        TestSuiteModel(
          name = testGroup.key.name,
          totalTests = testGroup.value.count(),
          time = String.format("%.2f", testGroup.value.sumByDouble { it.time.toDouble() }),
          resultType = testGroup.key.resultType,
          testCases = testGroup.value
            .map(::mapToTestModel)
            .sortedBy { it.path }
        )
      }
      .sortedWith(
        compareBy<TestSuiteModel> { it.resultType }
          .thenBy { it.name }
      )
  }

  private fun mapToTestModel(testCase: TestCase): TestModel {
    return TestModel(
      path = "${testCase.classname}#${testCase.name}",
      webLink = testCase.webLink?.trim(),
      time = testCase.time
    )
  }

  private data class NameAndResultType(
    val name: String,
    val resultType: TestResultType
  )
}
