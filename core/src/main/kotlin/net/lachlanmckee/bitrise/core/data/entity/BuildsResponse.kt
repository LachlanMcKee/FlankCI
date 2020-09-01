package net.lachlanmckee.bitrise.core.data.entity

import com.google.gson.FieldNamingPolicy
import com.google.gson.annotations.SerializedName
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
        val slug: String,
        val triggeredAt: String,
        val finishedAt: String,
        @SerializedName("original_build_params.environments")
        val originalEnvironmentValueList: List<EnvironmentValue>
    )

    @AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
    data class Paging(
        val totalItemCount: Int,
        val pageItemLimit: Int,
        val next: String
    )

    @AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
    data class EnvironmentValue(
        val mappedTo: String,
        val value: String
    )
}
