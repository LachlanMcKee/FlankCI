package net.lachlanmckee.bitrise.domain.mapper

import io.ktor.http.content.MultiPartData
import net.lachlanmckee.bitrise.domain.entity.FlankDataModel

class FlankDataMapper(
    private val formDataCollector: FormDataCollector
) {
    suspend fun mapToFlankData(multipart: MultiPartData): FlankDataModel {
        var branch: String? = null
        var commitHash: String? = null
        var rootPackage: String? = null
        val annotations = mutableListOf<String>()
        val packages = mutableListOf<String>()
        val classes = mutableListOf<String>()
        val checkboxOptions = mutableMapOf<Int, Boolean>()
        val dropDownOptions = mutableMapOf<Int, Int>()

        formDataCollector.collectData(multipart) { name, value ->
            when {
                name == "branch-select" -> branch = value
                name == "commitHash" -> commitHash = value
                name == "rootPackage" -> rootPackage = value
                name == "annotation" -> annotations.add(value)
                name == "package" -> packages.add(value)
                name == "class" -> classes.add(value)
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
            branch = requireNotNull(branch) { "Branch must exist" },
            commitHash = requireNotNull(commitHash) { "Commit hash must exist" },
            rootPackage = requireNotNull(rootPackage) { "Root package must exist" },
            annotations = annotations,
            packages = packages,
            classes = classes,
            checkboxOptions = checkboxOptions,
            dropDownOptions = dropDownOptions
        )
    }
}
