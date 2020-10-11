package net.lachlanmckee.bitrise.results.integration

import com.google.gson.TypeAdapterFactory
import dagger.Component
import net.lachlanmckee.bitrise.core.presentation.CorePresentationModule
import net.lachlanmckee.bitrise.core.presentation.RouteProvider
import net.lachlanmckee.bitrise.results.presentation.TestResultsPresentationModule
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    CorePresentationModule::class,
    TestResultsPresentationModule::class
  ]
)
interface ResultsTestComponent {
  fun routeProviders(): Set<RouteProvider>
  fun typeAdapterFactories(): Set<@JvmSuppressWildcards TypeAdapterFactory>
}
