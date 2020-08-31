package net.lachlanmckee.bitrise.core.data.entity

import com.google.gson.FieldNamingPolicy
import gsonpath.GsonResultList
import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
data class BuildsResponse(
    val data: GsonResultList<BuildData>,
    val paging: Paging
) {
    @AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
    data class BuildData(
        val branch: String,
        val statusText: String,
        val commitHash: String,
        val commitMessage: String?,
        val buildNumber: Int,
        val slug: String
    )

    @AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
    data class Paging(
        val totalItemCount: Int,
        val pageItemLimit: Int,
        val next: String
    )
}