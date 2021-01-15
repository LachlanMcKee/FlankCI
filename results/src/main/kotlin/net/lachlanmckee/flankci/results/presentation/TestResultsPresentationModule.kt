package net.lachlanmckee.flankci.results.presentation

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.Routing
import io.ktor.routing.get
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.core.presentation.ErrorScreenFactory
import net.lachlanmckee.flankci.core.presentation.RouteProvider
import net.lachlanmckee.flankci.results.domain.TestResultsDomainModule
import net.lachlanmckee.flankci.results.domain.interactor.TestResultInteractor
import net.lachlanmckee.flankci.results.domain.interactor.TestResultsListInteractor
import javax.inject.Singleton

@Module(includes = [TestResultsDomainModule::class])
object TestResultsPresentationModule {
  @Provides
  @Singleton
  @IntoSet
  internal fun provideJavascriptRouteProvider(): RouteProvider = object : RouteProvider {
    override fun provideRoute(): Routing.() -> Unit = {
      static("/static") {
        resource("test-result-script.js")
      }
    }
  }

  @Provides
  @Singleton
  @IntoSet
  internal fun provideTestResultsListRouteProvider(
    testResultsListInteractor: TestResultsListInteractor,
    errorScreenFactory: ErrorScreenFactory
  ): RouteProvider = object : RouteProvider {
    override fun provideRoute(): Routing.() -> Unit = {
      get("/{configuration-id}/test-results") {
        TestResultsListScreen(
          testResultsListInteractor,
          errorScreenFactory
        ).respondHtml(call, call.getConfigurationId())
      }
    }
  }

  @Provides
  @Singleton
  @IntoSet
  internal fun provideTestResultRouteProvider(
    testResultInteractor: TestResultInteractor,
    errorScreenFactory: ErrorScreenFactory
  ): RouteProvider = object : RouteProvider {
    override fun provideRoute(): Routing.() -> Unit = {
      get("/{configuration-id}/test-results/{build-slug}") {
        val buildSlug: String = call.parameters["build-slug"]!!
        TestResultScreen(
          testResultInteractor,
          errorScreenFactory
        ).respondHtml(call, call.getConfigurationId(), buildSlug)
      }
    }
  }

  private fun ApplicationCall.getConfigurationId(): ConfigurationId {
    return ConfigurationId(parameters["configuration-id"]!!)
  }
}
