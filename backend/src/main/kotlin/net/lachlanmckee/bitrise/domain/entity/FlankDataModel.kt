package net.lachlanmckee.bitrise.domain.entity

data class FlankDataModel(
    val branch: String,
    val commitHash: String,
    val rootPackage: String,
    val annotations: List<String>,
    val packages: List<String>,
    val classes: List<String>,
    val checkboxOptions : Map<Int, Boolean>,
    val dropDownOptions : Map<Int, Int>
)
