package net.lachlanmckee.bitrise.core.data

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.TypeAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import net.lachlanmckee.bitrise.core.CoreSerializationModule
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSourceImpl
import net.lachlanmckee.bitrise.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.bitrise.core.data.datasource.remote.CIDataSourceImpl
import net.lachlanmckee.bitrise.core.data.datasource.remote.CIService
import net.lachlanmckee.bitrise.core.data.datasource.remote.CIServiceImpl
import net.lachlanmckee.bitrise.core.data.mapper.TestSuitesMapper
import net.lachlanmckee.bitrise.core.data.mapper.TestSuitesMapperImpl
import javax.inject.Singleton

@Module(includes = [CoreSerializationModule::class, CoreIoModule::class])
internal abstract class CoreDataModule {
  @Binds
  @Singleton
  abstract fun bindConfigDataSource(impl: ConfigDataSourceImpl): ConfigDataSource

  @Binds
  @Singleton
  abstract fun bindCIDataSource(impl: CIDataSourceImpl): CIDataSource

  companion object {
    @Provides
    @Singleton
    fun provideBitriseService(
      configDataSource: ConfigDataSource,
      httpClientFactory: HttpClientFactory,
      typeAdapterFactories: Set<@JvmSuppressWildcards TypeAdapterFactory>
    ): CIService {
      return CIServiceImpl(
        client = HttpClient(httpClientFactory.engineFactory) {
          install(JsonFeature) {
            serializer = GsonSerializer {
              serializeNulls()
              setLenient()
              typeAdapterFactories.forEach { registerTypeAdapterFactory(it) }
            }
          }
          install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
          }
          httpClientFactory.handleConfig(this)
        },
        configDataSource = configDataSource
      )
    }

    @Provides
    @Singleton
    internal fun provideTestSuitesMapper(): TestSuitesMapper {
      return TestSuitesMapperImpl(
        xmlMapper = XmlMapper.Builder(XmlMapper())
          .defaultUseWrapper(false)
          .build()
          .registerKotlinModule()
      )
    }
  }
}
