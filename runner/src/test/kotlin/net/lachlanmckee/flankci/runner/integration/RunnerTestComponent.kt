package net.lachlanmckee.flankci.runner.integration

import com.google.gson.TypeAdapterFactory
import dagger.Component
import net.lachlanmckee.flankci.core.presentation.CorePresentationModule
import net.lachlanmckee.flankci.core.presentation.RouteProvider
import net.lachlanmckee.flankci.runner.presentation.TestRunnerPresentationModule
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
