package net.lachlanmckee.bitrise.runner.domain.interactor

import net.lachlanmckee.bitrise.core.data.datasource.remote.BitriseDataSource
import net.lachlanmckee.bitrise.runner.domain.entity.RerunModel
import javax.inject.Inject

class TestRerunInteractor @Inject constructor(
    private val bitriseDataSource: BitriseDataSource
) {
    suspend fun execute(buildSlug: String): Result<RerunModel> {
        return bitriseDataSource
            .getTestResults(buildSlug)
            .mapCatching { tests ->
                val failedTests: List<String> = tests
                    .flatMap { test ->
                        test.testcase
                            ?.mapNotNull {
                                if (it.failure != null) {
                                    it.classname
                                } else {
                                    null
                                }
                            }
                            ?: emptyList()
                    }

                RerunModel(failedTests)
            }
    }
}
