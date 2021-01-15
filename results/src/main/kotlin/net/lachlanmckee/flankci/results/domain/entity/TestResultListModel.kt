package net.lachlanmckee.flankci.results.domain.entity

import net.lachlanmckee.flankci.core.data.entity.ConfigurationId

internal data class TestResultListModel(
  val configurationId: ConfigurationId,
  val configurationDisplayName: String,
  val results: List<TestResultModel>
)
