package net.lachlanmckee.flankci.presentation

import com.google.gson.TypeAdapterFactory
import dagger.Component
import net.lachlanmckee.flankci.core.presentation.CorePresentationModule
import net.lachlanmckee.flankci.core.presentation.RouteProvider
import net.lachlanmckee.flankci.results.presentation.TestResultsPresentationModule
import net.lachlanmckee.flankci.runner.presentation.TestRunnerPresentationModule
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    BackendPresentationModule::class,
    CorePresentationModule::class,
    TestRunnerPresentationModule::class,
    TestResultsPresentationModule::class
  ]
)
interface ApplicationComponent {
  fun routeProviders(): Set<RouteProvider>
  fun typeAdapterFactories(): Set<@JvmSuppressWildcards TypeAdapterFactory>
}
