package net.lachlanmckee.bitrise.core.domain.ktor

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MultipartCallFactoryImplTest {
    private val applicationCall = mockk<ApplicationCall>()
    private val multiPartDataFunc = mockk<suspend (MultiPartData) -> Unit>()

    private val multipart = mockk<MultiPartData>()
    private val request = mockk<ApplicationRequest>()

    @BeforeEach
    fun setup() {
        mockkStatic("io.ktor.request.ApplicationReceiveFunctionsKt")
        mockkStatic("io.ktor.request.ApplicationRequestPropertiesKt")

        every { applicationCall.request } returns request
        coEvery { applicationCall.receiveMultipart() } returns multipart
    }

    @AfterEach
    fun verifyNoMoreInteractions() {
        confirmVerified(applicationCall, multiPartDataFunc)
    }

    @Test
    fun givenRequestIsMultipart_whenHandle_thenInvokeFunction() = runBlocking {
        every { request.isMultipart() } returns true

        MultipartCallFactoryImpl().handleMultipart(applicationCall, multiPartDataFunc)

        coVerifySequence {
            applicationCall.receiveMultipart()
            applicationCall.request
            request.isMultipart()
            multiPartDataFunc(multipart)
        }
    }

    @Test
    fun givenRequestIsNotMultipart_whenHandle_thenEmitErrorMessage() = runBlocking {
        mockkStatic("io.ktor.response.ApplicationResponseFunctionsKt")
        every { request.isMultipart() } returns false
        coJustRun { applicationCall.respondText(any()) }

        MultipartCallFactoryImpl().handleMultipart(applicationCall, multiPartDataFunc)

        coVerifySequence {
            applicationCall.receiveMultipart()
            applicationCall.request
            request.isMultipart()
            applicationCall.respondText("Request was not made by a form submission")
        }
    }
}
