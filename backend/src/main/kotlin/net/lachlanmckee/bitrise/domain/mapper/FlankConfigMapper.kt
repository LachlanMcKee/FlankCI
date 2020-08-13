package net.lachlanmckee.bitrise.domain.mapper

import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.data.entity.ConfigModel
import net.lachlanmckee.bitrise.domain.entity.FlankDataModel
import net.lachlanmckee.bitrise.domain.entity.GeneratedFlankConfig
import org.yaml.snakeyaml.Yaml
import java.io.File

class FlankConfigMapper(
    private val configDataSource: ConfigDataSource
) {
    suspend fun mapToFlankYaml(flankDataModel: FlankDataModel): Result<GeneratedFlankConfig> = kotlin.runCatching {
        val config = configDataSource.getConfig()
        val flankConfig = config.testData.flankConfig

        val annotationBasedYamlFileNames: List<String> = (flankConfig.annotationBasedYaml.options
            .firstOrNull {
                flankDataModel.annotations.contains(it.annotation)
            }
            ?.yamlFiles
            ?: flankConfig.annotationBasedYaml.fallbackYamlFiles)

        val yaml = Yaml()
        val mergedYaml = mutableMapOf<String, Any>()

        addYaml(yaml, mergedYaml, flankConfig.commonYamlFiles)
        addYaml(yaml, mergedYaml, annotationBasedYamlFileNames)

        flankDataModel.checkboxOptions.forEach { (indexKey, isChecked) ->
            val checkboxOption = config.testData.options[indexKey] as ConfigModel.Option.Checkbox
            if (isChecked) {
                addYaml(yaml, mergedYaml, checkboxOption.checkedYamlFiles)
            } else {
                addYaml(yaml, mergedYaml, checkboxOption.uncheckedYamlFiles)
            }
        }

        flankDataModel.dropDownOptions.forEach { (indexKey, optionIndex) ->
            val dropDownOption = config.testData.options[indexKey] as ConfigModel.Option.DropDown
            addYaml(yaml, mergedYaml, dropDownOption.values[optionIndex].yamlFiles)
        }

        // Replace any special values with their correct data.
        replaceYamlMapSpecialValues(mergedYaml, flankDataModel)

        addTestTargets(flankDataModel, mergedYaml)

        GeneratedFlankConfig(
            contentAsMap = mergedYaml,
            contentAsString = yaml.dumpAsMap(mergedYaml)
        )
    }

    private fun addYaml(yaml: Yaml, yamlMap: MutableMap<String, Any>, fileNames: List<String>) {
        fileNames.forEach { fileName ->
            val newYamlMap: Map<String, Any> = yaml.load(File(fileName).inputStream())
            deepMerge(yamlMap, newYamlMap)
        }
    }

    private fun replaceYamlMapSpecialValues(yamlMap: MutableMap<String, Any>, flankDataModel: FlankDataModel) {
        // Replace any special values with their correct data.
        yamlMap.entries.forEach { entry ->
            val entryValue = entry.value

            if (entryValue is String) {
                if (entryValue.contains("\$BRANCH")) {
                    entry.setValue(entryValue.replace("\$BRANCH", flankDataModel.branch))

                } else if (entryValue.contains("\$COMMIT_HASH")) {
                    entry.setValue(entryValue.replace("\$COMMIT_HASH", flankDataModel.commitHash))
                }
            } else if (entryValue is Map<*, *>) {
                replaceYamlMapSpecialValues(entryValue as MutableMap<String, Any>, flankDataModel)

            } else if (entryValue is List<*>) {
                replaceYamlListSpecialValues(entryValue as MutableList<Any>, flankDataModel)
            }
        }
    }

    private fun replaceYamlListSpecialValues(yamlList: MutableList<Any>, flankDataModel: FlankDataModel) {
        yamlList.forEachIndexed { index, listValue ->
            when (listValue) {
                is String -> {
                    yamlList[index] = when {
                        listValue.contains("\$BRANCH") -> {
                            listValue.replace("\$BRANCH", flankDataModel.branch)
                        }
                        listValue.contains("\$COMMIT_HASH") -> {
                            listValue.replace("\$COMMIT_HASH", flankDataModel.commitHash)
                        }
                        else -> {
                            listValue
                        }
                    }
                }
                is Map<*, *> -> {
                    replaceYamlMapSpecialValues(listValue as MutableMap<String, Any>, flankDataModel)
                }
                is List<*> -> {
                    replaceYamlListSpecialValues(listValue as MutableList<Any>, flankDataModel)
                }
            }
        }
    }

    private fun addTestTargets(mapFormData: FlankDataModel, mergedYaml: MutableMap<String, Any>) {
        val testTargets = mutableListOf<String>()
        val rootPackage = mapFormData.rootPackage

        mapFormData.annotations.forEach {
            testTargets.add("annotation $it")
        }
        mapFormData.classes.forEach {
            testTargets.add("class $rootPackage.$it")
        }
        mapFormData.packages.forEach {
            if (it.isEmpty()) {
                testTargets.add("package $it")
            } else {
                testTargets.add("package $rootPackage.$it")
            }
        }

        if (testTargets.isNotEmpty()) {
            val testsYaml: Map<String, Any> = mapOf(
                "gcloud" to mapOf<String, Any>(
                    "test-targets" to testTargets
                )
            )
            deepMerge(mergedYaml, testsYaml)
        }
    }

    private fun deepMerge(
        map1: MutableMap<String, Any>,
        map2: Map<String, Any>
    ) {
        map2.forEach { (key, value2) ->
            if (map1.containsKey(key)) {
                val value1 = map1[key]
                when {
                    value1 is Map<*, *> && value2 is Map<*, *> -> {
                        deepMerge(
                            value1 as MutableMap<String, Any>,
                            value2 as Map<String, Any>
                        )
                    }
                    value1 is List<*> && value2 is List<*> -> {
                        map1[key] = mergeLists(value1 as List<Any>, value2 as List<Any>)
                    }
                    else -> {
                        map1[key] = value2
                    }
                }
            } else {
                map1[key] = value2
            }
        }
    }

    private fun mergeLists(
        list1: List<Any>,
        list2: List<Any>
    ): List<Any> {
        return list1.plus(list2.minus(list1))
    }
}
