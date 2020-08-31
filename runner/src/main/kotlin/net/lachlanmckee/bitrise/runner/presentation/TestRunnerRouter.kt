package net.lachlanmckee.bitrise.runner.presentation

import io.ktor.application.call
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import net.lachlanmckee.bitrise.core.domain.CoreDomainDi
import net.lachlanmckee.bitrise.runner.domain.TestRunnerDomainDi

object TestRunnerRouter {
    fun setupRoutes(): Routing.() -> Unit = {
        get("/test-runner") {
            TestRunnerScreen(CoreDomainDi.configDataSource)
                .respondHtml(call)
        }
        get("/bitrise-data") {
            TestRunnerDomainDi.triggerBranchesInteractor.execute(call)
        }
        get("/artifact-data/{build-slug}") {
            val buildSlug: String = call.parameters["build-slug"]!!
            TestRunnerDomainDi.artifactsInteractor.execute(call, buildSlug)
        }
        get("/test-apk-metadata/{build-slug}/{artifact-slug}") {
            val buildSlug: String = call.parameters["build-slug"]!!
            val artifactSlug: String = call.parameters["artifact-slug"]!!

            TestRunnerDomainDi
                .testApkMetadataInteractor
                .execute(call, buildSlug, artifactSlug)
        }
        post("/trigger-tests") {
            TestRunnerDomainDi.workflowConfirmationInteractor.execute(call)
        }
        post("/confirm-test-trigger") {
            TestRunnerDomainDi.workflowTriggerInteractor.execute(call)
        }
        static("/static") {
            resource("test-runner-script.js")
        }
    }
}
