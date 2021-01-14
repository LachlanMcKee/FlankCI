package net.lachlanmckee.flankci.results.domain.interactor

import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.flankci.results.domain.entity.TestResultModel
import net.lachlanmckee.flankci.results.domain.mapper.TestResultsListMapper
import javax.inject.Inject

internal class TestResultsListInteractor @Inject constructor(
  private val ciDataSource: CIDataSource,
  private val configDataSource: ConfigDataSource,
  private val testResultsListMapper: TestResultsListMapper
) {
  suspend fun execute(): Result<List<TestResultModel>> {
    return ciDataSource
      .getBuilds(configDataSource.getConfig().bitrise.testTriggerWorkflow)
      .mapCatching(testResultsListMapper::mapToTestResultsList)
  }
}
