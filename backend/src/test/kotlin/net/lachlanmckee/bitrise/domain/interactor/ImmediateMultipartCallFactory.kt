package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.http.content.MultiPartData
import io.mockk.mockk
import net.lachlanmckee.bitrise.domain.ktor.MultipartCallFactory

class ImmediateMultipartCallFactory : MultipartCallFactory {
    override suspend fun handleMultipart(call: ApplicationCall, multiPartDataFunc: suspend (MultiPartData) -> Unit) {
        multiPartDataFunc(mockk())
    }
}
