package net.lachlanmckee.bitrise.results.domain.entity

import net.lachlanmckee.bitrise.core.data.entity.TestSuite

internal data class TestResultDetailModel(
  val buildSlug: String,
  val bitriseUrl: String,
  val cost: String,
  val testSuites: List<TestSuite>,
  val matrixIds: String
)
