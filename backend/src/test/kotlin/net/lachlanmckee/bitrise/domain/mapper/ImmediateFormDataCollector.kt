package net.lachlanmckee.bitrise.domain.mapper

import io.ktor.http.content.MultiPartData

class ImmediateFormDataCollector(
    private val dataToEmit: List<Pair<String, String>>
): FormDataCollector {
    override suspend fun collectData(multipart: MultiPartData, func: (String, String) -> Unit) {
        dataToEmit.forEach { data ->
            func(data.first, data.second)
        }
    }

}
