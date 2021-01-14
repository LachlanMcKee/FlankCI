package net.lachlanmckee.flankci.core.data.entity

import io.ktor.client.statement.HttpResponse
import java.io.IOException

internal data class HttpClientException(
  val response: HttpResponse
) : IOException("HTTP Error ${response.status}")
