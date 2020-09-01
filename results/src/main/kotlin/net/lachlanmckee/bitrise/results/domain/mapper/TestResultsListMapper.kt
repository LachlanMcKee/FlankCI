package net.lachlanmckee.bitrise.results.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.BuildsData
import net.lachlanmckee.bitrise.results.domain.entity.TestResultModel
import javax.inject.Inject

internal class TestResultsListMapper @Inject constructor() {
    fun mapToTestResultsList(buildsData: BuildsData): List<TestResultModel> {
        return buildsData.branchBuilds.entries.flatMap { (branch, builds) ->
            builds.map { build ->
                TestResultModel(
                    branch = branch,
                    status = build.status,
                    commitHash = build.commitHash,
                    triggeredAt = build.triggeredAt,
                    finishedAt = build.finishedAt,
                    buildSlug = build.buildSlug,
                    jobName = build.originalEnvironmentValueList
                        .find { envValue -> envValue.name == "JOB_NAME" }
                        ?.value
                )
            }
        }
    }
}
