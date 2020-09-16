package net.lachlanmckee.bitrise.core.presentation

import io.ktor.routing.Routing

fun interface RouteProvider {
    fun provideRoute(): Routing.() -> Unit
}
