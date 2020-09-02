package net.lachlanmckee.bitrise.results.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.BuildsResponse
import net.lachlanmckee.bitrise.results.domain.entity.TestResultModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TestResultsListMapperTest {
    @Test
    fun givenNoBuildData_whenMap_thenExpectEmptyListResultModels() {
        testMapBuilds(emptyList(), emptyList())
    }

    @Test
    fun givenBuildDataWithoutJobName_whenMap_thenExpectListResultModelsWithoutJobName() {
        testMapBuilds(
            listOf(
                BuildsResponse.BuildData(
                    branch = "branch",
                    statusText = "statusText",
                    commitHash = "commitHash",
                    commitMessage = "commitMessage",
                    buildNumber = 1,
                    slug = "slug",
                    triggeredAt = "triggeredAt",
                    finishedAt = "finishedAt",
                    originalEnvironmentValueList = listOf(
                        BuildsResponse.EnvironmentValue("ENV1", "VAL1")
                    )
                )
            ),
            listOf(
                TestResultModel(
                    branch = "branch",
                    status = "statusText",
                    commitHash = "commitHash",
                    triggeredAt = "triggeredAt",
                    finishedAt = "finishedAt",
                    buildSlug = "slug",
                    jobName = null
                )
            )
        )
    }

    @Test
    fun givenBuildDataWithJobName_whenMap_thenExpectListResultModelsWithJobName() {
        testMapBuilds(
            listOf(
                BuildsResponse.BuildData(
                    branch = "branch",
                    statusText = "statusText",
                    commitHash = "commitHash",
                    commitMessage = "commitMessage",
                    buildNumber = 1,
                    slug = "slug",
                    triggeredAt = "triggeredAt",
                    finishedAt = "finishedAt",
                    originalEnvironmentValueList = listOf(
                        BuildsResponse.EnvironmentValue("JOB_NAME", "JOB1")
                    )
                )
            ),
            listOf(
                TestResultModel(
                    branch = "branch",
                    status = "statusText",
                    commitHash = "commitHash",
                    triggeredAt = "triggeredAt",
                    finishedAt = "finishedAt",
                    buildSlug = "slug",
                    jobName = "JOB1"
                )
            )
        )
    }

    private fun testMapBuilds(data: List<BuildsResponse.BuildData>, expected: List<TestResultModel>) {
        assertEquals(expected, TestResultsListMapper().mapToTestResultsList(data))
    }
}
