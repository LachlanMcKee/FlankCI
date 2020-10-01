package net.lachlanmckee.bitrise.results.domain.interactor

import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.results.domain.entity.TestResultModel
import net.lachlanmckee.bitrise.results.domain.mapper.TestResultsListMapper
import javax.inject.Inject

internal class TestResultsListInteractor @Inject constructor(
  private val bitriseDataSource: BitriseDataSource,
  private val configDataSource: ConfigDataSource,
  private val testResultsListMapper: TestResultsListMapper
) {
  suspend fun execute(): Result<List<TestResultModel>> {
    return bitriseDataSource
      .getBuilds(configDataSource.getConfig().bitrise.testTriggerWorkflow)
      .mapCatching(testResultsListMapper::mapToTestResultsList)
  }
}
