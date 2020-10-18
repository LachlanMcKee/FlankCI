package net.lachlanmckee.bitrise.results.domain.mapper

import javax.inject.Inject

internal class FirebaseUrlMapper @Inject constructor() {
  fun mapBuildLogToFirebaseUrl(buildLog: String): String {
    return buildLog
      .lineSequence()
      .first { it.contains(FIREBASE_CONSOLE_PREFIX) }
      .let { FIREBASE_CONSOLE_PREFIX + it.substringAfter(FIREBASE_CONSOLE_PREFIX) }
  }

  private companion object {
    private const val FIREBASE_CONSOLE_PREFIX = "https://console.firebase.google.com/project/"
  }
}
