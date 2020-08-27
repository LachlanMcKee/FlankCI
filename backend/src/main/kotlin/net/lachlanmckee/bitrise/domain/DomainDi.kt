package net.lachlanmckee.bitrise.domain

import net.lachlanmckee.bitrise.data.DataDi
import net.lachlanmckee.bitrise.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.domain.interactor.*
import net.lachlanmckee.bitrise.domain.ktor.MultipartCallFactory
import net.lachlanmckee.bitrise.domain.ktor.MultipartCallFactoryImpl
import net.lachlanmckee.bitrise.domain.mapper.*
import net.lachlanmckee.bitrise.domain.validation.GeneratedFlankConfigValidator
import net.lachlanmckee.bitrise.presentation.ErrorScreenFactory

class DomainDi {
    private val dataDi = DataDi()

    val triggerBranchesInteractor: TriggerBranchesInteractor by lazy {
        TriggerBranchesInteractor(dataDi.bitriseDataSource, configDataSource, BuildsMapper())
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

    private val errorScreenFactory: ErrorScreenFactory by lazy {
        ErrorScreenFactory()
    }

    private val formDataCollector: FormDataCollector by lazy {
        FormDataCollectorImpl()
    }

    val workflowConfirmationInteractor: WorkflowConfirmationInteractor by lazy {
        WorkflowConfirmationInteractor(
            multipartCallFactory,
            errorScreenFactory,
            GeneratedFlankConfigValidator(dataDi.configDataSource),
            FlankDataMapper(formDataCollector),
            FlankConfigMapper(dataDi.configDataSource)
        )
    }

    val workflowTriggerInteractor: WorkflowTriggerInteractor by lazy {
        WorkflowTriggerInteractor(
            multipartCallFactory,
            errorScreenFactory,
            dataDi.bitriseDataSource,
            ConfirmDataMapper(formDataCollector)
        )
    }
}
