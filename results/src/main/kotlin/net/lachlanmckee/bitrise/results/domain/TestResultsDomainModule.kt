package net.lachlanmckee.bitrise.results.domain

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dagger.Module
import dagger.Provides
import net.lachlanmckee.bitrise.results.domain.mapper.TestSuitesMapper
import javax.inject.Singleton

@Module
internal object TestResultsDomainModule {
    @Provides
    @Singleton
    internal fun provideTestSuitesMapper(): TestSuitesMapper {
        return TestSuitesMapper(
            xmlMapper = XmlMapper.Builder(XmlMapper())
                .defaultUseWrapper(false)
                .build()
                .registerKotlinModule()
        )
    }
}
