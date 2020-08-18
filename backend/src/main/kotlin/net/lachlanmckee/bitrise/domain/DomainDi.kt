package net.lachlanmckee.bitrise.domain

import net.lachlanmckee.bitrise.data.DataDi
import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.domain.interactor.*
import net.lachlanmckee.bitrise.domain.ktor.MultipartCallFactory
import net.lachlanmckee.bitrise.domain.ktor.MultipartCallFactoryImpl
import net.lachlanmckee.bitrise.domain.mapper.*
import net.lachlanmckee.bitrise.domain.validation.GeneratedFlankConfigValidator

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

    private val multipartCallFactory: MultipartCallFactory by lazy {
        MultipartCallFactoryImpl()
    }

    private val formDataCollector: FormDataCollector by lazy {
        FormDataCollectorImpl()
    }

    val workflowConfirmationInteractor: WorkflowConfirmationInteractor by lazy {
        WorkflowConfirmationInteractor(
            multipartCallFactory,
            GeneratedFlankConfigValidator(dataDi.configDataSource),
            FlankDataMapper(formDataCollector),
            FlankConfigMapper(dataDi.configDataSource)
        )
    }

    val workflowTriggerInteractor: WorkflowTriggerInteractor by lazy {
        WorkflowTriggerInteractor(
            multipartCallFactory,
            dataDi.bitriseDataSource,
            ConfirmDataMapper(formDataCollector)
        )
    }
}
