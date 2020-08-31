package net.lachlanmckee.bitrise.runner.domain.validation

import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.runner.domain.entity.GeneratedFlankConfig

internal class GeneratedFlankConfigValidator(
    private val configDataSource: ConfigDataSource
) {
    suspend fun getValidationErrorMessage(generatedConfig: GeneratedFlankConfig) : String? {
        return if (!configDataSource.getConfig().testData.allowTestingWithoutFilters) {
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
