package net.lachlanmckee.flankci.core.data.entity.bitrise

import com.google.gson.FieldNamingPolicy
import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
data class BitriseEnvironmentValueResponse(
  val key: String,
  val value: String
)
