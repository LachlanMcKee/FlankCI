package net.lachlanmckee.bitrise.results.domain.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.lachlanmckee.bitrise.results.domain.entity.TestSuites

internal class TestSuitesMapper(
    private val xmlMapper: ObjectMapper
) {
    fun mapTestSuites(xmlText: String): TestSuites {
        return xmlMapper.readValue(xmlText)
    }
}
