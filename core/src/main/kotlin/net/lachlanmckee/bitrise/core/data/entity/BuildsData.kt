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
        val buildSlug: String,
        val triggeredAt: String,
        val finishedAt: String,
        val originalEnvironmentValueList: List<EnvironmentValue>
    )

    data class EnvironmentValue(
        val name: String,
        val value: String
    )
}
