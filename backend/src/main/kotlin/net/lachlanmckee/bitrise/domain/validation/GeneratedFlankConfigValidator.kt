package net.lachlanmckee.bitrise.domain.validation

import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.domain.entity.GeneratedFlankConfig

class GeneratedFlankConfigValidator(
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
