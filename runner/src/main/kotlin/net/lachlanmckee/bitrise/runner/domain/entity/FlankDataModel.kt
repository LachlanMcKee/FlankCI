package net.lachlanmckee.bitrise.runner.domain.entity

internal data class FlankDataModel(
    val branch: String,
    val commitHash: String,
    val rootPackage: String,
    val annotations: List<String>,
    val packages: List<String>,
    val classes: List<String>,
    val checkboxOptions : Map<Int, Boolean>,
    val dropDownOptions : Map<Int, Int>
)
