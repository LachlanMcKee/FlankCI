package net.lachlanmckee.bitrise.core.presentation

import dagger.Module
import net.lachlanmckee.bitrise.core.domain.CoreDomainModule

@Module(includes = [CoreDomainModule::class])
abstract class CorePresentationModule
