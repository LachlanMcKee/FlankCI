package net.lachlanmckee.flankci

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import net.lachlanmckee.flankci.core.data.FileReader
import net.lachlanmckee.flankci.core.data.HttpClientFactory
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

class TestFileReader(private val overrides: Map<String, String> = emptyMap()) : FileReader {
  override fun read(name: String): BufferedReader {
    val resourceName: String = overrides[name] ?: name
    return ClassLoader.getSystemClassLoader().getResourceAsStream("input/$resourceName")!!.bufferedReader()
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
