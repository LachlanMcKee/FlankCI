package net.lachlanmckee.flankci.runner.domain.interactor

import io.ktor.application.ApplicationCall
import io.ktor.response.respond
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSource
import javax.inject.Inject

internal class ArtifactsInteractor @Inject constructor(
  private val ciDataSource: CIDataSource
) {
  suspend fun execute(call: ApplicationCall, buildSlug: String) {
    println("Fetching artifact data for build[$buildSlug]")

    ciDataSource
      .getArtifactDetails(buildSlug)
      .onSuccess {
        println(it)
        call.respond(it)
      }
      .onFailure { println("Failure: $it") }
  }
}
