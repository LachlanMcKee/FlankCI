package net.lachlanmckee.bitrise.core.domain.ktor

import io.ktor.application.ApplicationCall
import io.ktor.http.content.MultiPartData

interface MultipartCallFactory {
  suspend fun handleMultipart(call: ApplicationCall, multiPartDataFunc: suspend (MultiPartData) -> Unit)
}
