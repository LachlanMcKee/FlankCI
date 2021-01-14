package net.lachlanmckee.flankci.core.data.datasource.local

import com.google.gson.Gson
import net.lachlanmckee.flankci.core.data.FileReader
import net.lachlanmckee.flankci.core.data.entity.Config
import net.lachlanmckee.flankci.core.data.entity.ConfigModel
import java.util.*
import javax.inject.Inject

internal class ConfigDataSourceImpl @Inject constructor(
  gson: Gson,
  fileReader: FileReader
) : ConfigDataSource {
  private val config: Config by lazy {
    Config(
      configModel = gson.fromJson(fileReader.read("config.json"), ConfigModel::class.java),
      secretProperties = Properties().also {
        it.load(fileReader.read("secrets.properties"))
      }
    )
  }

  override suspend fun getConfig(): Config {
    return config
  }
}
