package net.lachlanmckee.flankci.runner.data.datasource.remote

import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import net.lachlanmckee.flankci.core.data.datasource.remote.CommonService
import javax.inject.Inject

internal interface ApkDataSource {
  suspend fun getTestApkTestMethods(testApkUrl: String): Result<List<TestMethod>>
}

internal class ApkDataSourceImpl @Inject constructor(private val commonService: CommonService) : ApkDataSource {
  override suspend fun getTestApkTestMethods(testApkUrl: String): Result<List<TestMethod>> = kotlin.runCatching {
    commonService.getUsingTempFile(testApkUrl) {
      DexParser.findTestMethods(it.absolutePath)
    }
  }
}
