package net.lachlanmckee.bitrise.runner.domain.interactor

import io.mockk.*
import kotlinx.coroutines.runBlocking
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.core.data.entity.TestCase
import net.lachlanmckee.bitrise.core.data.entity.TestSuite
import net.lachlanmckee.bitrise.runner.domain.entity.RerunModel
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TestRerunInteractorTest {
  private val bitriseDataSource: BitriseDataSource = mockk()
  private val interactor = TestRerunInteractor(bitriseDataSource)

  @AfterEach
  fun verifyNoMoreInteractions() {
    confirmVerified(bitriseDataSource)
  }

  @Test
  fun givenBuildDetailsFails_whenExecute_thenReturnFailure() = runBlocking {
    val exception = RuntimeException()
    coEvery { bitriseDataSource.getBuildDetails("buildSlug") } returns Result.failure(exception)

    val result = interactor.execute("buildSlug")

    assertEquals(exception, result.exceptionOrNull())

    coVerifySequence {
      bitriseDataSource.getBuildDetails("buildSlug")
    }
  }

  @Test
  fun givenTestResultsFails_whenExecute_thenReturnFailure() = runBlocking {
    val exception = RuntimeException()
    coEvery { bitriseDataSource.getBuildDetails("buildSlug") } returns Result.success(
      mockk {
        every { branch } returns "branch"
      }
    )
    coEvery { bitriseDataSource.getTestResults("buildSlug") } returns Result.failure(exception)

    val result = interactor.execute("buildSlug")

    assertEquals(exception, result.exceptionOrNull())

    coVerifySequence {
      bitriseDataSource.getBuildDetails("buildSlug")
      bitriseDataSource.getTestResults("buildSlug")
    }
  }

  @Test
  fun givenDuplicateFailedTests_whenExecute_thenReturnUniqueTests() = runBlocking {
    testWithContentSuccess(
      testSuites = listOf(
        createTestSuite(
          createFailedTest("com.tests.TestB", "test1"),
          createFailedTest("com.tests.TestA", "test1")
        ),
        createTestSuite(
          createFailedTest("com.tests.TestA", "test1")
        )
      ),
      expectedTests = listOf(
        "com.tests.TestA#test1",
        "com.tests.TestB#test1"
      )
    )
  }

  @Test
  fun givenAllSuccessfulTests_whenExecute_thenReturnNoTests() = runBlocking {
    testWithContentSuccess(
      testSuites = listOf(
        createTestSuite(
          createSuccessfulTest("com.tests.TestA#test1")
        ),
        createTestSuite(
          createSuccessfulTest("com.tests.TestA#test2")
        )
      ),
      expectedTests = emptyList()
    )
  }

  @Test
  fun givenTestSuiteWithNullTestsCase_whenExecute_thenReturnNoTests() = runBlocking {
    testWithContentSuccess(
      testSuites = listOf(
        TestSuite(
          name = "suite",
          tests = 0,
          failures = 1,
          time = "time",
          testcase = null
        ),
        createTestSuite()
      ),
      expectedTests = emptyList()
    )
  }

  private fun testWithContentSuccess(testSuites: List<TestSuite>, expectedTests: List<String>) = runBlocking {
    coEvery { bitriseDataSource.getBuildDetails("buildSlug") } returns Result.success(
      mockk {
        every { branch } returns "branch"
      }
    )
    coEvery { bitriseDataSource.getTestResults("buildSlug") } returns Result.success(testSuites)

    val result = interactor.execute("buildSlug")

    assertEquals(
      RerunModel(
        branch = "branch",
        failedTests = expectedTests
      ),
      result.getOrNull()
    )

    coVerifySequence {
      bitriseDataSource.getBuildDetails("buildSlug")
      bitriseDataSource.getTestResults("buildSlug")
    }
  }

  private fun createTestSuite(vararg testcase: TestCase): TestSuite {
    return TestSuite(
      name = "suite",
      tests = testcase.size,
      failures = 1,
      time = "time",
      testcase = testcase.toList()
    )
  }

  private fun createSuccessfulTest(className: String): TestCase {
    return TestCase(
      name = "name",
      classname = className,
      time = "time",
      webLink = null,
      failure = null
    )
  }

  private fun createFailedTest(className: String, name: String): TestCase {
    return TestCase(
      name = name,
      classname = className,
      time = "time",
      webLink = null,
      failure = "Failure"
    )
  }
}
