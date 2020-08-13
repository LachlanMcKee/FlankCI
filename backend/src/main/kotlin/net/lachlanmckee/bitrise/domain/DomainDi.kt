package net.lachlanmckee.bitrise.domain

import net.lachlanmckee.bitrise.data.DataDi
import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.domain.interactor.*
import net.lachlanmckee.bitrise.domain.mapper.*

class DomainDi {
    private val dataDi = DataDi()

    val branchesInteractor: BranchesInteractor by lazy {
        BranchesInteractor(dataDi.bitriseDataSource, BuildsMapper())
    }

    val configDataSource: ConfigDataSource by lazy {
        dataDi.configDataSource
    }

    val artifactsInteractor: ArtifactsInteractor by lazy {
        ArtifactsInteractor(dataDi.bitriseDataSource)
    }

    val testApkMetadataInteractor: TestApkMetadataInteractor by lazy {
        TestApkMetadataInteractor(dataDi.bitriseDataSource, TestApkMetadataMapper(dataDi.configDataSource))
    }

    val workflowConfirmationInteractor: WorkflowConfirmationInteractor by lazy {
        WorkflowConfirmationInteractor(
            dataDi.configDataSource,
            FlankDataMapper(),
            FlankConfigMapper(dataDi.configDataSource)
        )
    }

    val workflowTriggerInteractor: WorkflowTriggerInteractor by lazy {
        WorkflowTriggerInteractor(dataDi.bitriseDataSource, ConfirmDataMapper())
    }
}
