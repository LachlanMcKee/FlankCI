package net.lachlanmckee.bitrise.presentation

import com.google.gson.TypeAdapterFactory
import dagger.Component
import net.lachlanmckee.bitrise.core.presentation.CorePresentationModule
import net.lachlanmckee.bitrise.core.presentation.RouteProvider
import net.lachlanmckee.bitrise.results.presentation.TestResultsPresentationModule
import net.lachlanmckee.bitrise.runner.presentation.TestRunnerPresentationModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CorePresentationModule::class,
        TestRunnerPresentationModule::class,
        TestResultsPresentationModule::class
    ]
)
interface ApplicationComponent {
    fun routeProviders(): Set<RouteProvider>
    fun typeAdapterFactories(): Set<@JvmSuppressWildcards TypeAdapterFactory>
}
