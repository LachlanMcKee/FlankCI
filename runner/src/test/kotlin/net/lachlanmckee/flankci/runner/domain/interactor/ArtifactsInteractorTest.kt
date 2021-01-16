package net.lachlanmckee.flankci.runner.domain.interactor

import io.ktor.application.*
import io.ktor.response.*
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.core.data.entity.generic.ArtifactsListResponse
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
    coEvery { ciDataSource.getArtifactDetails(ConfigurationId("config-id"), "buildSlug") } returns Result.success(
      ArtifactsListResponse(emptyList())
    )

    interactor.execute(applicationCall, ConfigurationId("config-id"), "buildSlug")

    coVerifySequence {
      ciDataSource.getArtifactDetails(ConfigurationId("config-id"), "buildSlug")
      applicationCall.respond(ArtifactsListResponse(emptyList()))
    }
  }

  @Test
  fun givenArtifactSuccess_whenExecute_thenDoNotRespond() = runBlocking {
    coEvery { ciDataSource.getArtifactDetails(ConfigurationId("config-id"), "buildSlug") } returns Result.failure(
      RuntimeException()
    )

    interactor.execute(applicationCall, ConfigurationId("config-id"), "buildSlug")

    coVerifySequence {
      ciDataSource.getArtifactDetails(ConfigurationId("config-id"), "buildSlug")
    }
  }
}
