package net.lachlanmckee.bitrise.results.domain.entity

internal sealed class TestResultDetailModel {
  abstract val buildSlug: String
  abstract val bitriseUrl: String
  abstract val firebaseUrl: String
  abstract val yaml: String?
  abstract val totalFailures: Int

  internal data class WithResults(
    override val buildSlug: String,
    override val bitriseUrl: String,
    override val firebaseUrl: String,
    override val totalFailures: Int,
    override val yaml: String?,
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
      val failure: String?,
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
    override val yaml: String? = null
    override val totalFailures: Int = 0
  }
}
