package net.lachlanmckee.flankci.core.data.entity.bitrise

import com.google.gson.FieldNamingPolicy
import gsonpath.GsonResultList
import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
data class BitriseMultipleBuildsResponse(
  val data: GsonResultList<BitriseBuildDataResponse>,
  val paging: Paging
) {
  @AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
  data class Paging(
    val totalItemCount: Int,
    val pageItemLimit: Int,
    val next: String
  )
}
