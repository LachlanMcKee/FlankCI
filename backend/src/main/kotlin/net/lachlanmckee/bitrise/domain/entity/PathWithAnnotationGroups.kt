package net.lachlanmckee.bitrise.domain.entity

data class PathWithAnnotationGroups(
    val path: String,
    val annotationGroups: List<List<String>>?
)
