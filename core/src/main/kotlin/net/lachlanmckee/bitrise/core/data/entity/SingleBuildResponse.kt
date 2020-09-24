package net.lachlanmckee.bitrise.core.data.entity

import com.google.gson.FieldNamingPolicy
import gsonpath.GsonResultList
import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
data class SingleBuildResponse(val data: BuildDataResponse)
