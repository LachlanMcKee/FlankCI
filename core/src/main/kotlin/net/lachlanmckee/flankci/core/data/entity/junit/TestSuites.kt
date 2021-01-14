package net.lachlanmckee.flankci.core.data.entity.junit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class TestSuites(
  val testsuite: List<TestSuite>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TestSuite(
  val name: String,
  val testcase: List<TestCase>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TestCase(
  val name: String,
  val classname: String,
  val time: String,
  val webLink: String?,
  val failure: String?
)
