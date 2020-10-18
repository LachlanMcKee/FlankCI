package net.lachlanmckee.bitrise.core

import kotlinx.coroutines.Deferred

suspend fun <T> Deferred<Result<T>>.awaitGetOrThrow(): T {
  return await().getOrThrow()
}
