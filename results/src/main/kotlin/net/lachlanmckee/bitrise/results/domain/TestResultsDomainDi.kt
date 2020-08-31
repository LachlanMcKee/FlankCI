package net.lachlanmckee.bitrise.results.domain

import net.lachlanmckee.bitrise.core.data.CoreDataDi
import net.lachlanmckee.bitrise.core.domain.mapper.BuildsMapper
import net.lachlanmckee.bitrise.results.domain.interactor.TestResultInteractor
import net.lachlanmckee.bitrise.results.domain.interactor.TestResultsListInteractor

internal object TestResultsDomainDi {
    val testResultsListInteractor: TestResultsListInteractor by lazy {
        TestResultsListInteractor(
            CoreDataDi.bitriseDataSource,
            CoreDataDi.configDataSource,
            BuildsMapper()
        )
    }

    val testResultInteractor: TestResultInteractor by lazy {
        TestResultInteractor(
            CoreDataDi.bitriseDataSource,
            CoreDataDi.configDataSource
        )
    }
}
