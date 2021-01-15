package net.lachlanmckee.flankci.presentation

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*
import net.lachlanmckee.flankci.core.data.datasource.local.ConfigDataSource
import net.lachlanmckee.flankci.core.presentation.RouteProvider
import javax.inject.Singleton

@Module
object BackendPresentationModule {
  @Provides
  @Singleton
  @IntoSet
  internal fun provideHomeRouteProvider(
    configDataSource: ConfigDataSource
  ): RouteProvider = object : RouteProvider {
    override fun provideRoute(): Routing.() -> Unit = {
      get("/") {
        val homeModel = HomeModel(
          configDataSource.getConfig().configurations.map { HomeModel.CiConfiguration(it.id, it.displayName) }
        )
        HomeScreen().respondHtml(call, homeModel)
      }
    }
  }

  @Provides
  @Singleton
  @IntoSet
  internal fun provideStylesRouteProvider(): RouteProvider = object : RouteProvider {
    override fun provideRoute(): Routing.() -> Unit = {
      static("/static") {
        resource("styles.css")
      }
    }
  }
}
