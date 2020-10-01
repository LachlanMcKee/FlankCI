package net.lachlanmckee.bitrise.domain.mapper

import io.ktor.http.content.MultiPartData
import net.lachlanmckee.bitrise.core.domain.mapper.FormDataCollector

class ImmediateFormDataCollector(
  private val dataToEmit: List<Pair<String, String>>
) : FormDataCollector {
  override suspend fun collectData(multipart: MultiPartData, func: (String, String) -> Unit) {
    dataToEmit.forEach { data ->
      func(data.first, data.second)
    }
  }
}
