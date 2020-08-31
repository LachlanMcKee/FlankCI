package net.lachlanmckee.bitrise.results.domain.entity

internal data class TestResultModel(
    val cost: String,
    val testSuites: TestSuites,
    val matrixIds: String
)
