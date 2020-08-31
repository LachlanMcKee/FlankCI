package net.lachlanmckee.bitrise.results.domain.interactor

import io.ktor.application.ApplicationCall
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.core.data.entity.BuildsData
import net.lachlanmckee.bitrise.core.domain.mapper.BuildsMapper

internal class TestResultsListInteractor(
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
