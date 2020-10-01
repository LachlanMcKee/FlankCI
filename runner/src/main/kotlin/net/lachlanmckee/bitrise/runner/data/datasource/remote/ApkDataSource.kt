package net.lachlanmckee.bitrise.runner.data.datasource.remote

import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseService
import javax.inject.Inject

internal interface ApkDataSource {
  suspend fun getTestApkTestMethods(testApkUrl: String): Result<List<TestMethod>>
}

internal class ApkDataSourceImpl @Inject constructor(private val bitriseService: BitriseService) : ApkDataSource {
  override suspend fun getTestApkTestMethods(testApkUrl: String): Result<List<TestMethod>> = kotlin.runCatching {
    bitriseService.getUsingTempFile(testApkUrl) {
      DexParser.findTestMethods(it.absolutePath)
    }
  }
}
