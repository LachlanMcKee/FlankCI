package net.lachlanmckee.bitrise.results.domain.entity

internal data class TestResultDetailModel(
  val buildSlug: String,
  val bitriseUrl: String,
  val cost: String,
  val testSuiteModelList: List<TestSuiteModel>
)

internal data class TestSuiteModel(
  val name: String,
  val totalTests: Int,
  val successfulTestCount: Int,
  val time: String,
  val resultType: TestResultType,
  val testCases: List<TestModel>
)

internal data class TestModel(
  val path: String,
  val webLink: String?,
  val time: String
)

internal enum class TestResultType {
  FAILURE, SKIPPED, SUCCESS
}
