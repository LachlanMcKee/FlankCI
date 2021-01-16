package net.lachlanmckee.flankci.runner.domain.mapper

import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.data.entity.Config
import net.lachlanmckee.flankci.core.data.entity.ConfigModel
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.runner.domain.entity.FlankDataModel
import net.lachlanmckee.flankci.runner.domain.entity.GeneratedFlankConfig
import org.yaml.snakeyaml.Yaml
import java.io.File
import javax.inject.Inject

internal class FlankConfigMapper @Inject constructor(
  private val configDataSource: ConfigDataSource
) {
  suspend fun mapToFlankYaml(
    configurationId: ConfigurationId,
    flankDataModel: FlankDataModel
  ): Result<GeneratedFlankConfig> = kotlin.runCatching {
    val config = configDataSource.getConfig()
    val testData = config.configuration(configurationId).testData

    val annotationBasedYamlFileNames: List<String> = (
      testData.annotationBasedYaml.options
        .firstOrNull {
          flankDataModel.annotations.contains(it.annotation)
        }
        ?.yamlFiles
        ?: testData.annotationBasedYaml.fallbackYamlFiles
      )

    val jobNames: MutableList<String> = testData.annotationBasedYaml.options
      .filter {
        flankDataModel.annotations.contains(it.annotation)
      }
      .map { it.jobLabel }
      .toMutableList()

    val yaml = Yaml()
    val mergedYaml = mutableMapOf<String, Any>()

    addYaml(yaml, mergedYaml, testData.commonYamlFiles)
    addYaml(yaml, mergedYaml, annotationBasedYamlFileNames)

    val configOptions = getConfigOptions(configurationId, config, flankDataModel)
    flankDataModel.checkboxOptions.forEach { (indexKey, isChecked) ->
      val checkboxOption = configOptions[indexKey] as ConfigModel.Option.Checkbox
      if (isChecked) {
        addYaml(yaml, mergedYaml, checkboxOption.checkedCheckBoxContent.yamlFiles)
        jobNames.add(checkboxOption.checkedCheckBoxContent.jobLabel)
      } else {
        addYaml(yaml, mergedYaml, checkboxOption.uncheckedCheckBoxContent.yamlFiles)
        jobNames.add(checkboxOption.uncheckedCheckBoxContent.jobLabel)
      }
    }

    flankDataModel.dropDownOptions.forEach { (indexKey, optionIndex) ->
      val dropDownOption = configOptions[indexKey] as ConfigModel.Option.DropDown
      val dropDownValue = dropDownOption.values[optionIndex]
      addYaml(yaml, mergedYaml, dropDownValue.yamlFiles)
      jobNames.add(dropDownValue.jobLabel)
    }

    // Replace any special values with their correct data.
    replaceYamlMapSpecialValues(mergedYaml, flankDataModel)

    addTestTargets(flankDataModel, mergedYaml)

    GeneratedFlankConfig(
      contentAsMap = mergedYaml,
      contentAsString = yaml.dumpAsMap(mergedYaml),
      jobName = jobNames
        .filter { it.isNotEmpty() }
        .joinToString(separator = "-")
    )
  }

  private fun getConfigOptions(
    configurationId: ConfigurationId,
    config: Config,
    flankDataModel: FlankDataModel
  ): List<ConfigModel.Option> {
    return config.configuration(configurationId).testData.options.run {
      if (flankDataModel.isRerun) {
        rerun ?: standard
      } else {
        standard
      }
    }
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
    mapFormData.fullClasses.forEach {
      testTargets.add("class $it")
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
