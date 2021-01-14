package net.lachlanmckee.flankci.results.integration

import com.google.gson.TypeAdapterFactory
import dagger.Component
import net.lachlanmckee.flankci.core.presentation.CorePresentationModule
import net.lachlanmckee.flankci.core.presentation.RouteProvider
import net.lachlanmckee.flankci.integration.bitrise.BitriseIntegrationModule
import net.lachlanmckee.flankci.results.presentation.TestResultsPresentationModule
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    CorePresentationModule::class,
    TestResultsPresentationModule::class,

    // Integrations
    BitriseIntegrationModule::class
  ]
)
interface ResultsTestComponent {
  fun routeProviders(): Set<RouteProvider>
  fun typeAdapterFactories(): Set<@JvmSuppressWildcards TypeAdapterFactory>
}
