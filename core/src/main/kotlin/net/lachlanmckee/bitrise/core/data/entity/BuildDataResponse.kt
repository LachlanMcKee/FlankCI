package net.lachlanmckee.bitrise.core.data.entity

import com.google.gson.FieldNamingPolicy
import com.google.gson.annotations.SerializedName
import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
data class BuildDataResponse(
  val branch: String,
  val statusText: String,
  val commitHash: String,
  val commitMessage: String?,
  val buildNumber: Int,
  val slug: String,
  val triggeredAt: String,
  val finishedAt: String?,
  @SerializedName("original_build_params.environments")
  val originalEnvironmentValueList: List<EnvironmentValueResponse>
)
