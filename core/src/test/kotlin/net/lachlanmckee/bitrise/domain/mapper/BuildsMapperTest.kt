package net.lachlanmckee.bitrise.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.BuildsData
import net.lachlanmckee.bitrise.core.data.entity.BuildsResponse
import net.lachlanmckee.bitrise.core.domain.mapper.BuildsMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BuildsMapperTest {
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
                BuildsResponse.BuildData(
                    branch = "dev",
                    statusText = "status-text-dev-1",
                    commitHash = "commit-hash-dev-1",
                    commitMessage = "commit-message-dev-1",
                    buildNumber = 1,
                    slug = "slug-dev-1"
                ),
                BuildsResponse.BuildData(
                    branch = "dev",
                    statusText = "status-text-dev-2",
                    commitHash = "commit-hash-dev-2",
                    commitMessage = "commit-message-dev-2\nSecond Line",
                    buildNumber = 2,
                    slug = "slug-dev-2"
                ),
                BuildsResponse.BuildData(
                    branch = "dev",
                    statusText = "status-text-dev-3",
                    commitHash = "commit-hash-dev-3",
                    commitMessage = null,
                    buildNumber = 3,
                    slug = "slug-dev-3"
                ),
                BuildsResponse.BuildData(
                    branch = "feature1",
                    statusText = "status-text-feature-1",
                    commitHash = "commit-hash-feature-1",
                    commitMessage = "commit-message-feature-1-that-will-exceed-50-characters",
                    buildNumber = 3,
                    slug = "slug-feature-1"
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
                            buildSlug = "slug-dev-3"
                        ),
                        BuildsData.Build(
                            status = "status-text-dev-2",
                            commitHash = "commit-hash-dev-2",
                            commitMessage = "commit-message-dev-2",
                            buildNumber = 2,
                            buildSlug = "slug-dev-2"
                        ),
                        BuildsData.Build(
                            status = "status-text-dev-1",
                            commitHash = "commit-hash-dev-1",
                            commitMessage = "commit-message-dev-1",
                            buildNumber = 1,
                            buildSlug = "slug-dev-1"
                        )
                    ),
                    "feature1" to listOf(
                        BuildsData.Build(
                            status = "status-text-feature-1",
                            commitHash = "commit-hash-feature-1",
                            commitMessage = "commit-message-feature-1-that-will-exceed-50-chara...",
                            buildNumber = 3,
                            buildSlug = "slug-feature-1"
                        )
                    )
                )
            )
        )
    }

    private fun testMapBuilds(data: List<BuildsResponse.BuildData>, expected: BuildsData) {
        assertEquals(expected, BuildsMapper().mapBuilds(data))
    }
}
