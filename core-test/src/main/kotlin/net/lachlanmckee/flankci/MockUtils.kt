package net.lachlanmckee.flankci

import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*

fun MockRequestHandleScope.respondJson(contentFilePath: String): HttpResponseData {
  return respond(
    readFile(contentFilePath),
    HttpStatusCode.OK,
    headersOf("Content-Type", ContentType.Application.Json.toString())
  )
}
