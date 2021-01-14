package net.lachlanmckee.flankci.presentation

import io.ktor.application.*
import io.ktor.client.engine.apache.*
import net.lachlanmckee.flankci.core.data.CoreIoModule
import net.lachlanmckee.flankci.core.data.FileReader
import net.lachlanmckee.flankci.core.data.HttpClientFactory
import net.lachlanmckee.flankci.core.startApplication
import java.io.BufferedReader
import java.io.FileInputStream

object ProductionHttpClientFactory : HttpClientFactory {
  override val engineFactory = Apache
}

object ProductionFileReader : FileReader {
  override fun read(name: String): BufferedReader {
    return FileInputStream(name).bufferedReader()
  }
}

// Referenced in application.conf
fun Application.main() {
  val applicationComponent: ApplicationComponent = DaggerApplicationComponent
    .builder()
    .coreIoModule(CoreIoModule(ProductionHttpClientFactory, ProductionFileReader))
    .build()

  startApplication(
    applicationComponent.typeAdapterFactories(),
    applicationComponent.routeProviders()
  )
}
