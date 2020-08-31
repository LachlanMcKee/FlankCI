package net.lachlanmckee.bitrise.runner.domain

import net.lachlanmckee.bitrise.core.data.CoreDataDi
import net.lachlanmckee.bitrise.core.domain.CoreDomainDi
import net.lachlanmckee.bitrise.core.domain.mapper.BuildsMapper
import net.lachlanmckee.bitrise.runner.data.TestRunnerDataDi
import net.lachlanmckee.bitrise.runner.domain.interactor.*
import net.lachlanmckee.bitrise.runner.domain.mapper.ConfirmDataMapper
import net.lachlanmckee.bitrise.runner.domain.mapper.FlankConfigMapper
import net.lachlanmckee.bitrise.runner.domain.mapper.FlankDataMapper
import net.lachlanmckee.bitrise.runner.domain.mapper.TestApkMetadataMapper
import net.lachlanmckee.bitrise.runner.domain.validation.GeneratedFlankConfigValidator

internal object TestRunnerDomainDi {
    val triggerBranchesInteractor: TriggerBranchesInteractor by lazy {
        TriggerBranchesInteractor(
            CoreDataDi.bitriseDataSource, CoreDataDi.configDataSource,
            BuildsMapper()
        )
    }

    val artifactsInteractor: ArtifactsInteractor by lazy {
        ArtifactsInteractor(CoreDataDi.bitriseDataSource)
    }

    val testApkMetadataInteractor: TestApkMetadataInteractor by lazy {
        TestApkMetadataInteractor(
            CoreDataDi.bitriseDataSource,
            TestRunnerDataDi.apkDataSource,
            TestApkMetadataMapper(CoreDataDi.configDataSource)
        )
    }

    val workflowConfirmationInteractor: WorkflowConfirmationInteractor by lazy {
        WorkflowConfirmationInteractor(
            CoreDomainDi.multipartCallFactory,
            CoreDomainDi.errorScreenFactory,
            GeneratedFlankConfigValidator(CoreDataDi.configDataSource),
            FlankDataMapper(CoreDomainDi.formDataCollector),
            FlankConfigMapper(CoreDataDi.configDataSource)
        )
    }

    val workflowTriggerInteractor: WorkflowTriggerInteractor by lazy {
        WorkflowTriggerInteractor(
            CoreDomainDi.multipartCallFactory,
            CoreDomainDi.errorScreenFactory,
            CoreDataDi.bitriseDataSource,
            ConfirmDataMapper(CoreDomainDi.formDataCollector)
        )
    }
}
