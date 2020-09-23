package net.lachlanmckee.bitrise.runner.presentation

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import net.lachlanmckee.bitrise.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.bitrise.core.presentation.ErrorScreenFactory
import net.lachlanmckee.bitrise.core.presentation.RouteProvider
import net.lachlanmckee.bitrise.runner.domain.TestRunnerDomainModule
import net.lachlanmckee.bitrise.runner.domain.interactor.*
import javax.inject.Singleton

@Module(includes = [TestRunnerDomainModule::class])
object TestRunnerPresentationModule {
    @Provides
    @Singleton
    @IntoSet
    internal fun provideJavascriptRouteProvider(): RouteProvider = object : RouteProvider {
        override fun provideRoute(): Routing.() -> Unit = {
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
    ): RouteProvider = object : RouteProvider {
        override fun provideRoute(): Routing.() -> Unit = {
            get("/test-runner") {
                TestRunnerScreen(configDataSource)
                    .respondHtml(call)
            }
        }
    }

    @Provides
    @Singleton
    @IntoSet
    internal fun provideTestRerunRouteProvider(
        configDataSource: ConfigDataSource,
        errorScreenFactory: ErrorScreenFactory,
        testRerunInteractor: TestRerunInteractor
    ): RouteProvider = object : RouteProvider {
        override fun provideRoute(): Routing.() -> Unit = {
            get("/test-rerun") {
                val buildSlug: String = call.parameters["build-slug"]!!

                TestRerunScreen(configDataSource, errorScreenFactory, testRerunInteractor)
                    .respondHtml(call, buildSlug)
            }
        }
    }

    @Provides
    @Singleton
    @IntoSet
    internal fun provideBitriseDataRouteProvider(
        triggerBranchesInteractor: TriggerBranchesInteractor
    ): RouteProvider = object : RouteProvider {
        override fun provideRoute(): Routing.() -> Unit = {
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
    ): RouteProvider = object : RouteProvider {
        override fun provideRoute(): Routing.() -> Unit = {
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
    ): RouteProvider = object : RouteProvider {
        override fun provideRoute(): Routing.() -> Unit = {
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
    ): RouteProvider = object : RouteProvider {
        override fun provideRoute(): Routing.() -> Unit = {
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
    ): RouteProvider = object : RouteProvider {
        override fun provideRoute(): Routing.() -> Unit = {
            post("/confirm-test-trigger") {
                workflowTriggerInteractor.execute(call)
            }
        }
    }
}
