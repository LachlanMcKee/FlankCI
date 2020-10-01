package net.lachlanmckee.bitrise.core.domain.mapper

import io.ktor.http.content.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

internal class FormDataCollectorImplTest {
  private val multipart = mockk<MultiPartData>()
  private val func = mockk<(String, String) -> Unit>()

  @AfterEach
  fun verifyNoMoreInteractions() {
    confirmVerified(multipart, func)
  }

  @Test
  fun givenReadPartReturnsNull_whenCollectData_thenNeverInvokeFunction() = runBlocking {
    coEvery { multipart.readPart() } returns null

    FormDataCollectorImpl().collectData(multipart, func)

    coVerifySequence {
      multipart.readPart()
    }
  }

  @Test
  fun givenReadPartReturnsNonFormItem_whenCollectData_thenNeverInvokeFunction() = runBlocking {
    val binaryItem = mockk<PartData.BinaryItem>()
    coEvery { multipart.readPart() } returnsMany listOf(binaryItem, null)

    FormDataCollectorImpl().collectData(multipart, func)

    coVerifySequence {
      multipart.readPart()
      binaryItem.dispose()
      multipart.readPart()
    }
  }

  @Test
  fun givenReadPartReturnsFormItemWithoutName_whenCollectData_thenNeverInvokeFunction() = runBlocking {
    val formItem = mockk<PartData.FormItem>()
    every { formItem.name } returns null
    coEvery { multipart.readPart() } returnsMany listOf(formItem, null)

    FormDataCollectorImpl().collectData(multipart, func)

    coVerifySequence {
      multipart.readPart()
      formItem.name
      formItem.dispose()
      multipart.readPart()
    }
  }

  @Test
  fun givenReadPartReturnsFormItemWithName_whenCollectData_thenInvokeFunction() = runBlocking {
    val formItem = mockk<PartData.FormItem>()
    every { formItem.name } returns "name"
    every { formItem.value } returns "value"
    coEvery { multipart.readPart() } returnsMany listOf(formItem, null)

    FormDataCollectorImpl().collectData(multipart, func)

    coVerifySequence {
      multipart.readPart()
      formItem.name
      formItem.value
      func("name", "value")
      formItem.dispose()
      multipart.readPart()
    }
  }
}
