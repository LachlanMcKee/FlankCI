package net.lachlanmckee.flankci.core.data.datasource.remote

import java.io.File

interface CommonService {
  suspend fun <T> getUsingTempFile(url: String, callback: suspend (file: File) -> T): T
}
