package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.header
import io.ktor.response.respond
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource

class ArtifactsInteractor(
    private val bitriseDataSource: BitriseDataSource
) {
    suspend fun execute(call: ApplicationCall, buildSlug: String) {
        println("Fetching artifact data for build[$buildSlug]")

        call.response.header("Access-Control-Allow-Origin", "*")

        bitriseDataSource
            .getArtifactDetails(buildSlug)
            .onSuccess {
                println(it)
                call.respond(it)
            }
            .onFailure { println("Failure: $it") }
    }
}
