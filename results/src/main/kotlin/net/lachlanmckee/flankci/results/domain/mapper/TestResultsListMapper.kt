package net.lachlanmckee.flankci.results.domain.mapper

import net.lachlanmckee.flankci.core.data.entity.generic.BuildDataResponse
import net.lachlanmckee.flankci.results.domain.entity.TestResultModel
import javax.inject.Inject

internal class TestResultsListMapper @Inject constructor() {
  fun mapToTestResultsList(buildsData: List<BuildDataResponse>): List<TestResultModel> {
    return buildsData.map { build ->
      TestResultModel(
        branch = build.branch,
        status = build.statusText,
        commitHash = build.commitHash,
        triggeredAt = build.triggeredAt,
        finishedAt = build.finishedAt,
        buildSlug = build.slug,
        jobName = build.originalEnvironmentValueList
          ?.find { envValue -> envValue.key == "JOB_NAME" }
          ?.value,
        ciUrl = "https://app.bitrise.io/build/${build.slug}"
      )
    }
  }
}
