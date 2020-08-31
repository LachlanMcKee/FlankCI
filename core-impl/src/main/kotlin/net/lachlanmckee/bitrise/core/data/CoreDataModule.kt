package net.lachlanmckee.bitrise.core.data

import com.google.gson.TypeAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import net.lachlanmckee.bitrise.core.CoreSerializationModule
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSourceImpl
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSourceImpl
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseService
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseServiceImpl
import javax.inject.Singleton

@Module(includes = [CoreSerializationModule::class])
internal abstract class CoreDataModule {
    @Binds
    @Singleton
    abstract fun bindConfigDataSource(impl: ConfigDataSourceImpl): ConfigDataSource

    @Binds
    @Singleton
    abstract fun bindBitriseDataSource(impl: BitriseDataSourceImpl): BitriseDataSource

    companion object {
        @Provides
        @Singleton
        fun provideBitriseService(
            configDataSource: ConfigDataSource,
            typeAdapterFactories: Set<@JvmSuppressWildcards TypeAdapterFactory>
        ): BitriseService {
            return BitriseServiceImpl(
                client = HttpClient(Apache) {
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
                },
                configDataSource = configDataSource
            )
        }
    }
}
