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
import net.lachlanmckee.flankci.core.data.entity.generic.ArtifactResponse
import net.lachlanmckee.flankci.runner.data.datasource.remote.ApkDataSource
import net.lachlanmckee.flankci.runner.domain.entity.TestApkMetadata
import net.lachlanmckee.flankci.runner.domain.mapper.TestApkMetadataMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class TestApkMetadataInteractorTest {
  private val ciDataSource: CIDataSource = mockk()
  private val apkDataSource: ApkDataSource = mockk()
  private val testApkMetadataMapper: TestApkMetadataMapper = mockk()
  private val interactor: TestApkMetadataInteractor =
    TestApkMetadataInteractor(ciDataSource, apkDataSource, testApkMetadataMapper)

  private val applicationCall: ApplicationCall = mockk()

  @AfterEach
  fun verifyNoMoreInteractions() {
    confirmVerified(ciDataSource, testApkMetadataMapper, applicationCall)
  }

  @Test
  fun givenArtifactAndTestApkMethodsSucceed_whenExecute_thenRespond() = runBlocking {
    coEvery {
      ciDataSource.getArtifact(
        ConfigurationId("config-id"),
        "buildSlug",
        "artifactSlug"
      )
    } returns Result.success(
      ArtifactResponse("apk-url")
    )
    coEvery { apkDataSource.getTestApkTestMethods("apk-url") } returns Result.success(emptyList())

    val testApkMetadata = mockk<TestApkMetadata>()
    coEvery {
      testApkMetadataMapper.mapTestApkMetadata(
        ConfigurationId("config-id"),
        emptyList()
      )
    } returns testApkMetadata

    interactor.execute(applicationCall, ConfigurationId("config-id"), "buildSlug", "artifactSlug")

    coVerifySequence {
      ciDataSource.getArtifact(ConfigurationId("config-id"), "buildSlug", "artifactSlug")
      apkDataSource.getTestApkTestMethods("apk-url")
      testApkMetadataMapper.mapTestApkMetadata(ConfigurationId("config-id"), emptyList())
      applicationCall.respond(testApkMetadata)
    }
  }

  @Test
  fun givenArtifactFails_whenExecute_thenDoNotRespond() = runBlocking {
    coEvery {
      ciDataSource.getArtifact(
        ConfigurationId("config-id"),
        "buildSlug",
        "artifactSlug"
      )
    } returns Result.failure(RuntimeException())

    interactor.execute(applicationCall, ConfigurationId("config-id"), "buildSlug", "artifactSlug")

    coVerifySequence {
      ciDataSource.getArtifact(ConfigurationId("config-id"), "buildSlug", "artifactSlug")
    }
  }

  @Test
  fun givenArtifactSucceedsAndTestApkMethodsFails_whenExecute_thenDoNotRespond() = runBlocking {
    coEvery {
      ciDataSource.getArtifact(
        ConfigurationId("config-id"),
        "buildSlug",
        "artifactSlug"
      )
    } returns Result.success(
      ArtifactResponse("apk-url")
    )
    coEvery { apkDataSource.getTestApkTestMethods("apk-url") } returns Result.failure(RuntimeException())

    interactor.execute(applicationCall, ConfigurationId("config-id"), "buildSlug", "artifactSlug")

    coVerifySequence {
      ciDataSource.getArtifact(ConfigurationId("config-id"), "buildSlug", "artifactSlug")
      apkDataSource.getTestApkTestMethods("apk-url")
    }
  }

  @Test
  fun givenArtifactAndTestApkMethodsSucceedAndMappingFails_whenExecute_thenDoNotRespond() = runBlocking {
    coEvery {
      ciDataSource.getArtifact(
        ConfigurationId("config-id"),
        "buildSlug",
        "artifactSlug"
      )
    } returns Result.success(
      ArtifactResponse("apk-url")
    )
    coEvery { apkDataSource.getTestApkTestMethods("apk-url") } returns Result.success(emptyList())

    coEvery {
      testApkMetadataMapper.mapTestApkMetadata(
        ConfigurationId("config-id"),
        emptyList()
      )
    } throws RuntimeException()

    interactor.execute(applicationCall, ConfigurationId("config-id"), "buildSlug", "artifactSlug")

    coVerifySequence {
      ciDataSource.getArtifact(ConfigurationId("config-id"), "buildSlug", "artifactSlug")
      apkDataSource.getTestApkTestMethods("apk-url")
      testApkMetadataMapper.mapTestApkMetadata(ConfigurationId("config-id"), emptyList())
    }
  }
}
