package net.lachlanmckee.bitrise.domain.ktor

import io.ktor.application.ApplicationCall
import io.ktor.http.content.MultiPartData
import io.ktor.request.isMultipart
import io.ktor.request.receiveMultipart
import io.ktor.response.respondTextWriter

suspend inline fun ApplicationCall.handleMultipart(multiPartDataFunc: (MultiPartData) -> Unit) {
    val multipart = receiveMultipart()
    if (request.isMultipart()) {
        multiPartDataFunc(multipart)
    } else {
        respondTextWriter {
            appendln("Request was not made by a form submission")
        }
    }
}
