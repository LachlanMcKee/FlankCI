package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respondRedirect
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.data.entity.BitriseTriggerResponse
import net.lachlanmckee.bitrise.domain.entity.ConfirmModel
import net.lachlanmckee.bitrise.domain.mapper.ConfirmDataMapper
import net.lachlanmckee.bitrise.presentation.ErrorScreenFactory
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class WorkflowTriggerInteractorTest {
    private val errorScreenFactory: ErrorScreenFactory = mockk()
    private val bitriseDataSource: BitriseDataSource = mockk()
    private val confirmDataMapper: ConfirmDataMapper = mockk()
    private val interactor: WorkflowTriggerInteractor =
        WorkflowTriggerInteractor(
            ImmediateMultipartCallFactory(),
            errorScreenFactory,
            bitriseDataSource,
            confirmDataMapper
        )

    private val applicationCall: ApplicationCall = mockk()

    @AfterEach
    fun verifyNoMoreInteractions() {
        confirmVerified(errorScreenFactory, bitriseDataSource, confirmDataMapper, applicationCall)
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
        givenMapToConfirmModelResult(Result.success(
            ConfirmModel(
                branch = "branch",
                commitHash = "hash",
                jobName = "job",
                flankConfigBase64 = "config"
            )
        ))
        givenTriggerWorkflowResult(Result.failure(RuntimeException("Reason")))

        interactor.execute(applicationCall)

        coVerifySequence {
            confirmDataMapper.mapToConfirmModel(any())
            bitriseDataSource.triggerWorkflow(
                branch = "branch",
                commitHash = "hash",
                jobName = "job",
                flankConfigBase64 = "config"
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
        givenMapToConfirmModelResult(Result.success(
            ConfirmModel(
                branch = "branch",
                commitHash = "hash",
                jobName = "job",
                flankConfigBase64 = "config"
            )
        ))
        givenTriggerWorkflowResult(Result.success(BitriseTriggerResponse("not-ok", "url")))

        interactor.execute(applicationCall)

        coVerifySequence {
            confirmDataMapper.mapToConfirmModel(any())
            bitriseDataSource.triggerWorkflow(
                branch = "branch",
                commitHash = "hash",
                jobName = "job",
                flankConfigBase64 = "config"
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
        givenMapToConfirmModelResult(Result.success(
            ConfirmModel(
                branch = "branch",
                commitHash = "hash",
                jobName = "job",
                flankConfigBase64 = "config"
            )
        ))
        givenTriggerWorkflowResult(Result.success(BitriseTriggerResponse("ok", "url")))

        interactor.execute(applicationCall)

        coVerifySequence {
            confirmDataMapper.mapToConfirmModel(any())
            bitriseDataSource.triggerWorkflow(
                branch = "branch",
                commitHash = "hash",
                jobName = "job",
                flankConfigBase64 = "config"
            )
            applicationCall.respondRedirect("url")
        }
    }

    private fun givenMapToConfirmModelResult(result: Result<ConfirmModel>) {
        coEvery { confirmDataMapper.mapToConfirmModel(any()) } returns result
    }

    private fun givenTriggerWorkflowResult(result: Result<BitriseTriggerResponse>) {
        coEvery {
            bitriseDataSource.triggerWorkflow(
                branch = "branch",
                commitHash = "hash",
                jobName = "job",
                flankConfigBase64 = "config"
            )
        } returns result
    }
}
