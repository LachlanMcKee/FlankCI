package net.lachlanmckee.flankci.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.flankci.core.data.entity.BuildType
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.core.domain.mapper.BuildsMapper
import javax.inject.Inject

internal class TriggerBranchesInteractor @Inject constructor(
  private val ciDataSource: CIDataSource,
  private val buildsMapper: BuildsMapper
) {
  suspend fun execute(call: ApplicationCall, configurationId: ConfigurationId) {
    ciDataSource
      .getBuilds(configurationId, BuildType.APK_SOURCE)
      .mapCatching(buildsMapper::mapBuilds)
      .onSuccess { call.respond(it) }
      .onFailure { println("Failure: $it") }
  }
}
