package net.lachlanmckee.flankci.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.http.content.MultiPartData
import net.lachlanmckee.flankci.core.domain.ktor.MultipartCallFactory

class ImmediateMultipartCallFactory : MultipartCallFactory {
  override suspend fun handleMultipart(call: ApplicationCall, multiPartDataFunc: suspend (MultiPartData) -> Unit) {
    multiPartDataFunc(MultiPartData.Empty)
  }
}
