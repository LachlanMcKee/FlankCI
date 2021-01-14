package net.lachlanmckee.flankci.runner.domain.entity

data class RerunModel(
  val branch: String,
  val failedTests: List<String>
)
