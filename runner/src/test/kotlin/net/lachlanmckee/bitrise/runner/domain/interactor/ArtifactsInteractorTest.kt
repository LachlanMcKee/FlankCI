package net.lachlanmckee.bitrise.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.bitrise.core.data.entity.BitriseArtifactsListResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class ArtifactsInteractorTest {
  private val ciDataSource: CIDataSource = mockk()
  private val interactor = ArtifactsInteractor(ciDataSource)

  private val applicationCall: ApplicationCall = mockk()

  @AfterEach
  fun verifyNoMoreInteractions() {
    confirmVerified(ciDataSource, applicationCall)
  }

  @Test
  fun givenArtifactSuccess_whenExecute_thenRespond() = runBlocking {
    coEvery { ciDataSource.getArtifactDetails("buildSlug") } returns Result.success(
      BitriseArtifactsListResponse(emptyList())
    )

    interactor.execute(applicationCall, "buildSlug")

    coVerifySequence {
      ciDataSource.getArtifactDetails("buildSlug")
      applicationCall.respond(BitriseArtifactsListResponse(emptyList()))
    }
  }

  @Test
  fun givenArtifactSuccess_whenExecute_thenDoNotRespond() = runBlocking {
    coEvery { ciDataSource.getArtifactDetails("buildSlug") } returns Result.failure(RuntimeException())

    interactor.execute(applicationCall, "buildSlug")

    coVerifySequence {
      ciDataSource.getArtifactDetails("buildSlug")
    }
  }
}
