package net.lachlanmckee.bitrise.domain.interactor

import com.google.gson.JsonObject
import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import io.mockk.*
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import org.junit.jupiter.api.Test

class ArtifactsInteractorTest {
    private val bitriseDataSource: BitriseDataSource = mockk()
    private val interactor = ArtifactsInteractor(bitriseDataSource)

    @Test
    fun givenArtifactSuccess_whenGetArtifactDetails_thenRespond() = runBlocking {
        coEvery { bitriseDataSource.getArtifactDetails("buildSlug") } returns Result.success(JsonObject())

        val applicationCall = mockk<ApplicationCall>()
        interactor.execute(applicationCall, "buildSlug")

        coVerify { applicationCall.respond(JsonObject()) }
        confirmVerified(applicationCall)
    }

    @Test
    fun givenArtifactSuccess_whenGetArtifactDetails_thenDoNothing() = runBlocking {
        coEvery { bitriseDataSource.getArtifactDetails("buildSlug") } returns Result.failure(RuntimeException())

        val applicationCall = mockk<ApplicationCall>()
        interactor.execute(applicationCall, "buildSlug")

        confirmVerified(applicationCall)
    }
}
