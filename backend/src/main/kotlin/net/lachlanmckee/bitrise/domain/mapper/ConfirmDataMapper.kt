package net.lachlanmckee.bitrise.domain.mapper

import io.ktor.http.content.MultiPartData
import net.lachlanmckee.bitrise.domain.entity.ConfirmModel

class ConfirmDataMapper {
    suspend fun mapToConfirmModel(multipart: MultiPartData): Result<ConfirmModel> = kotlin.runCatching {
        var branch: String? = null
        var flankConfigBase64: String? = null

        FormDataCollector.collectData(multipart) { name, value ->
            when (name) {
                "branch" -> branch = value
                "yaml-base64" -> flankConfigBase64 = value
            }
        }

        ConfirmModel(
            branch = requireNotNull(branch) { "Branch must exist" },
            flankConfigBase64 = requireNotNull(flankConfigBase64) { "Flank Base64 must exist" }
        )
    }
}
