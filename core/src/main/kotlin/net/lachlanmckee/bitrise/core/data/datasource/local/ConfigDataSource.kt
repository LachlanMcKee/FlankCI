package net.lachlanmckee.bitrise.core.data.datasource.local

import net.lachlanmckee.bitrise.core.data.entity.Config

interface ConfigDataSource {
    suspend fun getConfig(): Config
}
