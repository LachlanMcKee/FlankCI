package net.lachlanmckee.flankci.core.data.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.lachlanmckee.flankci.core.data.entity.junit.TestSuites

interface TestSuitesMapper {
  fun mapTestSuites(xmlText: String): TestSuites
}

internal class TestSuitesMapperImpl(
  private val xmlMapper: ObjectMapper
) : TestSuitesMapper {
  override fun mapTestSuites(xmlText: String): TestSuites {
    return xmlMapper.readValue(xmlText)
  }
}
