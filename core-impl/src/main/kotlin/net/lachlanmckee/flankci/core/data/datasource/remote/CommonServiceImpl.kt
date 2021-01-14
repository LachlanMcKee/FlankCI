package net.lachlanmckee.flankci.core.data.datasource.remote

import io.ktor.client.*
import net.lachlanmckee.flankci.core.data.api.withTempFile
import net.lachlanmckee.flankci.core.data.entity.generic.*
import java.io.File
import javax.inject.Inject

internal class CommonServiceImpl @Inject constructor(
  private val client: HttpClient
) : CommonService {

  override suspend fun <T> getUsingTempFile(url: String, callback: suspend (file: File) -> T): T {
    var startTime = System.currentTimeMillis()
    println("Download started")
    return client.withTempFile(url) {
      println("Download finished. Time: ${System.currentTimeMillis() - startTime}")

      startTime = System.currentTimeMillis()
      callback(it).also {
        println("Parsing finished. Time: ${System.currentTimeMillis() - startTime}")
      }
    }
  }
}
