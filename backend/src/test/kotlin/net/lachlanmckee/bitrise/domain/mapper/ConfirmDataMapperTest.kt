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
    fun givenNoYaml_whenMap_thenExpectFailureResult() = runBlocking {
        val confirmModel = testMapToConfirmModel(
            listOf(
                "branch" to "dev"
            )
        )

        assertTrue(confirmModel.isFailure)
        assertEquals("Flank Base64 must exist", confirmModel.exceptionOrNull()!!.message)
    }

    @Test
    fun givenBranchAndYaml_whenMap_thenAssertConfirmModel() = runBlocking {
        val confirmModel = testMapToConfirmModel(
            listOf(
                "branch" to "dev",
                "yaml-base64" to "config"
            )
        )

        assertEquals(
            ConfirmModel(
                branch = "dev",
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
