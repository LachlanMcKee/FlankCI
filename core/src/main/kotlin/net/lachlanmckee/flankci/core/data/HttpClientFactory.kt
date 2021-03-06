package net.lachlanmckee.flankci.core.data

import io.ktor.client.*
import io.ktor.client.engine.*

interface HttpClientFactory {
  val engineFactory: HttpClientEngineFactory<HttpClientEngineConfig>
  fun handleConfig(config: HttpClientConfig<HttpClientEngineConfig>) {
  }
}
