package net.lachlanmckee.flankci.presentation

data class HomeModel(val configurations: List<CiConfiguration>) {
  data class CiConfiguration(
    val id: String,
    val displayName: String
  )
}
