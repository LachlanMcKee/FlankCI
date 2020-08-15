package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.domain.mapper.ConfirmDataMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class WorkflowTriggerInteractorTest {
    private val bitriseDataSource: BitriseDataSource = mockk()
    private val confirmDataMapper: ConfirmDataMapper = mockk()
    private val interactor: WorkflowTriggerInteractor =
        WorkflowTriggerInteractor(ImmediateMultipartCallFactory(), bitriseDataSource, confirmDataMapper)

    private val applicationCall: ApplicationCall = mockk()

    @AfterEach
    fun verifyNoMoreInteractions() {
        confirmVerified(bitriseDataSource, confirmDataMapper, applicationCall)
    }

    @Test
    fun foo() = runBlocking {
        // TODO
    }
}
