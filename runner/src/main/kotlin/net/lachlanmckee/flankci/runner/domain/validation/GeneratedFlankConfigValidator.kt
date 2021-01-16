package net.lachlanmckee.flankci.runner.domain.validation

import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.runner.domain.entity.GeneratedFlankConfig
import javax.inject.Inject

internal class GeneratedFlankConfigValidator @Inject constructor(
  private val configDataSource: ConfigDataSource
) {
  suspend fun getValidationErrorMessage(
    configurationId: ConfigurationId,
    generatedConfig: GeneratedFlankConfig
  ): String? {
    return if (!configDataSource.getConfig().configuration(configurationId).testData.allowTestingWithoutFilters) {
      when {
        ((generatedConfig.contentAsMap["gcloud"] as? Map<String, Any>?)?.get("test-targets") as? List<String>?).isNullOrEmpty() -> {
          "You cannot execute the tests without specifying at least one annotation/package/class filter"
        }
        ((generatedConfig.contentAsMap["gcloud"] as? Map<String, Any>?)?.get("device") as? List<String>?).isNullOrEmpty() -> {
          "You cannot execute the tests without specifying at least one device"
        }
        else -> {
          null
        }
      }
    } else {
      null
    }
  }
}
