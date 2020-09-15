package net.lachlanmckee.bitrise.results.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.BuildsResponse
import net.lachlanmckee.bitrise.results.domain.entity.TestResultModel
import javax.inject.Inject

internal class TestResultsListMapper @Inject constructor() {
    fun mapToTestResultsList(buildsData: List<BuildsResponse.BuildData>): List<TestResultModel> {
        return buildsData.map { build ->
            TestResultModel(
                branch = build.branch,
                status = build.statusText,
                commitHash = build.commitHash,
                triggeredAt = build.triggeredAt,
                finishedAt = build.finishedAt,
                buildSlug = build.slug,
                jobName = build.originalEnvironmentValueList
                    .find { envValue -> envValue.mappedTo == "JOB_NAME" }
                    ?.value,
                bitriseUrl = "https://app.bitrise.io/build/${build.slug}"
            )
        }
    }
}