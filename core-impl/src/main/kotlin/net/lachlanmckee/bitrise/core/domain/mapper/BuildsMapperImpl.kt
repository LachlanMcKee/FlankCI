package net.lachlanmckee.bitrise.core.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.BuildDataResponse
import net.lachlanmckee.bitrise.core.data.entity.BuildsData
import javax.inject.Inject

internal class BuildsMapperImpl @Inject constructor() : BuildsMapper {
    override fun mapBuilds(data: List<BuildDataResponse>): BuildsData {
        return BuildsData(
            branches = mapBranches(data),
            branchBuilds = mapBranchBuilds(data)
        )
    }

    private fun mapBranches(data: List<BuildDataResponse>): List<String> {
        return data
            .map { it.branch }
            .distinct()
    }

    private fun mapBranchBuilds(data: List<BuildDataResponse>): Map<String, List<BuildsData.Build>> {
        return data
            .sortedByDescending { it.buildNumber }
            .groupBy(
                keySelector = { it.branch },
                valueTransform = ::mapBuildData
            )
    }

    private fun mapBuildData(response: BuildDataResponse): BuildsData.Build {
        return BuildsData.Build(
            status = response.statusText,
            commitHash = response.commitHash,
            commitMessage = mapCommitMessage(response),
            buildNumber = response.buildNumber,
            buildSlug = response.slug,
            triggeredAt = response.triggeredAt,
            finishedAt = response.finishedAt,
            originalEnvironmentValueList = response.originalEnvironmentValueList.map { env ->
                BuildsData.EnvironmentValue(env.mappedTo, env.value)
            }
        )
    }

    private fun mapCommitMessage(response: BuildDataResponse): String? {
        val commitMessage = response.commitMessage ?: return null
        return commitMessage
            .split("\n")
            .first()
            .let { message ->
                if (message.length > 50) {
                    message.take(50) + "..."
                } else {
                    message
                }
            }
    }
}
