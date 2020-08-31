package net.lachlanmckee.bitrise.core.data.entity

import io.ktor.client.statement.HttpResponse
import java.io.IOException

data class HttpClientException(
    val response: HttpResponse
) : IOException("HTTP Error ${response.status}")
