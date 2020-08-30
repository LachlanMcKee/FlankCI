package net.lachlanmckee.bitrise.domain.entity

data class TestResultModel(
    val cost: String,
    val testSuites: TestSuites,
    val matrixIds: String
)
