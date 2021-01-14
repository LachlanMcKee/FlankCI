package net.lachlanmckee.flankci.core.data.entity.bitrise

import gsonpath.annotation.AutoGsonAdapter

@AutoGsonAdapter
data class BitriseConfigModel(
  val appId: String,
  val testApkSourceWorkflow: String,
  val testTriggerWorkflow: String
)
