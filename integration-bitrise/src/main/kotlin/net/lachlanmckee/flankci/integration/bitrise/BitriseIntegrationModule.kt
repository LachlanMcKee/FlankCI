package net.lachlanmckee.flankci.integration.bitrise

import com.google.gson.Gson
import com.google.gson.TypeAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.multibindings.ElementsIntoSet
import gsonpath.GsonPath
import io.ktor.client.*
import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.data.datasource.remote.BitriseCIService
import net.lachlanmckee.flankci.core.data.datasource.remote.CIService
import javax.inject.Singleton

@Module
object BitriseIntegrationModule {
  @Provides
  @Singleton
  internal fun provideCIService(
    configDataSource: ConfigDataSource,
    gson: Gson,
    httpClient: HttpClient
  ): CIService {
    return BitriseCIService(
      client = httpClient,
      configDataSource = configDataSource,
      gson = gson
    )
  }

  @Provides
  @ElementsIntoSet
  internal fun provideGsonTypeAdapterFactories(): Set<@JvmSuppressWildcards TypeAdapterFactory> {
    return setOf(
      GsonPath.createTypeAdapterFactory(BitriseIntegrationGsonTypeFactory::class.java)
    )
  }
}
