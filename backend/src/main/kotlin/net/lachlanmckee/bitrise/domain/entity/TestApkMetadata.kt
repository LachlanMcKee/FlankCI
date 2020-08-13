package net.lachlanmckee.bitrise.domain.entity

data class TestApkMetadata(
    val rootPackage: String,
    val annotations: List<String>,
    val packages: List<PathWithAnnotationGroups>,
    val classes: List<PathWithAnnotationGroups>
)

