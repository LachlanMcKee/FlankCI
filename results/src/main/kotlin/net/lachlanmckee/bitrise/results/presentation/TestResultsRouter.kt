package net.lachlanmckee.bitrise.results.presentation

import io.ktor.application.call
import io.ktor.routing.Routing
import io.ktor.routing.get
import net.lachlanmckee.bitrise.core.domain.CoreDomainDi
import net.lachlanmckee.bitrise.results.domain.TestResultsDomainDi

object TestResultsRouter {
    fun setupRoutes(): Routing.() -> Unit = {
        get("/test-results") {
            TestResultsListScreen(
                TestResultsDomainDi.testResultsListInteractor,
                CoreDomainDi.errorScreenFactory
            ).respondHtml(call)
        }
        get("/test-results/{build-slug}") {
            val buildSlug: String = call.parameters["build-slug"]!!
            TestResultScreen(
                TestResultsDomainDi.testResultInteractor,
                CoreDomainDi.errorScreenFactory
            ).respondHtml(call, buildSlug)
        }
    }
}
