package net.lachlanmckee.bitrise.core.data

import gsonpath.GsonPath
import gsonpath.GsonPathTypeAdapterFactoryKt
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSourceImpl
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSourceImpl
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseService
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseServiceImpl
import net.lachlanmckee.bitrise.core.data.serialization.BitriseGsonTypeFactory

object CoreDataDi {
    val bitriseService: BitriseService by lazy {
        BitriseServiceImpl(
            client = HttpClient(Apache) {
                install(JsonFeature) {
                    serializer = GsonSerializer {
                        serializeNulls()
                        setLenient()
                        registerTypeAdapterFactory(GsonPathTypeAdapterFactoryKt())
                        registerTypeAdapterFactory(
                            GsonPath.createTypeAdapterFactory(BitriseGsonTypeFactory::class.java)
                        )
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

    val bitriseDataSource: BitriseDataSource by lazy {
        BitriseDataSourceImpl(bitriseService, configDataSource)
    }

    val configDataSource: ConfigDataSource by lazy {
        ConfigDataSourceImpl()
    }
}
