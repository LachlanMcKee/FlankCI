package net.lachlanmckee.bitrise.results.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class TestSuites(
    val testsuite: List<TestSuite>
)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class TestSuite(
    val name: String,
    val tests: Int,
    val failures: Int,
    val time: String,
    val testcase: List<TestCase>
)

@JsonIgnoreProperties(ignoreUnknown = true)
internal data class TestCase(
    val name: String,
    val classname: String,
    val time: String,
    val webLink: String,
    val failure: String?
)
