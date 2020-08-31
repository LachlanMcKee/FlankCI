package net.lachlanmckee.bitrise.core.presentation

import io.ktor.routing.Routing

interface RouteProvider {
    fun provideRoute(): Routing.() -> Unit
}
