package net.lachlanmckee.bitrise.runner.domain.interactor

import com.google.gson.JsonObject
import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.runner.domain.entity.TestApkMetadata
import net.lachlanmckee.bitrise.runner.domain.mapper.TestApkMetadataMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class TestApkMetadataInteractorTest {
    private val bitriseDataSource: BitriseDataSource = mockk()
    private val testApkMetadataMapper: TestApkMetadataMapper = mockk()
    private val interactor: TestApkMetadataInteractor =
        TestApkMetadataInteractor(bitriseDataSource, testApkMetadataMapper)

    private val applicationCall: ApplicationCall = mockk()

    @AfterEach
    fun verifyNoMoreInteractions() {
        confirmVerified(bitriseDataSource, testApkMetadataMapper, applicationCall)
    }

    @Test
    fun givenArtifactAndTestApkMethodsSucceed_whenExecute_thenRespond() = runBlocking {
        val artifactDataJson = JsonObject().apply {
            add("data", JsonObject().apply {
                addProperty("expiring_download_url", "apk-url")
            })
        }
        coEvery { bitriseDataSource.getArtifact("buildSlug", "artifactSlug") } returns Result.success(artifactDataJson)
        coEvery { bitriseDataSource.getTestApkTestMethods("apk-url") } returns Result.success(emptyList())

        val testApkMetadata = mockk<TestApkMetadata>()
        coEvery { testApkMetadataMapper.mapTestApkMetadata(emptyList()) } returns testApkMetadata

        interactor.execute(applicationCall, "buildSlug", "artifactSlug")

        coVerifySequence {
            bitriseDataSource.getArtifact("buildSlug", "artifactSlug")
            bitriseDataSource.getTestApkTestMethods("apk-url")
            testApkMetadataMapper.mapTestApkMetadata(emptyList())
            applicationCall.respond(testApkMetadata)
        }
    }

    @Test
    fun givenArtifactFails_whenExecute_thenDoNotRespond() = runBlocking {
        coEvery {
            bitriseDataSource.getArtifact(
                "buildSlug",
                "artifactSlug"
            )
        } returns Result.failure(RuntimeException())

        interactor.execute(applicationCall, "buildSlug", "artifactSlug")

        coVerifySequence {
            bitriseDataSource.getArtifact("buildSlug", "artifactSlug")
        }
    }

    @Test
    fun givenArtifactSucceedsAndTestApkMethodsFails_whenExecute_thenDoNotRespond() = runBlocking {
        val artifactDataJson = JsonObject().apply {
            add("data", JsonObject().apply {
                addProperty("expiring_download_url", "apk-url")
            })
        }
        coEvery { bitriseDataSource.getArtifact("buildSlug", "artifactSlug") } returns Result.success(artifactDataJson)
        coEvery { bitriseDataSource.getTestApkTestMethods("apk-url") } returns Result.failure(RuntimeException())

        interactor.execute(applicationCall, "buildSlug", "artifactSlug")

        coVerifySequence {
            bitriseDataSource.getArtifact("buildSlug", "artifactSlug")
            bitriseDataSource.getTestApkTestMethods("apk-url")
        }
    }

    @Test
    fun givenArtifactAndTestApkMethodsSucceedAndMappingFails_whenExecute_thenDoNotRespond() = runBlocking {
        val artifactDataJson = JsonObject().apply {
            add("data", JsonObject().apply {
                addProperty("expiring_download_url", "apk-url")
            })
        }
        coEvery { bitriseDataSource.getArtifact("buildSlug", "artifactSlug") } returns Result.success(artifactDataJson)
        coEvery { bitriseDataSource.getTestApkTestMethods("apk-url") } returns Result.success(emptyList())

        coEvery { testApkMetadataMapper.mapTestApkMetadata(emptyList()) } throws RuntimeException()

        interactor.execute(applicationCall, "buildSlug", "artifactSlug")

        coVerifySequence {
            bitriseDataSource.getArtifact("buildSlug", "artifactSlug")
            bitriseDataSource.getTestApkTestMethods("apk-url")
            testApkMetadataMapper.mapTestApkMetadata(emptyList())
        }
    }
}
