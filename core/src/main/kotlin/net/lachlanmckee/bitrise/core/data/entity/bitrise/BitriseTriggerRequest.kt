package net.lachlanmckee.bitrise.core.data.entity.bitrise

import com.google.gson.FieldNamingPolicy
import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
data class BitriseTriggerRequest(
  val buildParams: BuildParams,
  val hookInfo: HookInfo = HookInfo(),
  val triggeredBy: String = "curl"
) {
  @AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
  data class HookInfo(
    val type: String = "bitrise"
  )

  @AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
  data class BuildParams(
    val environments: List<EnvironmentValue>,
    val branch: String,
    val commitHash: String,
    val workflowId: String
  ) {
    @AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
    data class EnvironmentValue(
      val mappedTo: String,
      val value: String,
      val isExpand: Boolean = false
    )
  }
}
