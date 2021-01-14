package net.lachlanmckee.flankci.core.data.entity.generic

data class ArtifactsListResponse(
  val data: List<ArtifactContent>
) {
  data class ArtifactContent(
    val title: String,
    val slug: String,
    val artifactMeta: Meta?
  ) {
    data class Meta(
      val buildType: String
    )
  }
}
