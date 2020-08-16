package net.lachlanmckee.bitrise.data.entity

import gsonpath.annotation.AutoGsonAdapter
import gsonpath.annotation.GsonSubtype
import gsonpath.annotation.GsonSubtypeGetter

@AutoGsonAdapter
data class ConfigModel(
    val bitrise: Bitrise,
    val testData: TestData
) {
    @AutoGsonAdapter
    data class Bitrise(
        val appId: String,
        val testApkSourceWorkflow: String,
        val testTriggerWorkflow: String
    )

    @AutoGsonAdapter
    data class TestData(
        val hiddenAnnotations: List<String>,
        val ignoreTestsWithAnnotations: List<String>,
        val allowTestingWithoutFilters: Boolean,
        val flankConfig: FlankConfig,
        val options: List<Option>
    )

    @AutoGsonAdapter
    data class FlankConfig(
        val commonYamlFiles: List<String>,
        val annotationBasedYaml: AnnotationBasedYaml
    ) {
        @AutoGsonAdapter
        data class AnnotationBasedYaml(
            val options: List<AnnotationAndYaml>,
            val fallbackYamlFiles: List<String>
        )

        @AutoGsonAdapter
        data class AnnotationAndYaml(
            val annotation: String,
            val yamlFiles: List<String>
        )
    }

    @GsonSubtype(jsonKeys = ["type"])
    sealed class Option {
        @AutoGsonAdapter
        data class Checkbox(
            val label: String,
            val checkedYamlFiles: List<String>,
            val uncheckedYamlFiles: List<String>
        ) : Option()

        @AutoGsonAdapter
        data class DropDown(
            val label: String,
            val values: List<Value>
        ) : Option() {
            @AutoGsonAdapter
            data class Value(
                val label: String,
                val yamlFiles: List<String>
            )
        }

        companion object {
            @JvmStatic
            @GsonSubtypeGetter
            fun getSubType(type: String): Class<out Option> {
                return when (type) {
                    "checkbox" -> Checkbox::class.java
                    "drop-down" -> DropDown::class.java
                    else -> throw IllegalArgumentException("Unexpected type: $type")
                }
            }
        }
    }
}