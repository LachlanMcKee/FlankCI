package net.lachlanmckee.flankci.core.presentation

import dagger.Binds
import dagger.Module
import net.lachlanmckee.flankci.core.domain.CoreDomainModule
import javax.inject.Singleton

@Module(includes = [CoreDomainModule::class])
abstract class CorePresentationModule {
  @Binds
  @Singleton
  internal abstract fun bindErrorScreenFactory(impl: ErrorScreenFactoryImpl): ErrorScreenFactory
}
