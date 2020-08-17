package net.lachlanmckee.bitrise.domain.mapper

import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData

interface FormDataCollector {
    suspend fun collectData(multipart: MultiPartData, func: (String, String) -> Unit)
}

class FormDataCollectorImpl : FormDataCollector {
    override suspend fun collectData(multipart: MultiPartData, func: (String, String) -> Unit) {
        loop@ while (true) {
            val part = multipart.readPart() ?: break
            when (part) {
                is PartData.FormItem -> {
                    val name = part.name ?: continue@loop
                    func(name, part.value)
                }
            }
            part.dispose()
        }
    }
}
