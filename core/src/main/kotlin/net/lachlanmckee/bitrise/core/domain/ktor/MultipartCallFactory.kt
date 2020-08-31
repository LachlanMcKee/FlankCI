package net.lachlanmckee.bitrise.core.domain.ktor

import io.ktor.application.ApplicationCall
import io.ktor.http.content.MultiPartData
import io.ktor.request.isMultipart
import io.ktor.request.receiveMultipart
import io.ktor.response.respondTextWriter

interface MultipartCallFactory {
    suspend fun handleMultipart(call: ApplicationCall, multiPartDataFunc: suspend (MultiPartData) -> Unit)
}

internal class MultipartCallFactoryImpl : MultipartCallFactory {
    override suspend fun handleMultipart(call: ApplicationCall, multiPartDataFunc: suspend (MultiPartData) -> Unit) {
        val multipart = call.receiveMultipart()
        if (call.request.isMultipart()) {
            multiPartDataFunc(multipart)
        } else {
            call.respondTextWriter {
                appendln("Request was not made by a form submission")
            }
        }
    }
}
