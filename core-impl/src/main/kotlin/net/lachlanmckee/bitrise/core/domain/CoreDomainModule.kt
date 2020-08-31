package net.lachlanmckee.bitrise.core.domain

import dagger.Binds
import dagger.Module
import net.lachlanmckee.bitrise.core.data.CoreDataModule
import net.lachlanmckee.bitrise.core.domain.ktor.MultipartCallFactory
import net.lachlanmckee.bitrise.core.domain.ktor.MultipartCallFactoryImpl
import net.lachlanmckee.bitrise.core.domain.mapper.BuildsMapper
import net.lachlanmckee.bitrise.core.domain.mapper.BuildsMapperImpl
import net.lachlanmckee.bitrise.core.domain.mapper.FormDataCollector
import net.lachlanmckee.bitrise.core.domain.mapper.FormDataCollectorImpl
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactoryImpl
import javax.inject.Singleton

@Module(includes = [CoreDataModule::class])
internal abstract class CoreDomainModule {
    @Binds
    @Singleton
    internal abstract fun bindMultipartCallFactory(impl: MultipartCallFactoryImpl): MultipartCallFactory

    @Binds
    @Singleton
    internal abstract fun bindErrorScreenFactory(impl: ErrorScreenFactoryImpl): ErrorScreenFactory

    @Binds
    @Singleton
    internal abstract fun bindFormDataCollector(impl: FormDataCollectorImpl): FormDataCollector

    @Binds
    @Singleton
    internal abstract fun bindBuildsMapper(impl: BuildsMapperImpl): BuildsMapper
}
