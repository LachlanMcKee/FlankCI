package net.lachlanmckee.bitrise.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import javax.inject.Inject

internal class ArtifactsInteractor @Inject constructor(
    private val bitriseDataSource: BitriseDataSource
) {
    suspend fun execute(call: ApplicationCall, buildSlug: String) {
        println("Fetching artifact data for build[$buildSlug]")

        bitriseDataSource
            .getArtifactDetails(buildSlug)
            .onSuccess {
                println(it)
                call.respond(it)
            }
            .onFailure { println("Failure: $it") }
    }
}
