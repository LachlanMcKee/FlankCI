package net.lachlanmckee.bitrise.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import io.mockk.*
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.bitrise.core.data.entity.BuildsData
import net.lachlanmckee.bitrise.core.data.entity.Config
import net.lachlanmckee.bitrise.core.data.entity.ConfigModel
import net.lachlanmckee.bitrise.core.domain.mapper.BuildsMapper
import net.lachlanmckee.bitrise.data.datasource.local.TestConfigDataSource
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class TriggerBranchesInteractorTest {
  private val ciDataSource: CIDataSource = mockk()
  private val buildsMapper: BuildsMapper = mockk()
  private val interactor = TriggerBranchesInteractor(
    ciDataSource,
    TestConfigDataSource(
      Config(
        configModel = ConfigModel(
          bitrise = ConfigModel.Bitrise(
            appId = "",
            testApkSourceWorkflow = "source-workflow",
            testTriggerWorkflow = ""
          ),
          testData = mockk()
        ),
        secretProperties = mockk()
      )
    ),
    buildsMapper
  )

  private val applicationCall: ApplicationCall = mockk()

  @AfterEach
  fun verifyNoMoreInteractions() {
    confirmVerified(ciDataSource, buildsMapper, applicationCall)
  }

  @Test
  fun givenGetBuildsSuccessAndMappingSuccess_whenExecute_thenRespond() = runBlocking {
    coEvery { ciDataSource.getBuilds("source-workflow") } returns Result.success(emptyList())

    val buildsData = BuildsData(emptyList(), emptyMap())
    every { buildsMapper.mapBuilds(emptyList()) } returns buildsData

    interactor.execute(applicationCall)

    coVerifySequence {
      ciDataSource.getBuilds("source-workflow")
      buildsMapper.mapBuilds(emptyList())
      applicationCall.respond(buildsData)
    }
  }

  @Test
  fun givenGetBuildsSuccessAndMappingFailure_whenExecute_thenDoNotRespond() = runBlocking {
    coEvery { ciDataSource.getBuilds("source-workflow") } returns Result.success(emptyList())
    every { buildsMapper.mapBuilds(emptyList()) } throws RuntimeException()

    interactor.execute(applicationCall)

    coVerifySequence {
      ciDataSource.getBuilds("source-workflow")
      buildsMapper.mapBuilds(emptyList())
    }
  }

  @Test
  fun givenGetBuildsFailure_whenExecute_thenDoNotRespond() = runBlocking {
    coEvery { ciDataSource.getBuilds("source-workflow") } returns Result.failure(RuntimeException())

    interactor.execute(applicationCall)

    coVerifySequence {
      ciDataSource.getBuilds("source-workflow")
    }
  }
}
