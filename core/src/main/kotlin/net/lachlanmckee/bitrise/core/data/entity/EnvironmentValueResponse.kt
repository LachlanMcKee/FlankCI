package net.lachlanmckee.bitrise.core.data.entity

import com.google.gson.FieldNamingPolicy
import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
data class EnvironmentValueResponse(
    val mappedTo: String,
    val value: String
)
