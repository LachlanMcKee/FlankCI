package net.lachlanmckee.bitrise.data.datasource.local

import com.google.gson.GsonBuilder
import gsonpath.GsonPath
import gsonpath.GsonPathTypeAdapterFactoryKt
import net.lachlanmckee.bitrise.data.entity.Config
import net.lachlanmckee.bitrise.data.entity.ConfigModel
import net.lachlanmckee.bitrise.data.serialization.BitriseGsonTypeFactory
import java.io.FileInputStream
import java.util.*

interface ConfigDataSource {
    suspend fun getConfig(): Config
}

class ConfigDataSourceImpl : ConfigDataSource {
    private val config: Config by lazy {
        Config(
            configModel = GsonBuilder()
                .registerTypeAdapterFactory(GsonPathTypeAdapterFactoryKt())
                .registerTypeAdapterFactory(GsonPath.createTypeAdapterFactory(BitriseGsonTypeFactory::class.java))
                .create()
                .fromJson(FileInputStream("config.json").bufferedReader(), ConfigModel::class.java),
            secretProperties = Properties().also {
                it.load(FileInputStream("secrets.properties").bufferedReader())
            }
        )
    }

    override suspend fun getConfig(): Config {
        return config
    }
}


