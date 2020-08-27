package net.lachlanmckee.bitrise.domain.mapper

import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.domain.entity.ConfirmModel
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
    fun givenNoCommitHash_whenMap_thenExpectFailureResult() = runBlocking {
        val confirmModel = testMapToConfirmModel(
            listOf(
                "branch" to "dev"
            )
        )

        assertTrue(confirmModel.isFailure)
        assertEquals("Commit hash must exist", confirmModel.exceptionOrNull()!!.message)
    }

    @Test
    fun givenBranchAndJobNameAndYaml_whenMap_thenAssertConfirmModel() = runBlocking {
        val confirmModel = testMapToConfirmModel(
            listOf(
                "branch" to "dev",
                "commit-hash" to "hash",
                "job-name" to "job",
                "yaml-base64" to "config"
            )
        )

        assertEquals(
            ConfirmModel(
                branch = "dev",
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
