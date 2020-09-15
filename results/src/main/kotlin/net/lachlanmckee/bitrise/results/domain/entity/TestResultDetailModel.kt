package net.lachlanmckee.bitrise.results.domain.entity

internal data class TestResultDetailModel(
    val bitriseUrl: String,
    val cost: String,
    val testSuites: TestSuites,
    val matrixIds: String
)
