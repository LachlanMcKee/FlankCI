package net.lachlanmckee.bitrise.core.data

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CoreHttpModule(private val httpClientFactory: HttpClientFactory) {
  @Provides
  @Singleton
  fun provideHttpClientFactory(): HttpClientFactory {
    return httpClientFactory
  }
}
