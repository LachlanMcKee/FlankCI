package net.lachlanmckee.bitrise.presentation

import gsonpath.GsonPath
import gsonpath.GsonPathTypeAdapterFactoryKt
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
import io.ktor.routing.routing
import net.lachlanmckee.bitrise.core.data.serialization.BitriseGsonTypeFactory
import net.lachlanmckee.bitrise.results.presentation.TestResultsRouter
import net.lachlanmckee.bitrise.runner.presentation.TestRunnerRouter
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
            registerTypeAdapterFactory(GsonPathTypeAdapterFactoryKt())
            registerTypeAdapterFactory(
                GsonPath.createTypeAdapterFactory(BitriseGsonTypeFactory::class.java)
            )
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
            setLenient()
        }
    }

    routing {
        get("/") {
            HomeScreen().respondHtml(call)
        }
        TestRunnerRouter.setupRoutes().invoke(this)
        TestResultsRouter.setupRoutes().invoke(this)
        static("/static") {
            resource("styles.css")
        }
    }
}
