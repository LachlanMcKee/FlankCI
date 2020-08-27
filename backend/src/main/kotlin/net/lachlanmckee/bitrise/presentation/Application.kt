package net.lachlanmckee.bitrise.presentation

import gsonpath.GsonPath
import gsonpath.GsonPathTypeAdapterFactory
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import net.lachlanmckee.bitrise.data.serialization.BitriseGsonTypeFactory
import net.lachlanmckee.bitrise.domain.DomainDi
import java.text.DateFormat

// Referenced in application.conf
@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            registerTypeAdapterFactory(GsonPathTypeAdapterFactory())
            registerTypeAdapterFactory(
                GsonPath.createTypeAdapterFactory(BitriseGsonTypeFactory::class.java)
            )
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
            setLenient()
        }
    }

    val domainDi = DomainDi()

    routing {
        get("/") {
            HomeScreen().respondHtml(call)
        }
        get("/test-runner") {
            TestRunnerScreen(domainDi.configDataSource).respondHtml(call)
        }
        get("/test-results") {
            TestResultsScreen().respondHtml(call)
        }
        get("/bitrise-data") {
            domainDi.branchesInteractor.execute(call)
        }
        get("/artifact-data/{build-slug}") {
            val buildSlug: String = call.parameters["build-slug"]!!
            domainDi.artifactsInteractor.execute(call, buildSlug)
        }
        get("/test-apk-metadata/{build-slug}/{artifact-slug}") {
            val buildSlug: String = call.parameters["build-slug"]!!
            val artifactSlug: String = call.parameters["artifact-slug"]!!

            domainDi
                .testApkMetadataInteractor
                .execute(call, buildSlug, artifactSlug)
        }
        post("/trigger-tests") {
            domainDi.workflowConfirmationInteractor.execute(call)
        }
        post("/confirm-test-trigger") {
            domainDi.workflowTriggerInteractor.execute(call)
        }
        static("/static") {
            resource("script.js")
            resource("styles.css")
        }
    }
}
