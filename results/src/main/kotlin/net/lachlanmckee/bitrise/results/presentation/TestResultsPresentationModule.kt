package net.lachlanmckee.bitrise.results.presentation

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.application.*
import io.ktor.routing.*
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.core.presentation.RouteProvider
import net.lachlanmckee.bitrise.results.domain.TestResultsDomainModule
import net.lachlanmckee.bitrise.results.domain.interactor.TestResultInteractor
import net.lachlanmckee.bitrise.results.domain.interactor.TestResultsListInteractor
import javax.inject.Singleton

@Module(includes = [TestResultsDomainModule::class])
object TestResultsPresentationModule {
    @Provides
    @Singleton
    @IntoSet
    internal fun provideTestResultsListRouteProvider(
        testResultsListInteractor: TestResultsListInteractor,
        errorScreenFactory: ErrorScreenFactory
    ): RouteProvider = RouteProvider {
        {
            get("/test-results") {
                TestResultsListScreen(
                    testResultsListInteractor,
                    errorScreenFactory
                ).respondHtml(call)
            }
        }
    }

    @Provides
    @Singleton
    @IntoSet
    internal fun provideTestResultRouteProvider(
        testResultInteractor: TestResultInteractor,
        errorScreenFactory: ErrorScreenFactory
    ): RouteProvider = RouteProvider {
        {
            get("/test-results/{build-slug}") {
                val buildSlug: String = call.parameters["build-slug"]!!
                TestResultScreen(
                    testResultInteractor,
                    errorScreenFactory
                ).respondHtml(call, buildSlug)
            }
        }
    }
}
