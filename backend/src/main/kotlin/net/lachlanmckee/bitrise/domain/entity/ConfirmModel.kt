package net.lachlanmckee.bitrise.domain.entity

data class ConfirmModel(
    val branch: String,
    val jobName: String,
    val flankConfigBase64: String
)
