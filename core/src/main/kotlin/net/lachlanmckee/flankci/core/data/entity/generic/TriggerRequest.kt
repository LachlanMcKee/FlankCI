package net.lachlanmckee.flankci.core.data.entity.generic

data class TriggerRequest(
  val buildParams: BuildParams,
  val hookInfo: HookInfo = HookInfo(),
  val triggeredBy: String = "curl"
) {
  data class HookInfo(
    val type: String = "bitrise"
  )

  data class BuildParams(
    val environments: List<EnvironmentValue>,
    val branch: String,
    val commitHash: String,
    val workflowId: String
  ) {
    data class EnvironmentValue(
      val mappedTo: String,
      val value: String,
      val isExpand: Boolean = false
    )
  }
}
