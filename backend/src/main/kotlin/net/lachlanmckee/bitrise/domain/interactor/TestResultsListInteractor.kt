package net.lachlanmckee.bitrise.domain.interactor

import io.ktor.application.ApplicationCall
import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.data.entity.BuildsData
import net.lachlanmckee.bitrise.domain.mapper.BuildsMapper

class TestResultsListInteractor(
    private val bitriseDataSource: BitriseDataSource,
    private val configDataSource: ConfigDataSource,
    private val buildsMapper: BuildsMapper
) {
    suspend fun execute(call: ApplicationCall): Result<BuildsData> {
        return bitriseDataSource
            .getBuilds(configDataSource.getConfig().bitrise.testTriggerWorkflow)
            .mapCatching(buildsMapper::mapBuilds)
    }
}
