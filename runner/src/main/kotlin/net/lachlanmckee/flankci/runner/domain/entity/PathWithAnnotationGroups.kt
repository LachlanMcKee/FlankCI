package net.lachlanmckee.flankci.runner.domain.entity

internal data class PathWithAnnotationGroups(
  val path: String,
  val annotationGroups: List<List<String>>?
)
