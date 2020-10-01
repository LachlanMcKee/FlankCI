package net.lachlanmckee.bitrise.data.datasource.local

import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.entity.Config

class TestConfigDataSource(private val config: Config) : ConfigDataSource {
  override suspend fun getConfig() = config
}
