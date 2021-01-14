package net.lachlanmckee.flankci.core.data.api

import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.lachlanmckee.flankci.core.data.entity.HttpClientException
import java.io.File

internal suspend fun <T> HttpClient.withTempFile(url: String, callback: suspend (file: File) -> T): T {
  return withContext(Dispatchers.IO) {
    val file = File.createTempFile("ktor", "http-client")
    val response = request<HttpResponse> {
      url(url)
      method = HttpMethod.Get
    }
    if (!response.status.isSuccess()) {
      throw HttpClientException(response)
    }
    response.content.copyAndClose(file.writeChannel())

    try {
      callback(file)
    } finally {
      file.delete()
    }
  }
}
