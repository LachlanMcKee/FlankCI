package net.lachlanmckee.bitrise.results.presentation

import kotlinx.html.HtmlBlockTag
import kotlinx.html.a
import kotlinx.html.classes

fun HtmlBlockTag.button(label: String, url: String, gray: Boolean = true) {
  a(href = url) {
    classes = mutableSetOf("mdl-button mdl-button--colored", "mdl-js-button", "mdl-js-ripple-effect")
      .apply {
        if (gray) {
          add("gray-button")
        }
      }
    target = "_blank"
    text(label)
  }
  text(" ")
}
