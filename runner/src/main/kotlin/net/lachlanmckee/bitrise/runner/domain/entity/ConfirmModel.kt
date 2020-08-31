package net.lachlanmckee.bitrise.runner.domain.entity

internal data class ConfirmModel(
    val branch: String,
    val commitHash: String,
    val jobName: String,
    val flankConfigBase64: String
)
