package net.lachlanmckee.bitrise.results.domain.entity

internal data class TestResultModel(
    val branch: String,
    val status: String,
    val commitHash: String,
    val triggeredAt: String,
    val finishedAt: String?,
    val buildSlug: String,
    val jobName: String?,
    val bitriseUrl: String
)
