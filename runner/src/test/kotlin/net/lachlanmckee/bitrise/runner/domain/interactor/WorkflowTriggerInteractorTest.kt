package net.lachlanmckee.bitrise.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respondRedirect
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.bitrise.core.data.entity.WorkflowTriggerData
import net.lachlanmckee.bitrise.core.data.entity.generic.TriggerResponse
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.domain.interactor.ImmediateMultipartCallFactory
import net.lachlanmckee.bitrise.runner.domain.entity.ConfirmModel
import net.lachlanmckee.bitrise.runner.domain.mapper.ConfirmDataMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class WorkflowTriggerInteractorTest {
  private val errorScreenFactory: ErrorScreenFactory = mockk()
  private val ciDataSource: CIDataSource = mockk()
  private val confirmDataMapper: ConfirmDataMapper = mockk()
  private val interactor: WorkflowTriggerInteractor =
    WorkflowTriggerInteractor(
      ImmediateMultipartCallFactory(),
      errorScreenFactory,
      ciDataSource,
      confirmDataMapper
    )

  private val applicationCall: ApplicationCall = mockk()

  @AfterEach
  fun verifyNoMoreInteractions() {
    confirmVerified(errorScreenFactory, ciDataSource, confirmDataMapper, applicationCall)
  }

  @Test
  fun givenConfirmDataMapperFails_whenExecute_thenRespondErrorHtml() = runBlocking {
    givenMapToConfirmModelResult(Result.failure(RuntimeException("Reason")))

    interactor.execute(applicationCall)

    coVerifySequence {
      confirmDataMapper.mapToConfirmModel(any())
      errorScreenFactory.respondHtml(
        applicationCall,
        "Error",
        "Reason"
      )
    }
  }

  @Test
  fun givenConfirmDataMapperSuccessAndTriggerFails_whenExecute_thenRespondErrorHtml() = runBlocking {
    givenMapToConfirmModelResult(
      Result.success(
        ConfirmModel(
          branch = "branch",
          buildSlug = "slug",
          commitHash = "hash",
          jobName = "job",
          flankConfigBase64 = "config"
        )
      )
    )
    givenTriggerWorkflowResult(Result.failure(RuntimeException("Reason")))

    interactor.execute(applicationCall)

    coVerifySequence {
      confirmDataMapper.mapToConfirmModel(any())
      ciDataSource.triggerWorkflow(
        WorkflowTriggerData(
          branch = "branch",
          buildSlug = "slug",
          commitHash = "hash",
          jobName = "job",
          flankConfigBase64 = "config"
        )
      )
      errorScreenFactory.respondHtml(
        applicationCall,
        "Error",
        "Failed to submit build. Message: Reason"
      )
    }
  }

  @Test
  fun givenConfirmDataMapperSuccessAndTriggerSuccessWithoutOkStatus_whenExecute_thenRespondErrorHtml() = runBlocking {
    givenMapToConfirmModelResult(
      Result.success(
        ConfirmModel(
          branch = "branch",
          buildSlug = "slug",
          commitHash = "hash",
          jobName = "job",
          flankConfigBase64 = "config"
        )
      )
    )
    givenTriggerWorkflowResult(Result.success(TriggerResponse("not-ok", "url")))

    interactor.execute(applicationCall)

    coVerifySequence {
      confirmDataMapper.mapToConfirmModel(any())
      ciDataSource.triggerWorkflow(
        WorkflowTriggerData(
          branch = "branch",
          buildSlug = "slug",
          commitHash = "hash",
          jobName = "job",
          flankConfigBase64 = "config"
        )
      )
      errorScreenFactory.respondHtml(
        applicationCall,
        "Error",
        "Bitrise rejected build"
      )
    }
  }

  @Test
  fun givenConfirmDataMapperSuccessAndTriggerSuccessWithOkStatus_whenExecute_thenRespondRedirect() = runBlocking {
    givenMapToConfirmModelResult(
      Result.success(
        ConfirmModel(
          branch = "branch",
          buildSlug = "slug",
          commitHash = "hash",
          jobName = "job",
          flankConfigBase64 = "config"
        )
      )
    )
    givenTriggerWorkflowResult(Result.success(TriggerResponse("ok", "url")))

    interactor.execute(applicationCall)

    coVerifySequence {
      confirmDataMapper.mapToConfirmModel(any())
      ciDataSource.triggerWorkflow(
        WorkflowTriggerData(
          branch = "branch",
          buildSlug = "slug",
          commitHash = "hash",
          jobName = "job",
          flankConfigBase64 = "config"
        )
      )
      applicationCall.respondRedirect("url")
    }
  }

  private fun givenMapToConfirmModelResult(result: Result<ConfirmModel>) {
    coEvery { confirmDataMapper.mapToConfirmModel(any()) } returns result
  }

  private fun givenTriggerWorkflowResult(result: Result<TriggerResponse>) {
    coEvery {
      ciDataSource.triggerWorkflow(
        WorkflowTriggerData(
          branch = "branch",
          buildSlug = "slug",
          commitHash = "hash",
          jobName = "job",
          flankConfigBase64 = "config"
        )
      )
    } returns result
  }
}
