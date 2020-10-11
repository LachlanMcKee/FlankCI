package net.lachlanmckee.bitrise.runner.integration

import com.google.gson.TypeAdapterFactory
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
interface RunnerTestComponent {
  fun routeProviders(): Set<RouteProvider>
  fun typeAdapterFactories(): Set<@JvmSuppressWildcards TypeAdapterFactory>
}
