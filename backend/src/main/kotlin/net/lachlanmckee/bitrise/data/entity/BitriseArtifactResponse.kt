package net.lachlanmckee.bitrise.data.entity

import com.google.gson.FieldNamingPolicy
import com.google.gson.annotations.SerializedName
import gsonpath.annotation.AutoGsonAdapter
import gsonpath.annotation.NestedJson

@AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
data class BitriseArtifactResponse(
    @SerializedName("data.expiring_download_url")
    val expiringDownloadUrl: String
)
