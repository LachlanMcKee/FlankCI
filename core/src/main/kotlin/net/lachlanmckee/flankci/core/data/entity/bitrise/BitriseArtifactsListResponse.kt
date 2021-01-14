package net.lachlanmckee.flankci.core.data.entity.bitrise

import com.google.gson.FieldNamingPolicy
import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
data class BitriseArtifactsListResponse(
  val data: List<ArtifactContent>
) {
  @AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
  data class ArtifactContent(
    val title: String,
    val slug: String,
    val artifactMeta: Meta?
  ) {
    @AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
    data class Meta(
      val buildType: String
    )
  }
}
