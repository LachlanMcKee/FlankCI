package net.lachlanmckee.bitrise.results.domain.entity

internal sealed class TestResultDetailModel {
  abstract val buildSlug: String
  abstract val bitriseUrl: String
  abstract val firebaseUrl: String
  abstract val totalFailures: Int

  internal data class WithResults(
    override val buildSlug: String,
    override val bitriseUrl: String,
    override val firebaseUrl: String,
    override val totalFailures: Int,
    val cost: String?,
    val testSuiteModelList: List<TestSuiteModel>
  ) : TestResultDetailModel() {

    internal data class TestSuiteModel(
      val name: String,
      val totalTests: Int,
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
  }

  internal data class NoResults(
    override val buildSlug: String,
    override val bitriseUrl: String,
    override val firebaseUrl: String
  ) : TestResultDetailModel() {
    override val totalFailures: Int = 0
  }
}
