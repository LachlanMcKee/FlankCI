package net.lachlanmckee.flankci.runner.data

import dagger.Binds
import dagger.Module
import net.lachlanmckee.flankci.runner.data.datasource.remote.ApkDataSource
import net.lachlanmckee.flankci.runner.data.datasource.remote.ApkDataSourceImpl
import javax.inject.Singleton

@Module
internal abstract class TestRunnerDataModule {
  @Binds
  @Singleton
  abstract fun bindApkDataSource(impl: ApkDataSourceImpl): ApkDataSource
}
