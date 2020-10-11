package net.lachlanmckee.bitrise.core.data

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CoreIoModule(
  private val httpClientFactory: HttpClientFactory,
  private val fileReader: FileReader
) {
  @Provides
  @Singleton
  fun provideHttpClientFactory(): HttpClientFactory {
    return httpClientFactory
  }

  @Provides
  @Singleton
  fun provideFileReader(): FileReader {
    return fileReader
  }
}
