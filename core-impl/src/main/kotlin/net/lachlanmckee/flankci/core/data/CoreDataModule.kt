package net.lachlanmckee.flankci.core.data

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
import net.lachlanmckee.flankci.core.CoreSerializationModule
import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSourceImpl
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSourceImpl
import net.lachlanmckee.flankci.core.data.datasource.remote.CommonService
import net.lachlanmckee.flankci.core.data.datasource.remote.CommonServiceImpl
import net.lachlanmckee.flankci.core.data.mapper.TestSuitesMapper
import net.lachlanmckee.flankci.core.data.mapper.TestSuitesMapperImpl
import javax.inject.Singleton

@Module(includes = [CoreSerializationModule::class, CoreIoModule::class])
internal abstract class CoreDataModule {
  @Binds
  @Singleton
  abstract fun bindConfigDataSource(impl: ConfigDataSourceImpl): ConfigDataSource

  @Binds
  @Singleton
  abstract fun bindCIDataSource(impl: CIDataSourceImpl): CIDataSource

  @Binds
  @Singleton
  abstract fun bindCommonService(impl: CommonServiceImpl): CommonService

  companion object {
    @Provides
    @Singleton
    fun provideHttpClient(
      httpClientFactory: HttpClientFactory,
      typeAdapterFactories: Set<@JvmSuppressWildcards TypeAdapterFactory>
    ): HttpClient {
      return HttpClient(httpClientFactory.engineFactory) {
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
      }
    }

    @Provides
    @Singleton
    fun provideGson(
      typeAdapterFactories: Set<@JvmSuppressWildcards TypeAdapterFactory>
    ): Gson {
      return GsonBuilder()
        .apply {
          typeAdapterFactories.forEach { registerTypeAdapterFactory(it) }
        }
        .create()
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
