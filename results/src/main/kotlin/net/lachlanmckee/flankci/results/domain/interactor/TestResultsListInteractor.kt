package net.lachlanmckee.flankci.results.domain.interactor

import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.data.datasource.remote.CIDataSource
import net.lachlanmckee.flankci.core.data.entity.BuildType
import net.lachlanmckee.flankci.core.data.entity.ConfigurationId
import net.lachlanmckee.flankci.results.domain.entity.TestResultListModel
import net.lachlanmckee.flankci.results.domain.mapper.TestResultsListMapper
import javax.inject.Inject

internal class TestResultsListInteractor @Inject constructor(
  private val ciDataSource: CIDataSource,
  private val configDataSource: ConfigDataSource,
  private val testResultsListMapper: TestResultsListMapper
) {
  suspend fun execute(configurationId: ConfigurationId): Result<TestResultListModel> {
    return ciDataSource
      .getBuilds(configurationId, BuildType.TEST_TRIGGER)
      .mapCatching(testResultsListMapper::mapToTestResultsList)
      .mapCatching { resultsList ->
        val configuration = configDataSource.getConfig().configuration(configurationId)
        TestResultListModel(
          configurationId = configurationId,
          configurationDisplayName = configuration.displayName,
          results = resultsList
        )
      }
  }
}
