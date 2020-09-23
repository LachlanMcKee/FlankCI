package net.lachlanmckee.bitrise.results.domain.entity

import net.lachlanmckee.bitrise.core.data.entity.TestSuites

internal data class TestResultDetailModel(
    val bitriseUrl: String,
    val cost: String,
    val testSuites: TestSuites,
    val matrixIds: String
)
