package net.lachlanmckee.flankci.runner.domain

import dagger.Module
import net.lachlanmckee.flankci.runner.data.TestRunnerDataModule

@Module(includes = [TestRunnerDataModule::class])
internal abstract class TestRunnerDomainModule
