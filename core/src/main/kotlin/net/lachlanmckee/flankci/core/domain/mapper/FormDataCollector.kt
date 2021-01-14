package net.lachlanmckee.flankci.core.domain.mapper

import io.ktor.http.content.MultiPartData

interface FormDataCollector {
  suspend fun collectData(multipart: MultiPartData, func: (String, String) -> Unit)
}
