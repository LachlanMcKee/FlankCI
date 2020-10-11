package net.lachlanmckee.bitrise.core.domain.mapper

import gsonpath.GsonSafeList
import net.lachlanmckee.bitrise.core.data.entity.BuildDataResponse
import net.lachlanmckee.bitrise.core.data.entity.BuildsData
import net.lachlanmckee.bitrise.core.data.entity.EnvironmentValueResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class BuildsMapperImplTest {
  @Test
  fun givenNoBuildData_whenMap_thenExpectEmptyBuildsData() {
    testMapBuilds(
      emptyList(),
      BuildsData(
        branches = emptyList(),
        branchBuilds = emptyMap()
      )
    )
  }

  @Test
  fun givenBuildDataExists_whenMap_thenAssertBuildsData() {
    testMapBuilds(
      listOf(
        BuildDataResponse(
          branch = "dev",
          statusText = "status-text-dev-1",
          commitHash = "commit-hash-dev-1",
          commitMessage = "commit-message-dev-1",
          buildNumber = 1,
          slug = "slug-dev-1",
          triggeredAt = "2020-09-01T16:00:00Z",
          finishedAt = "2020-09-01T17:00:00Z",
          originalEnvironmentValueList = GsonSafeList<EnvironmentValueResponse>().apply {
            add(EnvironmentValueResponse("ENV1", "VALUE1"))
          }
        ),
        BuildDataResponse(
          branch = "dev",
          statusText = "status-text-dev-2",
          commitHash = "commit-hash-dev-2",
          commitMessage = "commit-message-dev-2\nSecond Line",
          buildNumber = 2,
          slug = "slug-dev-2",
          triggeredAt = "2020-09-01T16:00:00Z",
          finishedAt = "2020-09-01T17:00:00Z",
          originalEnvironmentValueList = GsonSafeList<EnvironmentValueResponse>()
        ),
        BuildDataResponse(
          branch = "dev",
          statusText = "status-text-dev-3",
          commitHash = "commit-hash-dev-3",
          commitMessage = null,
          buildNumber = 3,
          slug = "slug-dev-3",
          triggeredAt = "2020-09-01T16:00:00Z",
          finishedAt = "2020-09-01T17:00:00Z",
          originalEnvironmentValueList = GsonSafeList<EnvironmentValueResponse>()
        ),
        BuildDataResponse(
          branch = "feature1",
          statusText = "status-text-feature-1",
          commitHash = "commit-hash-feature-1",
          commitMessage = "commit-message-feature-1-that-will-exceed-50-characters",
          buildNumber = 3,
          slug = "slug-feature-1",
          triggeredAt = "2020-09-01T16:00:00Z",
          finishedAt = "2020-09-01T17:00:00Z",
          originalEnvironmentValueList = GsonSafeList<EnvironmentValueResponse>()
        )
      ),
      BuildsData(
        branches = listOf("dev", "feature1"),
        branchBuilds = mapOf(
          "dev" to listOf(
            BuildsData.Build(
              status = "status-text-dev-3",
              commitHash = "commit-hash-dev-3",
              commitMessage = null,
              buildNumber = 3,
              buildSlug = "slug-dev-3",
              triggeredAt = "2020-09-01T16:00:00Z",
              finishedAt = "2020-09-01T17:00:00Z",
              originalEnvironmentValueList = emptyList()
            ),
            BuildsData.Build(
              status = "status-text-dev-2",
              commitHash = "commit-hash-dev-2",
              commitMessage = "commit-message-dev-2",
              buildNumber = 2,
              buildSlug = "slug-dev-2",
              triggeredAt = "2020-09-01T16:00:00Z",
              finishedAt = "2020-09-01T17:00:00Z",
              originalEnvironmentValueList = emptyList()
            ),
            BuildsData.Build(
              status = "status-text-dev-1",
              commitHash = "commit-hash-dev-1",
              commitMessage = "commit-message-dev-1",
              buildNumber = 1,
              buildSlug = "slug-dev-1",
              triggeredAt = "2020-09-01T16:00:00Z",
              finishedAt = "2020-09-01T17:00:00Z",
              originalEnvironmentValueList = listOf(
                BuildsData.EnvironmentValue("ENV1", "VALUE1")
              )
            )
          ),
          "feature1" to listOf(
            BuildsData.Build(
              status = "status-text-feature-1",
              commitHash = "commit-hash-feature-1",
              commitMessage = "commit-message-feature-1-that-will-exceed-50-chara...",
              buildNumber = 3,
              buildSlug = "slug-feature-1",
              triggeredAt = "2020-09-01T16:00:00Z",
              finishedAt = "2020-09-01T17:00:00Z",
              originalEnvironmentValueList = emptyList()
            )
          )
        )
      )
    )
  }

  private fun testMapBuilds(data: List<BuildDataResponse>, expected: BuildsData) {
    assertEquals(expected, BuildsMapperImpl().mapBuilds(data))
  }
}
