package net.lachlanmckee.bitrise.presentation

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.apache.*
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.content.resource
import io.ktor.http.content.static
import io.ktor.routing.get
import io.ktor.routing.routing
import net.lachlanmckee.bitrise.core.data.CoreHttpModule
import net.lachlanmckee.bitrise.core.data.HttpClientFactory
import java.text.DateFormat

object ProductionHttpClientFactory : HttpClientFactory {
  override val engineFactory = Apache
}

// Referenced in application.conf
fun Application.main(httpClientFactory: HttpClientFactory = ProductionHttpClientFactory) {
  val applicationComponent: ApplicationComponent = DaggerApplicationComponent
    .builder()
    .coreHttpModule(CoreHttpModule(httpClientFactory))
    .build()

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
