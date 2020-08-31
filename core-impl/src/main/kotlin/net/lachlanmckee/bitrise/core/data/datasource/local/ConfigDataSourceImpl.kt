package net.lachlanmckee.bitrise.core.data.datasource.local

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapterFactory
import net.lachlanmckee.bitrise.core.data.entity.Config
import net.lachlanmckee.bitrise.core.data.entity.ConfigModel
import java.io.FileInputStream
import java.util.*
import javax.inject.Inject

internal class ConfigDataSourceImpl @Inject constructor(
    typeAdapterFactories: Set<@JvmSuppressWildcards TypeAdapterFactory>
) : ConfigDataSource {
    private val config: Config by lazy {
        Config(
            configModel = GsonBuilder()
                .apply {
                    typeAdapterFactories.forEach { registerTypeAdapterFactory(it) }
                }
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


