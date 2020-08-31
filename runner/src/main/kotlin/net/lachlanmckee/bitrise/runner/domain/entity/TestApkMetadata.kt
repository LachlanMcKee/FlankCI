package net.lachlanmckee.bitrise.runner.domain.entity

internal data class TestApkMetadata(
    val rootPackage: String,
    val annotations: List<String>,
    val packages: List<PathWithAnnotationGroups>,
    val classes: List<PathWithAnnotationGroups>
)

