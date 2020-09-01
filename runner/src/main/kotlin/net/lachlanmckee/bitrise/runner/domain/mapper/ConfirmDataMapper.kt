package net.lachlanmckee.bitrise.runner.domain.mapper

import io.ktor.http.content.MultiPartData
import net.lachlanmckee.bitrise.core.domain.mapper.FormDataCollector
import net.lachlanmckee.bitrise.runner.domain.entity.ConfirmModel
import javax.inject.Inject

internal class ConfirmDataMapper @Inject constructor(
    private val formDataCollector: FormDataCollector
) {
    suspend fun mapToConfirmModel(multipart: MultiPartData): Result<ConfirmModel> = kotlin.runCatching {
        var branch: String? = null
        var buildSlug: String? = null
        var commitHash: String? = null
        var jobName: String? = null
        var flankConfigBase64: String? = null

        formDataCollector.collectData(multipart) { name, value ->
            when (name) {
                "branch" -> branch = value
                "build-slug" -> buildSlug = value
                "commit-hash" -> commitHash = value
                "job-name" -> jobName = value
                "yaml-base64" -> flankConfigBase64 = value
            }
        }

        ConfirmModel(
            branch = requireNotNull(branch) { "Branch must exist" },
            buildSlug = requireNotNull(buildSlug) { "Build slug must exist" },
            commitHash = requireNotNull(commitHash) { "Commit hash must exist" },
            jobName = requireNotNull(jobName) { "Job name must exist" },
            flankConfigBase64 = requireNotNull(flankConfigBase64) { "Flank Base64 must exist" }
        )
    }
}
