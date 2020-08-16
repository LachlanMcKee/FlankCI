package net.lachlanmckee.bitrise.data.datasource.local

import net.lachlanmckee.bitrise.data.entity.Config

class TestConfigDataSource(private val config: Config) : ConfigDataSource {
    override suspend fun getConfig() = config
}
