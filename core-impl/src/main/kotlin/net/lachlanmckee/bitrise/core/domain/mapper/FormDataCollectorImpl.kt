package net.lachlanmckee.bitrise.core.domain.mapper

import io.ktor.http.content.*
import javax.inject.Inject

internal class FormDataCollectorImpl @Inject constructor() : FormDataCollector {
    override suspend fun collectData(multipart: MultiPartData, func: (String, String) -> Unit) {
        loop@ while (true) {
            val part = multipart.readPart() ?: break
            if (part is PartData.FormItem) {
                part.name?.let { name -> func(name, part.value) }
            }
            part.dispose()
        }
    }
}
