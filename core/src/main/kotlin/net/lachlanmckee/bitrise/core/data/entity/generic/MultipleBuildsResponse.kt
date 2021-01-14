package net.lachlanmckee.bitrise.core.data.entity.generic

import gsonpath.GsonResultList

data class MultipleBuildsResponse(
  val data: GsonResultList<BuildDataResponse>,
  val paging: Paging
) {
  data class Paging(
    val totalItemCount: Int,
    val pageItemLimit: Int,
    val next: String
  )
}
