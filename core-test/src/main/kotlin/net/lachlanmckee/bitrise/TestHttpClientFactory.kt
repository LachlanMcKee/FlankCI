package net.lachlanmckee.bitrise

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import net.lachlanmckee.bitrise.core.data.FileReader
import net.lachlanmckee.bitrise.core.data.HttpClientFactory
import java.io.BufferedReader

val unhandledMockRequestHandler: MockRequestHandler = { request ->
  error("Unhandled ${request.url.fullPath}")
}

fun createTestHttpClientFactory(
  requestHandler: MockRequestHandler = unhandledMockRequestHandler
): TestHttpClientFactory {
  return object : TestHttpClientFactory() {
    override val requestHandler = requestHandler
  }
}

object TestFileReader : FileReader {
  override fun read(name: String): BufferedReader {
    return ClassLoader.getSystemClassLoader().getResourceAsStream("input/$name")!!.bufferedReader()
  }
}

abstract class TestHttpClientFactory : HttpClientFactory {
  final override val engineFactory = MockEngine

  final override fun handleConfig(config: HttpClientConfig<HttpClientEngineConfig>) {
    with(config) {
      engine {
        (this as MockEngineConfig).addHandler(requestHandler)
      }
    }
  }

  abstract val requestHandler: MockRequestHandler
}
