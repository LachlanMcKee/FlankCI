package net.lachlanmckee.flankci.runner.domain.entity

internal data class ConfirmModel(
  val branch: String,
  val buildSlug: String,
  val commitHash: String,
  val jobName: String,
  val flankConfigBase64: String
)
