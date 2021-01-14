package net.lachlanmckee.flankci.core.data.datasource.local

import net.lachlanmckee.flankci.core.data.entity.Config

interface ConfigDataSource {
  suspend fun getConfig(): Config
}
