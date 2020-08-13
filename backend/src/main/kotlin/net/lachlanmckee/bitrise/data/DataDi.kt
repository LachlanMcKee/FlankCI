package net.lachlanmckee.bitrise.data

import gsonpath.GsonPath
import gsonpath.GsonPathTypeAdapterFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSourceImpl
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseDataSourceImpl
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseService
import net.lachlanmckee.bitrise.data.datasource.remote.BitriseServiceImpl
import net.lachlanmckee.bitrise.data.serialization.BitriseGsonTypeFactory


class DataDi {
    private val bitriseService: BitriseService by lazy {
        BitriseServiceImpl(
            client = HttpClient(Apache) {
                install(JsonFeature) {
                    serializer = GsonSerializer {
                        serializeNulls()
                        setLenient()
                        registerTypeAdapterFactory(GsonPathTypeAdapterFactory())
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
