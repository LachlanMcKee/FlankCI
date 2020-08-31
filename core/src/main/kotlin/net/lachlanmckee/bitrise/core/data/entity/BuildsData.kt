package net.lachlanmckee.bitrise.core.data.entity

data class BuildsData(
    val branches: List<String>,
    val branchBuilds: Map<String, List<Build>>
) {
    data class Build(
        val status: String,
        val commitHash: String,
        val commitMessage: String?,
        val buildNumber: Int,
        val buildSlug: String
    )
}
