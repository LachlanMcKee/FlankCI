package net.lachlanmckee.bitrise.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.core.domain.mapper.BuildsMapper
import javax.inject.Inject

internal class TriggerBranchesInteractor @Inject constructor(
  private val bitriseDataSource: BitriseDataSource,
  private val configDataSource: ConfigDataSource,
  private val buildsMapper: BuildsMapper
) {
  suspend fun execute(call: ApplicationCall) {
    bitriseDataSource
      .getBuilds(configDataSource.getConfig().bitrise.testApkSourceWorkflow)
      .mapCatching(buildsMapper::mapBuilds)
      .onSuccess { call.respond(it) }
      .onFailure { println("Failure: $it") }
  }
}
