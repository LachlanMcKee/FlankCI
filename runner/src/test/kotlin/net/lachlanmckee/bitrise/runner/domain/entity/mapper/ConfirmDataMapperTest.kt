package net.lachlanmckee.bitrise.runner.domain.entity.mapper

import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.domain.mapper.ImmediateFormDataCollector
import net.lachlanmckee.bitrise.runner.domain.entity.ConfirmModel
import net.lachlanmckee.bitrise.runner.domain.mapper.ConfirmDataMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConfirmDataMapperTest {
    @Test
    fun givenNoBranchOrYaml_whenMap_thenExpectFailureResult() = runBlocking {
        val confirmModel = testMapToConfirmModel(emptyList())

        assertTrue(confirmModel.isFailure)
        assertEquals("Branch must exist", confirmModel.exceptionOrNull()!!.message)
    }

    @Test
    fun givenNoBuildSlug_whenMap_thenExpectFailureResult() = runBlocking {
        val confirmModel = testMapToConfirmModel(
            listOf(
                "branch" to "dev"
            )
        )

        assertTrue(confirmModel.isFailure)
        assertEquals("Build slug must exist", confirmModel.exceptionOrNull()!!.message)
    }

    @Test
    fun givenBranchAndJobNameAndYaml_whenMap_thenAssertConfirmModel() = runBlocking {
        val confirmModel = testMapToConfirmModel(
            listOf(
                "branch" to "dev",
                "build-slug" to "slug",
                "commit-hash" to "hash",
                "job-name" to "job",
                "yaml-base64" to "config"
            )
        )

        assertEquals(
            ConfirmModel(
                branch = "dev",
                buildSlug = "slug",
                commitHash = "hash",
                jobName = "job",
                flankConfigBase64 = "config"
            ),
            confirmModel.getOrThrow()
        )
    }

    private suspend fun testMapToConfirmModel(dataToEmit: List<Pair<String, String>>): Result<ConfirmModel> {
        return ConfirmDataMapper(ImmediateFormDataCollector(dataToEmit))
            .mapToConfirmModel(mockk())
    }
}
