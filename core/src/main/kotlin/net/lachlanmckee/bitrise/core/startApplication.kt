package net.lachlanmckee.bitrise.core

import com.google.gson.TypeAdapterFactory
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*
import net.lachlanmckee.bitrise.core.presentation.RouteProvider
import java.text.DateFormat

fun Application.startApplication(
  typeAdapterFactories: Set<@JvmSuppressWildcards TypeAdapterFactory>,
  routerProviders: Set<RouteProvider>
) {
  install(DefaultHeaders)
  install(Compression)
  install(CallLogging)
  install(ContentNegotiation) {
    gson {
      typeAdapterFactories
        .forEach {
          registerTypeAdapterFactory(it)
        }
      setDateFormat(DateFormat.LONG)
      setPrettyPrinting()
      setLenient()
    }
  }

  routing {
    routerProviders
      .forEach {
        it.provideRoute().invoke(this)
      }
  }
}
