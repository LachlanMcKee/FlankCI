package net.lachlanmckee.bitrise.presentation

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
import java.text.DateFormat

// Referenced in application.conf
@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val applicationComponent: ApplicationComponent = DaggerApplicationComponent.create()

    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            applicationComponent
                .typeAdapterFactories()
                .forEach {
                    registerTypeAdapterFactory(it)
                }
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
            setLenient()
        }
    }

    routing {
        get("/") {
            HomeScreen().respondHtml(call)
        }

        applicationComponent
            .routeProviders()
            .forEach {
                it.provideRoute().invoke(this)
            }

        static("/static") {
            resource("styles.css")
        }
    }
}
