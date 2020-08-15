package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import io.mockk.*
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.data.entity.BuildsData
import net.lachlanmckee.bitrise.domain.mapper.BuildsMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class BranchesInteractorTest {
    private val bitriseDataSource: BitriseDataSource = mockk()
    private val buildsMapper: BuildsMapper = mockk()
    private val interactor = BranchesInteractor(bitriseDataSource, buildsMapper)

    private val applicationCall: ApplicationCall = mockk()

    @AfterEach
    fun verifyNoMoreInteractions() {
        confirmVerified(bitriseDataSource, buildsMapper, applicationCall)
    }

    @Test
    fun givenGetBuildsSuccessAndMappingSuccess_whenExecute_thenRespond() = runBlocking {
        coEvery { bitriseDataSource.getBuilds() } returns Result.success(emptyList())

        val buildsData = BuildsData(emptyList(), emptyMap())
        every { buildsMapper.mapBuilds(emptyList()) } returns buildsData

        interactor.execute(applicationCall)

        coVerifySequence {
            bitriseDataSource.getBuilds()
            buildsMapper.mapBuilds(emptyList())
            applicationCall.respond(buildsData)
        }
    }

    @Test
    fun givenGetBuildsSuccessAndMappingFailure_whenExecute_thenDoNotRespond() = runBlocking {
        coEvery { bitriseDataSource.getBuilds() } returns Result.success(emptyList())
        every { buildsMapper.mapBuilds(emptyList()) } throws RuntimeException()

        interactor.execute(applicationCall)

        coVerifySequence {
            bitriseDataSource.getBuilds()
            buildsMapper.mapBuilds(emptyList())
        }
    }

    @Test
    fun givenGetBuildsFailure_whenExecute_thenDoNotRespond() = runBlocking {
        coEvery { bitriseDataSource.getBuilds() } returns Result.failure(RuntimeException())

        interactor.execute(applicationCall)

        coVerifySequence {
            bitriseDataSource.getBuilds()
        }
    }
}
