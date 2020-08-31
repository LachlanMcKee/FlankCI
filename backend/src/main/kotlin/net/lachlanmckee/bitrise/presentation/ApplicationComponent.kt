package net.lachlanmckee.bitrise.presentation

import dagger.Component
import net.lachlanmckee.bitrise.core.presentation.CorePresentationModule
import net.lachlanmckee.bitrise.core.presentation.RouteProvider
import net.lachlanmckee.bitrise.runner.presentation.TestRunnerPresentationModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CorePresentationModule::class,
        TestRunnerPresentationModule::class
    ]
)
interface ApplicationComponent {
    fun routeProviders(): Set<RouteProvider>
}
