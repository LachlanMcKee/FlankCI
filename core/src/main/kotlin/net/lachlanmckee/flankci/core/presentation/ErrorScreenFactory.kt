package net.lachlanmckee.flankci.core.presentation

import io.ktor.application.ApplicationCall

interface ErrorScreenFactory {
  suspend fun respondHtml(call: ApplicationCall, title: String, body: String)
}
