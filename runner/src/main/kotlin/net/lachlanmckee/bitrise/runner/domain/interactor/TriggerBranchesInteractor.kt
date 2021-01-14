package net.lachlanmckee.bitrise.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.bitrise.core.domain.mapper.BuildsMapper
import javax.inject.Inject

internal class TriggerBranchesInteractor @Inject constructor(
  private val ciDataSource: CIDataSource,
  private val configDataSource: ConfigDataSource,
  private val buildsMapper: BuildsMapper
) {
  suspend fun execute(call: ApplicationCall) {
    ciDataSource
      .getBuilds(configDataSource.getConfig().bitrise.testApkSourceWorkflow)
      .mapCatching(buildsMapper::mapBuilds)
      .onSuccess { call.respond(it) }
      .onFailure { println("Failure: $it") }
  }
}
