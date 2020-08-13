package net.lachlanmckee.bitrise.data.entity

import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter
data class BuildsData(
    val branches: List<String>,
    val branchBuilds: Map<String, List<Build>>
) {
    @AutoGsonAdapter
    data class Build(
        val status: String,
        val commitHash: String,
        val commitMessage: String?,
        val buildNumber: Int,
        val buildSlug: String
    )
}
