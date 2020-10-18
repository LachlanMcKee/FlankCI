package net.lachlanmckee.bitrise.results.domain.mapper

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class FirebaseUrlMapperTest {
  @Test
  fun givenNoFirebaseConsoleUrl_whenMap_thenExpectException() {
    Assertions.assertThrows(NoSuchElementException::class.java) {
      FirebaseUrlMapper().mapBuildLogToFirebaseUrl("No console URL")
    }
  }

  @Test
  fun givenFirebaseConsoleUrlExists_whenMap_thenExpectUrl() {
    val firebaseUrl = FirebaseUrlMapper()
      .mapBuildLogToFirebaseUrl(
        """
            Log line 1
            https://console.firebase.google.com/project/foobar
            Log line 2
        """.trimIndent()
      )

    assertEquals("https://console.firebase.google.com/project/foobar", firebaseUrl)
  }
}
