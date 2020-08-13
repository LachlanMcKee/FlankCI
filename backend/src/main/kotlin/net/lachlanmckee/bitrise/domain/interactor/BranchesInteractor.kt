package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.header
import io.ktor.response.respond
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.domain.mapper.BuildsMapper

class BranchesInteractor(
    private val bitriseDataSource: BitriseDataSource,
    private val buildsMapper: BuildsMapper
) {
    suspend fun execute(call: ApplicationCall) {
        call.response.header("Access-Control-Allow-Origin", "*")

        bitriseDataSource
            .getBuilds()
            .mapCatching(buildsMapper::mapBuilds)
            .onSuccess { call.respond(it) }
            .onFailure { println("Failure: $it") }
    }
}
