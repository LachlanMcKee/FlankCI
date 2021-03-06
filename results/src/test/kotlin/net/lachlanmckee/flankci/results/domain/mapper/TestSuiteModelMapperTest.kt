package net.lachlanmckee.flankci.results.domain.mapper

import net.lachlanmckee.flankci.core.data.entity.junit.TestCase
import net.lachlanmckee.flankci.core.data.entity.junit.TestSuite
import net.lachlanmckee.flankci.results.domain.entity.TestResultDetailModel.WithResults.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TestSuiteModelMapperTest {
  @Test
  fun givenEmptyList_whenMap_thenExpectEmptyList() = test(
    input = emptyList(),
    output = emptyList()
  )

  @Test
  fun givenNonEmptyList_whenMap_thenExpectTestSuiteModelList() = test(
    input = listOf(
      TestSuite(
        name = "Device2",
        testcase = listOf(
          TestCase(
            name = "testB",
            classname = "com.example.TestClass2",
            time = "5.00",
            webLink = " link ",
            failure = "failureReason"
          ),
          TestCase(
            name = "testA",
            classname = "com.example.TestClass1",
            time = "5.00",
            webLink = " link ",
            failure = null
          )
        )
      ),
      TestSuite(
        name = "Device1",
        testcase = listOf(
          TestCase(
            name = "testC",
            classname = "com.example.TestClass3",
            time = "5.00",
            webLink = " link ",
            failure = "failureReason"
          )
        )
      ),
      TestSuite(
        name = "Device1",
        testcase = listOf(
          TestCase(
            name = "testB",
            classname = "com.example.TestClass2",
            time = "5.00",
            webLink = " link ",
            failure = null
          ),
          TestCase(
            name = "testA",
            classname = "com.example.TestClass1",
            time = "5.00",
            webLink = " link ",
            failure = null
          )
        )
      ),
      TestSuite(
        name = "Device2",
        testcase = listOf(
          TestCase(
            name = "testC",
            classname = "com.example.TestClass3",
            time = "5.00",
            webLink = " link ",
            failure = null
          )
        )
      )
    ),
    output = listOf(
      TestSuiteModel(
        name = "Device1",
        totalTests = 1,
        time = "5.00",
        resultType = TestResultType.FAILURE,
        testCases = listOf(
          TestModel(
            path = "com.example.TestClass3#testC",
            webLink = "link",
            failure = "failureReason",
            time = "5.00"
          )
        )
      ),
      TestSuiteModel(
        name = "Device2",
        totalTests = 1,
        time = "5.00",
        resultType = TestResultType.FAILURE,
        testCases = listOf(
          TestModel(
            path = "com.example.TestClass2#testB",
            webLink = "link",
            failure = "failureReason",
            time = "5.00"
          )
        )
      ),
      TestSuiteModel(
        name = "Device1",
        totalTests = 2,
        time = "10.00",
        resultType = TestResultType.SUCCESS,
        testCases = listOf(
          TestModel(
            path = "com.example.TestClass1#testA",
            webLink = "link",
            failure = null,
            time = "5.00"
          ),
          TestModel(
            path = "com.example.TestClass2#testB",
            webLink = "link",
            failure = null,
            time = "5.00"
          )
        )
      ),
      TestSuiteModel(
        name = "Device2",
        totalTests = 2,
        time = "10.00",
        resultType = TestResultType.SUCCESS,
        testCases = listOf(
          TestModel(
            path = "com.example.TestClass1#testA",
            webLink = "link",
            failure = null,
            time = "5.00"
          ),
          TestModel(
            path = "com.example.TestClass3#testC",
            webLink = "link",
            failure = null,
            time = "5.00"
          )
        )
      )
    )
  )

  private fun test(input: List<TestSuite>, output: List<TestSuiteModel>) {
    assertEquals(output, TestSuiteModelMapper().mapToTestSuiteModelList(input))
  }
}
