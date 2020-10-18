package net.lachlanmckee.bitrise.results.presentation

import kotlinx.html.BODY
import kotlinx.html.a
import kotlinx.html.classes

fun BODY.button(label: String, url: String) {
  a(href = url) {
    classes = setOf("mdl-button mdl-button--colored", "mdl-js-button", "mdl-js-ripple-effect", "gray-button")
    target = "_blank"
    text(label)
  }
  text(" ")
}
