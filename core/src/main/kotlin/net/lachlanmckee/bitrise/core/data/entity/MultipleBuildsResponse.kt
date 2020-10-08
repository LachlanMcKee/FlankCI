package net.lachlanmckee.bitrise.core.data.entity

import com.google.gson.FieldNamingPolicy
import gsonpath.GsonResultList
import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
data class MultipleBuildsResponse(
  val data: GsonResultList<BuildDataResponse>,
  val paging: Paging
) {
  @AutoGsonAdapter(fieldNamingPolicy = [FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES])
  data class Paging(
    val totalItemCount: Int,
    val pageItemLimit: Int,
    val next: String
  )
}
