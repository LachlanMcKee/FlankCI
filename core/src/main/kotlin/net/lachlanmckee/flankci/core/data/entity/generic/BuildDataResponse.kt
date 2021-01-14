package net.lachlanmckee.flankci.core.data.entity.generic

data class BuildDataResponse(
  val branch: String,
  val statusText: String,
  val commitHash: String,
  val commitMessage: String?,
  val buildNumber: Int,
  val slug: String,
  val triggeredAt: String,
  val finishedAt: String?,
  val originalEnvironmentValueList: List<EnvironmentValueResponse>?
)
