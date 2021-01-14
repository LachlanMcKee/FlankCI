package net.lachlanmckee.flankci.data.datasource.local

import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.data.entity.Config

class TestConfigDataSource(private val config: Config) : ConfigDataSource {
  override suspend fun getConfig() = config
}
