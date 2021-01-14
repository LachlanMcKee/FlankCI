package net.lachlanmckee.flankci.runner.domain.mapper

import io.ktor.http.content.MultiPartData
import net.lachlanmckee.flankci.core.domain.mapper.FormDataCollector
import net.lachlanmckee.flankci.runner.domain.entity.FlankDataModel
import javax.inject.Inject

internal class FlankDataMapper @Inject constructor(
  private val formDataCollector: FormDataCollector
) {
  suspend fun mapToFlankData(multipart: MultiPartData): FlankDataModel {
    var isRerun = false
    var branch: String? = null
    var buildSlug: String? = null
    var commitHash: String? = null
    var rootPackage: String? = null
    val annotations = mutableListOf<String>()
    val packages = mutableListOf<String>()
    val classes = mutableListOf<String>()
    val fullClasses = mutableListOf<String>()
    val checkboxOptions = mutableMapOf<Int, Boolean>()
    val dropDownOptions = mutableMapOf<Int, Int>()

    formDataCollector.collectData(multipart) { name, value ->
      when {
        name == "isRerun" -> isRerun = value.toBoolean()
        name == "branch-select" -> branch = value
        name == "buildSlug" -> buildSlug = value
        name == "commitHash" -> commitHash = value
        name == "rootPackage" -> rootPackage = value
        name == "annotation" -> annotations.add(value)
        name == "package" -> packages.add(value)
        name == "class" -> classes.add(value)
        name == "full_class" -> fullClasses.add(value)
        name.startsWith("option-checkbox-") -> {
          val checkboxContent = name.removePrefix("option-checkbox-").split("-")
          val index = checkboxContent[0].toInt()
          val isEnabled = checkboxContent.size == 1

          val existingValue = checkboxOptions[index]
          if (existingValue == null) {
            checkboxOptions[index] = isEnabled
          } else if (!existingValue) {
            checkboxOptions[index] = isEnabled
          }
        }
        name.startsWith("option-drop-down-") -> {
          val dropDownIndex = name.removePrefix("option-drop-down-").toInt()
          dropDownOptions[dropDownIndex] = value.toInt()
        }
      }
    }

    return FlankDataModel(
      isRerun = isRerun,
      branch = requireNotNull(branch) { "Branch must exist" },
      buildSlug = requireNotNull(buildSlug) { "Build slug must exist" },
      commitHash = requireNotNull(commitHash) { "Commit hash must exist" },
      rootPackage = requireNotNull(rootPackage) { "Root package must exist" },
      annotations = annotations,
      packages = packages,
      classes = classes,
      fullClasses = fullClasses,
      checkboxOptions = checkboxOptions,
      dropDownOptions = dropDownOptions
    )
  }
}
