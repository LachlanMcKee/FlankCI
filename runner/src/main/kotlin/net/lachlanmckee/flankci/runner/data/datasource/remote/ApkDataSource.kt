package net.lachlanmckee.flankci.runner.data.datasource.remote

import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import net.lachlanmckee.flankci.core.data.datasource.remote.CIService
import javax.inject.Inject

internal interface ApkDataSource {
  suspend fun getTestApkTestMethods(testApkUrl: String): Result<List<TestMethod>>
}

internal class ApkDataSourceImpl @Inject constructor(private val ciService: CIService) : ApkDataSource {
  override suspend fun getTestApkTestMethods(testApkUrl: String): Result<List<TestMethod>> = kotlin.runCatching {
    ciService.getUsingTempFile(testApkUrl) {
      DexParser.findTestMethods(it.absolutePath)
    }
  }
}
