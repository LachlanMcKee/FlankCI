package net.lachlanmckee.bitrise.runner.data

import net.lachlanmckee.bitrise.core.data.CoreDataDi
import net.lachlanmckee.bitrise.runner.data.datasource.remote.ApkDataSource
import net.lachlanmckee.bitrise.runner.data.datasource.remote.ApkDataSourceImpl

internal object TestRunnerDataDi {
    val apkDataSource: ApkDataSource by lazy {
        ApkDataSourceImpl(
            bitriseService = CoreDataDi.bitriseService
        )
    }
}
