package net.lachlanmckee.flankci.core.data.entity

data class WorkflowTriggerData(
  val branch: String,
  val buildSlug: String,
  val commitHash: String,
  val jobName: String,
  val flankConfigBase64: String
)
