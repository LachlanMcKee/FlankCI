package net.lachlanmckee.bitrise.core.domain

import net.lachlanmckee.bitrise.core.data.CoreDataDi
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.domain.ktor.MultipartCallFactory
import net.lachlanmckee.bitrise.core.domain.ktor.MultipartCallFactoryImpl
import net.lachlanmckee.bitrise.core.domain.mapper.FormDataCollector
import net.lachlanmckee.bitrise.core.domain.mapper.FormDataCollectorImpl
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory

object CoreDomainDi {
    val configDataSource: ConfigDataSource by lazy {
        CoreDataDi.configDataSource
    }

    val multipartCallFactory: MultipartCallFactory by lazy {
        MultipartCallFactoryImpl()
    }

    val errorScreenFactory: ErrorScreenFactory by lazy {
        ErrorScreenFactory()
    }

    val formDataCollector: FormDataCollector by lazy {
        FormDataCollectorImpl()
    }
}
