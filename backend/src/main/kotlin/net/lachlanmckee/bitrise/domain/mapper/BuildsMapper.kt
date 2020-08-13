package net.lachlanmckee.bitrise.domain.mapper

import net.lachlanmckee.bitrise.data.entity.BuildsResponse
import net.lachlanmckee.bitrise.data.entity.BuildsData

class BuildsMapper {
    fun mapBuilds(data: List<BuildsResponse.BuildData>): BuildsData {
        return BuildsData(
            branches = data
                .map { it.branch }
                .distinct(),

            branchBuilds = data
                .sortedByDescending { it.buildNumber }
                .groupBy(
                    keySelector = { it.branch },
                    valueTransform = {
                        BuildsData.Build(
                            status = it.statusText,
                            commitHash = it.commitHash,
                            commitMessage = it.commitMessage
                                ?.split("\n")
                                ?.firstOrNull()
                                ?.let { message ->
                                    if (message.length > 50) {
                                        message.take(50) + "..."
                                    } else {
                                        message
                                    }
                                },
                            buildNumber = it.buildNumber,
                            buildSlug = it.slug
                        )
                    }
                )
        )
    }
}
