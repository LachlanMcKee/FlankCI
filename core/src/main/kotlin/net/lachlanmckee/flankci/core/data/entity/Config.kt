package net.lachlanmckee.flankci.core.data.entity

import java.util.*

class Config(
  private val configModel: ConfigModel,
  private val secretProperties: Properties
) {
  val configurations: List<ConfigModel.Configuration>
    get() = configModel.configurations

  fun configuration(configurationId: ConfigurationId): ConfigModel.Configuration {
    return configModel.configurations.single { it.id == configurationId.id }
  }

  fun token(configurationId: ConfigurationId): String {
    return secretProperties.getProperty(configurationId.id)
  }
}
