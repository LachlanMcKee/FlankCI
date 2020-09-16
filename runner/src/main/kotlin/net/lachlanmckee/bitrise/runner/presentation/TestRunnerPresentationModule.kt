package net.lachlanmckee.bitrise.runner.presentation

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.application.call
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.routing.get
import io.ktor.routing.post
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.presentation.RouteProvider
import net.lachlanmckee.bitrise.runner.domain.TestRunnerDomainModule
import net.lachlanmckee.bitrise.runner.domain.interactor.*
import javax.inject.Singleton

@Module(includes = [TestRunnerDomainModule::class])
object TestRunnerPresentationModule {
    @Provides
    @Singleton
    @IntoSet
    internal fun provideJavascriptRouteProvider(): RouteProvider = RouteProvider {
        {
            static("/static") {
                resource("test-runner-script.js")
            }
        }
    }

    @Provides
    @Singleton
    @IntoSet
    internal fun provideTestRunnerRouteProvider(
        configDataSource: ConfigDataSource
    ): RouteProvider = RouteProvider {
        {
            get("/test-runner") {
                TestRunnerScreen(configDataSource)
                    .respondHtml(call)
            }
        }
    }

    @Provides
    @Singleton
    @IntoSet
    internal fun provideBitriseDataRouteProvider(
        triggerBranchesInteractor: TriggerBranchesInteractor
    ): RouteProvider = RouteProvider {
        {
            get("/bitrise-data") {
                triggerBranchesInteractor.execute(call)
            }
        }
    }

    @Provides
    @Singleton
    @IntoSet
    internal fun provideArtifactDataRouteProvider(
        artifactsInteractor: ArtifactsInteractor
    ): RouteProvider = RouteProvider {
        {
            get("/artifact-data/{build-slug}") {
                val buildSlug: String = call.parameters["build-slug"]!!
                artifactsInteractor.execute(call, buildSlug)
            }
        }
    }

    @Provides
    @Singleton
    @IntoSet
    internal fun provideTestApkMetadataRouteProvider(
        testApkMetadataInteractor: TestApkMetadataInteractor
    ): RouteProvider = RouteProvider {
        {
            get("/test-apk-metadata/{build-slug}/{artifact-slug}") {
                val buildSlug: String = call.parameters["build-slug"]!!
                val artifactSlug: String = call.parameters["artifact-slug"]!!

                testApkMetadataInteractor
                    .execute(call, buildSlug, artifactSlug)
            }
        }
    }

    @Provides
    @Singleton
    @IntoSet
    internal fun provideTriggerTestsRouteProvider(
        workflowConfirmationInteractor: WorkflowConfirmationInteractor
    ): RouteProvider = RouteProvider {
        {
            post("/trigger-tests") {
                workflowConfirmationInteractor.execute(call)
            }
        }
    }

    @Provides
    @Singleton
    @IntoSet
    internal fun provideConfirmTestTriggerRouteProvider(
        workflowTriggerInteractor: WorkflowTriggerInteractor
    ): RouteProvider = RouteProvider {
        {
            post("/confirm-test-trigger") {
                workflowTriggerInteractor.execute(call)
            }
        }
    }
}
