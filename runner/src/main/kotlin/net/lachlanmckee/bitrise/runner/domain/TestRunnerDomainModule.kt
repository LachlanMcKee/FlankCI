package net.lachlanmckee.bitrise.runner.domain

import dagger.Module
import net.lachlanmckee.bitrise.runner.data.TestRunnerDataModule

@Module(includes = [TestRunnerDataModule::class])
internal abstract class TestRunnerDomainModule
