package net.lachlanmckee.bitrise.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.core.data.entity.BitriseArtifactsListResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class ArtifactsInteractorTest {
  private val bitriseDataSource: BitriseDataSource = mockk()
  private val interactor = ArtifactsInteractor(bitriseDataSource)

  private val applicationCall: ApplicationCall = mockk()

  @AfterEach
  fun verifyNoMoreInteractions() {
    confirmVerified(bitriseDataSource, applicationCall)
  }

  @Test
  fun givenArtifactSuccess_whenExecute_thenRespond() = runBlocking {
    coEvery { bitriseDataSource.getArtifactDetails("buildSlug") } returns Result.success(
      BitriseArtifactsListResponse(emptyList())
    )

    interactor.execute(applicationCall, "buildSlug")

    coVerifySequence {
      bitriseDataSource.getArtifactDetails("buildSlug")
      applicationCall.respond(BitriseArtifactsListResponse(emptyList()))
    }
  }

  @Test
  fun givenArtifactSuccess_whenExecute_thenDoNotRespond() = runBlocking {
    coEvery { bitriseDataSource.getArtifactDetails("buildSlug") } returns Result.failure(RuntimeException())

    interactor.execute(applicationCall, "buildSlug")

    coVerifySequence {
      bitriseDataSource.getArtifactDetails("buildSlug")
    }
  }
}
