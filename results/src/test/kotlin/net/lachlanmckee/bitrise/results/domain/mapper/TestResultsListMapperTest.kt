package net.lachlanmckee.bitrise.results.domain.mapper

import net.lachlanmckee.bitrise.core.data.entity.BuildDataResponse
import net.lachlanmckee.bitrise.core.data.entity.EnvironmentValueResponse
import net.lachlanmckee.bitrise.core.data.entity.MultipleBuildsResponse
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
                BuildDataResponse(
                    branch = "branch",
                    statusText = "statusText",
                    commitHash = "commitHash",
                    commitMessage = "commitMessage",
                    buildNumber = 1,
                    slug = "slug",
                    triggeredAt = "triggeredAt",
                    finishedAt = "finishedAt",
                    originalEnvironmentValueList = listOf(
                        EnvironmentValueResponse("ENV1", "VAL1")
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
                    jobName = null,
                    bitriseUrl = "https://app.bitrise.io/build/slug"
                )
            )
        )
    }

    @Test
    fun givenBuildDataWithJobName_whenMap_thenExpectListResultModelsWithJobName() {
        testMapBuilds(
            listOf(
                BuildDataResponse(
                    branch = "branch",
                    statusText = "statusText",
                    commitHash = "commitHash",
                    commitMessage = "commitMessage",
                    buildNumber = 1,
                    slug = "slug",
                    triggeredAt = "triggeredAt",
                    finishedAt = "finishedAt",
                    originalEnvironmentValueList = listOf(
                        EnvironmentValueResponse("JOB_NAME", "JOB1")
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
                    jobName = "JOB1",
                    bitriseUrl = "https://app.bitrise.io/build/slug"
                )
            )
        )
    }

    private fun testMapBuilds(data: List<BuildDataResponse>, expected: List<TestResultModel>) {
        assertEquals(expected, TestResultsListMapper().mapToTestResultsList(data))
    }
}
